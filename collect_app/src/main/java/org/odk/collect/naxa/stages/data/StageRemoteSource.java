package org.odk.collect.naxa.stages.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseRemoteDataSource;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.database.FieldSightConfigDatabase;
import org.odk.collect.naxa.common.database.SiteOveride;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.network.ApiInterface;
import org.odk.collect.naxa.network.ServiceGenerator;
import org.odk.collect.naxa.onboarding.XMLForm;
import org.odk.collect.naxa.onboarding.XMLFormBuilder;
import org.odk.collect.naxa.project.data.ProjectLocalSource;
import org.odk.collect.naxa.substages.data.SubStageLocalSource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class StageRemoteSource implements BaseRemoteDataSource<Stage> {

    private static StageRemoteSource INSTANCE;

    public static StageRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StageRemoteSource();
        }
        return INSTANCE;
    }

    public StageRemoteSource() {
    }


    @Override
    public void getAll() {
        Observable<List<XMLForm>> siteODKForms = FieldSightConfigDatabase
                .getDatabase(Collect.getInstance())
                .getSiteOverideDAO()
                .getAll()
                .map((Function<SiteOveride, LinkedList<String>>) siteOveride -> {
                    Type type = new TypeToken<LinkedList<String>>() {
                    }.getType();//todo use typeconvertor
                    return new Gson().fromJson(siteOveride.getStagedFormIds(), type);
                }).flattenAsObservable((Function<LinkedList<String>, Iterable<String>>) siteIds -> siteIds)
                .map(siteId -> new XMLFormBuilder()
                        .setFormCreatorsId(siteId)
                        .setIsCreatedFromProject(false)
                        .createXMLForm())
                .toList()
                .toObservable();

        Observable<List<XMLForm>> projectODKForms = ProjectLocalSource.getInstance().getProjectsMaybe()
                .flattenAsObservable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .map(project -> new XMLFormBuilder()
                        .setFormCreatorsId(project.getId())
                        .setIsCreatedFromProject(true)
                        .createXMLForm())
                .toList()
                .toObservable();


        Observable.merge(siteODKForms, projectODKForms)
                .flatMapIterable((Function<List<XMLForm>, Iterable<XMLForm>>) xmlForms -> xmlForms)
                .flatMap((Function<XMLForm, ObservableSource<ArrayList<Stage>>>) this::downloadStages)
                .toList()
                .map(listOfStages -> {
                    ArrayList<Stage> stagesList = new ArrayList<>(0);
                    ArrayList<SubStage> subStageList = new ArrayList<>(0);

                    for (ArrayList<Stage> stages : listOfStages) {
                        stagesList.addAll(stages);
                    }

                    for (Stage stage : stagesList) {
                        subStageList.addAll(stage.getSubStage());
                    }

                    StageLocalSource.getInstance().save(stagesList);
                    SubStageLocalSource.getInstance().save(subStageList);

                    return stagesList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ArrayList<Stage>>() {
                    @Override
                    public void onSuccess(ArrayList<Stage> stages) {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.STAGED_FORMS, DataSyncEvent.EventStatus.EVENT_END));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.STAGED_FORMS, DataSyncEvent.EventStatus.EVENT_ERROR));

                    }
                });
    }


    private Observable<ArrayList<Stage>> downloadStages(XMLForm xmlForm) {

        String createdFromProject = XMLForm.toNumeralString(xmlForm.isCreatedFromProject());
        String creatorsId = xmlForm.getFormCreatorsId();

        return ServiceGenerator
                .getRxClient()
                .create(ApiInterface.class)
                .getStageSubStage(createdFromProject, creatorsId)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                if (throwable instanceof IOException) {
                                    return throwableObservable;
                                }

                                return Observable.error(throwable);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}