package org.odk.collect.naxa.onboarding;

import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.generalforms.GeneralFormResponse;
import org.odk.collect.naxa.login.model.MeResponse;
import org.odk.collect.naxa.login.model.MySites;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.network.ApiInterface;
import org.odk.collect.naxa.network.ServiceGenerator;
import org.odk.collect.naxa.project.db.ProjectRepository;
import org.odk.collect.naxa.project.db.ProjectViewModel;
import org.odk.collect.naxa.site.db.SiteViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_UPDATE;

public class DownloadModelImpl implements DownloadModel {

    private SiteViewModel siteViewModel;
    private ProjectViewModel projectViewModel;
    private DownloadFormsTask downloadFormsTask;
    private HashMap<String, FormDetails> formNamesAndURLs;
    private ArrayList<HashMap<String, String>> formList;
    private DownloadFormListTask downloadFormListTask;

    public DownloadModelImpl() {
        this.siteViewModel = new SiteViewModel(Collect.getInstance());
        this.projectViewModel = new ProjectViewModel(Collect.getInstance());
        formList = new ArrayList<>();
    }

    @Override
    public void fetchGeneralForms() {
        new ProjectRepository(Collect.getInstance())
                .getAllProjects()
                .observeForever(projects -> {

                    ArrayList<String> projectIds = new ArrayList<>();
                    ArrayList<XMLForm> projectForms = new ArrayList<>();

                    for (Project project : projects) {

                        projectForms.add(new XMLFormBuilder()
                                .setFormCreatorsId(project.getId())
                                .setIsCreatedFromProject(true)
                                .createXMLForm());

                        downloadProjectGeneral(projectForms)
                                .subscribe(new DisposableObserver<Object>() {
                                    @Override
                                    public void onNext(Object o) {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    }


                });
    }


    private Observable<Object> downloadProjectGeneral(ArrayList<XMLForm> projectForms) {
        return Observable.just(projectForms)
                .flatMapIterable((Function<ArrayList<XMLForm>, Iterable<XMLForm>>) forms -> forms)
                .flatMap((Function<XMLForm, Observable<Object>>) xmlForm -> {

                    String createdFromProject = XMLForm.toNumeralString(xmlForm.isCreatedFromProject());
                    String creatorsId = xmlForm.getFormCreatorsId();

                    Observable<ArrayList<GeneralFormResponse>> generalFormsObservable = ServiceGenerator
                            .getRxClient()
                            .create(ApiInterface.class)
                            .getGeneralFormsObservable(createdFromProject, creatorsId);

                    return generalFormsObservable
                            .flatMap(new Function<ArrayList<GeneralFormResponse>, ObservableSource<?>>() {
                                @Override
                                public ObservableSource<?> apply(ArrayList<GeneralFormResponse> generalFormResponses) throws Exception {
                                    return Observable.empty();
                                }
                            });
                })
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
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void fetchProjectSites() {
        ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getUserInformation()
                .flatMap((Function<MeResponse, ObservableSource<List<MySites>>>) meResponse -> Observable.just(meResponse.getData().getMySitesModel()))
                .flatMapIterable((Function<List<MySites>, Iterable<MySites>>) mySites -> mySites)
                .map(mySites -> {
                    siteViewModel.insert(mySites.getSite());
                    projectViewModel.insert(mySites.getProject());
                    return mySites.getProject();
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObservable(Constant.DownloadUID.PROJECT_SITES));
    }

    @Override
    public void fetchODKForms() {
        int uid = Constant.DownloadUID.ODK_FORMS;

        XMLFormDownloadReceiver xmlFormDownloadReceiver = new XMLFormDownloadReceiver(new Handler());
        xmlFormDownloadReceiver.setReceiver((resultCode, resultData) -> {
            switch (resultCode) {
                case DownloadProgress.STATUS_RUNNING:
                    EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_START));
                    break;
                case DownloadProgress.STATUS_PROGRESS_UPDATE:
                    DownloadProgress progress = (DownloadProgress) resultData.getSerializable(EXTRA_OBJECT);
                    Timber.i(progress.getMessage());
                    EventBus.getDefault().post(new DataSyncEvent(uid, progress));
                    break;
                case DownloadProgress.STATUS_ERROR:
                    EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_ERROR));
                    break;
                case DownloadProgress.STATUS_FINISHED_FORM:
                    EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_END));
                    break;
            }
        });
        XMLFormDownloadService.start(Collect.getInstance(), xmlFormDownloadReceiver);

    }


    private SingleObserver<? super List<Project>> getObservable(int uid) {

        return new SingleObserver<List<Project>>() {
            @Override
            public void onSubscribe(Disposable d) {
                EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_START));
            }

            @Override
            public void onSuccess(List<Project> projects) {
                EventBus.getDefault().post(new DataSyncEvent(uid, DataSyncEvent.EventStatus.EVENT_END));
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_ERROR));
            }
        };
    }
}
