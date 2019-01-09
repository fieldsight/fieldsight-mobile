package org.bcss.collect.naxa.scheduled.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import org.bcss.collect.naxa.sync.SyncRepository;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;

public class ScheduledFormsRemoteSource implements BaseRemoteDataSource<ScheduleForm> {

    private static ScheduledFormsRemoteSource INSTANCE;
    private ProjectLocalSource projectLocalSource;
    private SyncRepository syncRepository;


    public static ScheduledFormsRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScheduledFormsRemoteSource();
        }
        return INSTANCE;
    }

    private ScheduledFormsRemoteSource() {
        this.projectLocalSource = ProjectLocalSource.getInstance();
        this.syncRepository = SyncRepository.getInstance();
    }


    @Override
    public void getAll() {

        fetchAllScheduledForms()
                .subscribe(new SingleObserver<ArrayList<ScheduleForm>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.SCHEDULED_FORMS, EVENT_START));

                    }

                    @Override
                    public void onSuccess(ArrayList<ScheduleForm> scheduleForms) {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.SCHEDULED_FORMS, EVENT_END));
                        syncRepository.setSuccess(Constant.DownloadUID.SCHEDULED_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        syncRepository.setError(Constant.DownloadUID.SCHEDULED_FORMS);
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.SCHEDULED_FORMS, EVENT_ERROR));
                    }
                });



    }


    private Observable<ArrayList<ScheduleForm>> downloadProjectSchedule(XMLForm xmlForm) {

        String createdFromProject = XMLForm.toNumeralString(xmlForm.isCreatedFromProject());
        String creatorsId = xmlForm.getFormCreatorsId();
        return ServiceGenerator.getRxClient().create(ApiInterface.class)
                .getScheduleForms(createdFromProject, creatorsId)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) {
                                if (throwable instanceof IOException) {
                                    return throwableObservable;
                                }

                                return Observable.error(throwable);
                            }
                        });
                    }
                });

    }

    public Single<ArrayList<ScheduleForm>> fetchAllScheduledForms() {

        Observable<List<XMLForm>> siteODKForms = FieldSightConfigDatabase
                .getDatabase(Collect.getInstance())
                .getSiteOverideDAO()
                .getAll()
                .map((Function<SiteOveride, LinkedList<String>>) siteOveride -> {
                    Type type = new TypeToken<LinkedList<String>>() {
                    }.getType();//todo use typeconvertor
                    return new Gson().fromJson(siteOveride.getScheduleFormIds(), type);
                }).flattenAsObservable((Function<LinkedList<String>, Iterable<String>>) siteIds -> siteIds)
                .map(siteId -> new XMLFormBuilder()
                        .setFormCreatorsId(siteId)
                        .setIsCreatedFromProject(false)
                        .createXMLForm())
                .toList()
                .toObservable();


        Observable<List<XMLForm>> projectODKForms = projectLocalSource
                .getProjectsMaybe()
                .flattenAsObservable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .map(project -> new XMLFormBuilder()
                        .setFormCreatorsId(project.getId())
                        .setIsCreatedFromProject(true)
                        .createXMLForm())
                .toList()
                .toObservable();


        return Observable.merge(siteODKForms, projectODKForms)

                .flatMapIterable((Function<List<XMLForm>, Iterable<XMLForm>>) xmlForms -> xmlForms)
                .flatMap(new Function<XMLForm, Observable<ArrayList<ScheduleForm>>>() {
                    @Override
                    public Observable<ArrayList<ScheduleForm>> apply(XMLForm xmlForm) {
                        return downloadProjectSchedule(xmlForm);
                    }
                })
                .map(new Function<ArrayList<ScheduleForm>, ArrayList<ScheduleForm>>() {
                    @Override
                    public ArrayList<ScheduleForm> apply(ArrayList<ScheduleForm> scheduleForms) {
                        for (ScheduleForm scheduleForm : scheduleForms) {
                            String deployedFrom = scheduleForm.getProjectId() != null ? Constant.FormDeploymentFrom.PROJECT : Constant.FormDeploymentFrom.SITE;
                            scheduleForm.setFormDeployedFrom(deployedFrom);
                        }

                        return scheduleForms;

                    }
                })
                .toList()
                .map(new Function<List<ArrayList<ScheduleForm>>, ArrayList<ScheduleForm>>() {
                    @Override
                    public ArrayList<ScheduleForm> apply(List<ArrayList<ScheduleForm>> arrayLists) {
                        ArrayList<ScheduleForm> scheduleForms = new ArrayList<>(0);

                        for (ArrayList<ScheduleForm> scheduleFormsList : arrayLists) {
                            scheduleForms.addAll(scheduleFormsList);
                        }

                        ScheduledFormsLocalSource.getInstance().updateAll(scheduleForms);
                        return scheduleForms;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
