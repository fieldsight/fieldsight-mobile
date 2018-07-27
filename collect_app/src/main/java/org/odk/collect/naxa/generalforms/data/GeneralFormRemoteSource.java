package org.odk.collect.naxa.generalforms.data;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
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
import org.odk.collect.naxa.project.db.ProjectRepository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;

public class GeneralFormRemoteSource implements BaseRemoteDataSource<GeneralForm> {

    private static GeneralFormRemoteSource INSTANCE;
    private ProjectRepository projectRepository;

    public static GeneralFormRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeneralFormRemoteSource();
        }
        return INSTANCE;
    }


    public GeneralFormRemoteSource() {
        this.projectRepository = new ProjectRepository();
    }


    private Observable<ArrayList<GeneralForm>> downloadGeneralForm(XMLForm xmlForm) {
        String createdFromProject = XMLForm.toNumeralString(xmlForm.isCreatedFromProject());
        String creatorsId = xmlForm.getFormCreatorsId();

        return ServiceGenerator
                .getRxClient()
                .create(ApiInterface.class)
                .getGeneralFormsObservable(createdFromProject, creatorsId)
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

    @Override
    public void getAll() {
        Observable<List<XMLForm>> siteODKForms = FieldSightConfigDatabase
                .getDatabase(Collect.getInstance())
                .getSiteOverideDAO()
                .getAll()
                .map((Function<SiteOveride, LinkedList<String>>) siteOveride -> {
                    Type type = new TypeToken<LinkedList<String>>() {
                    }.getType();//todo use typeconvertor
                    return new Gson().fromJson(siteOveride.getGeneralFormIds(), type);
                }).flattenAsObservable((Function<LinkedList<String>, Iterable<String>>) siteIds -> siteIds)
                .map(siteId -> new XMLFormBuilder()
                        .setFormCreatorsId(siteId)
                        .setIsCreatedFromProject(false)
                        .createXMLForm())
                .toList()
                .toObservable();


        Observable<List<XMLForm>> projectODKForms = projectRepository
                .getAllProjectsMaybe()
                .flattenAsObservable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .map(project -> new XMLFormBuilder()
                        .setFormCreatorsId(project.getId())
                        .setIsCreatedFromProject(true)
                        .createXMLForm())
                .toList()
                .toObservable();


        Observable.merge(siteODKForms, projectODKForms)

                .flatMapIterable((Function<List<XMLForm>, Iterable<XMLForm>>) xmlForms -> xmlForms)
                .flatMap((Function<XMLForm, ObservableSource<ArrayList<GeneralForm>>>) this::downloadGeneralForm)
                .map(generalForms -> {
                    for (GeneralForm generalForm : generalForms) {
                        String deployedFrom = generalForm.getProject() != null ? Constant.FormDeploymentFrom.PROJECT : Constant.FormDeploymentFrom.SITE;
                        generalForm.setFormDeployedFrom(deployedFrom);
                    }

                    return generalForms;
                })
                .toList()
                .map((List<ArrayList<GeneralForm>> arrayLists) -> {
                    ArrayList<GeneralForm> generalForms = new ArrayList<>(0);

                    for (ArrayList<GeneralForm> generalFormList : arrayLists) {
                        generalForms.addAll(generalFormList);
                    }
                    GeneralFormLocalSource.getInstance().updateAll(generalForms);
                    return generalForms;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ArrayList<GeneralForm>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.GENERAL_FORMS, EVENT_START));
                    }

                    @Override
                    public void onSuccess(ArrayList<GeneralForm> generalForms) {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.GENERAL_FORMS, EVENT_END));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.GENERAL_FORMS, EVENT_ERROR));

                    }
                });

    }

    @Override
    public void getById(GeneralForm... items) {

    }
}
