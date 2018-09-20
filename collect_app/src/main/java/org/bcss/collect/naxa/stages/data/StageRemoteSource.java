package org.bcss.collect.naxa.stages.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.database.FieldSightConfigDatabase;
import org.bcss.collect.naxa.common.database.SiteOveride;
import org.bcss.collect.naxa.common.event.DataSyncEvent;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.onboarding.XMLForm;
import org.bcss.collect.naxa.onboarding.XMLFormBuilder;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.substages.data.SubStageLocalSource;
import org.bcss.collect.naxa.sync.SyncRepository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class StageRemoteSource implements BaseRemoteDataSource<Stage> {

    private static StageRemoteSource INSTANCE;
    private SyncRepository syncRepository;


    public static StageRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StageRemoteSource();
        }
        return INSTANCE;
    }

    public StageRemoteSource() {
        this.syncRepository = SyncRepository.getInstance();
    }


    @Override
    public void getAll() {
        fetchAllStages()
                .subscribe(new DisposableSingleObserver<ArrayList<Stage>>() {
                    @Override
                    public void onSuccess(ArrayList<Stage> stages) {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.STAGED_FORMS, DataSyncEvent.EventStatus.EVENT_END));
                        syncRepository.setSuccess(Constant.DownloadUID.STAGED_FORMS);

                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.STAGED_FORMS, DataSyncEvent.EventStatus.EVENT_ERROR));
                        syncRepository.setSuccess(Constant.DownloadUID.STAGED_FORMS);
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


    public Single<ArrayList<Stage>> fetchAllStages() {
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


        return Observable.merge(siteODKForms, projectODKForms)
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
                        for(SubStage subStage: stage.getSubStage()){
                            subStage.setStageId(stage.getId());
                            subStage.setFsFormId(subStage.getStageForms().getId());
                            subStage.setJrFormId(subStage.getStageForms().getXf().getJrFormId());
                            subStageList.add(subStage);
                        }

                    }

                    StageLocalSource.getInstance().save(stagesList);
                    SubStageLocalSource.getInstance().save(subStageList);

                    return stagesList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
