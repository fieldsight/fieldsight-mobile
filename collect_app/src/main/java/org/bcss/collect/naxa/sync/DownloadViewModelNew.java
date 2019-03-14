package org.bcss.collect.naxa.sync;

import android.arch.lifecycle.ViewModel;
import android.os.Handler;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.ODKFormRemoteSource;
import org.bcss.collect.naxa.common.event.DataSyncEvent;
import org.bcss.collect.naxa.common.rx.RetrofitException;
import org.bcss.collect.naxa.contact.ContactRemoteSource;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.educational.EducationalMaterialsRemoteSource;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.generalforms.data.GeneralFormRemoteSource;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.onboarding.DownloadProgress;
import org.bcss.collect.naxa.onboarding.XMLFormDownloadReceiver;
import org.bcss.collect.naxa.onboarding.XMLFormDownloadService;
import org.bcss.collect.naxa.previoussubmission.LastSubmissionRemoteSource;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.project.data.ProjectSitesRemoteSource;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsRemoteSource;
import org.bcss.collect.naxa.site.SiteTypeRemoteSource;
import org.bcss.collect.naxa.stages.data.Stage;
import org.bcss.collect.naxa.stages.data.StageRemoteSource;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.ALL_FORMS;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.EDU_MATERIALS;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.GENERAL_FORMS;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.PREV_SUBMISSION;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.SCHEDULED_FORMS;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.SITE_TYPES;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.STAGED_FORMS;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;

public class DownloadViewModelNew extends ViewModel {

    private final CompositeDisposable disposables = new CompositeDisposable();

    void queueSyncTask(List<Sync> syncs) {
        for (Sync sync : syncs) {
            downloadOneItem(sync.getUid());
        }

    }


    void cancelAllTask() {
        DisposableManager.dispose();
        SyncLocalSource.getINSTANCE().markAllAsPending();
    }


    public Single<Object> fetchGeneralForms() {

        return ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
//                        ProjectSitesRemoteSource
//                                .getInstance()
//                                .fetchProjecSites()
                .flatMap((Function<List<Project>, SingleSource<?>>) projects -> {
                    /*note:
                     *1. ignored projects from flat map
                     *2. used tolist() to wait to complete all odk forms download
                     */
                    return ODKFormRemoteSource.getInstance()
                            .fetchODKForms()
                            .map(downloadProgress -> {
                                //todo: broadcast odk form progress
                                Timber.i(downloadProgress.toString());
                                return downloadProgress;
                            })
                            .toList();
                })
                .flatMap(new Function<Object, SingleSource<?>>() {
                    public SingleSource<?> apply(Object o) {
                        return GeneralFormRemoteSource.getInstance().fetchAllGeneralForms();
                    }
                });
    }





    public void fetchScheduledForms() {
        ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
//        ProjectSitesRemoteSource
//                .getInstance()
//                .fetchProjecSites()
                .flatMap((Function<List<Project>, Single<List<DownloadProgress>>>) projects -> {
                    /*note:
                     *1. ignored projects from flat map
                     *2. used tolist to wait to complete all odk forms download
                     */
                    return ODKFormRemoteSource.getInstance()
                            .fetchODKForms()
                            .map(downloadProgress -> {
                                //todo: broadcast odk form progress
                                Timber.i(downloadProgress.toString());
                                return downloadProgress;
                            })
                            .toList();
                })
                .flatMap(new Function<List<DownloadProgress>, Single<ArrayList<ScheduleForm>>>() {
                    @Override
                    public Single<ArrayList<ScheduleForm>> apply(List<DownloadProgress> downloadProgresses) {
                        return ScheduledFormsRemoteSource.getInstance().fetchAllScheduledForms();
                    }
                })
                .subscribe(new SingleObserver<ArrayList<ScheduleForm>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DisposableManager.add(d);
                        SyncLocalSource.getINSTANCE().markAsRunning(SCHEDULED_FORMS);
                    }

                    @Override
                    public void onSuccess(ArrayList<ScheduleForm> scheduleForms) {
                        SyncLocalSource.getINSTANCE().markAsCompleted(SCHEDULED_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        SyncLocalSource.getINSTANCE().markAsFailed(SCHEDULED_FORMS);
                        if (e instanceof RetrofitException) {
                            String message = e.getMessage();
                            SyncLocalSource.getINSTANCE().addErrorMessage(SCHEDULED_FORMS, message);
                        }
                    }
                });
    }


    public void fetchStagedForms() {

//        ProjectSitesRemoteSource
//                .getInstance()
//                .fetchProjecSites()
        ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap((Function<List<Project>, SingleSource<?>>) projects -> {
                    /*note:
                     *1. ignored projects from flat map
                     *2. used tolist to wait to complete all odk forms download
                     */
                    return ODKFormRemoteSource.getInstance()
                            .fetchODKForms()
                            .map(downloadProgress -> {
                                //todo: broadcast odk form progress
                                Timber.i(downloadProgress.toString());
                                return downloadProgress;
                            })
                            .toList();
                })
                .flatMap(new Function<Object, SingleSource<ArrayList<Stage>>>() {
                    @Override
                    public SingleSource<ArrayList<Stage>> apply(Object o) {
                        return StageRemoteSource.getInstance().fetchAllStages();
                    }
                })
                .subscribe(new SingleObserver<ArrayList<Stage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DisposableManager.add(d);
                        SyncLocalSource.getINSTANCE().markAsRunning(STAGED_FORMS);
                    }

                    @Override
                    public void onSuccess(ArrayList<Stage> scheduleForms) {
                        SyncLocalSource.getINSTANCE().markAsCompleted(STAGED_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        SyncLocalSource.getINSTANCE().markAsFailed(STAGED_FORMS);
                        if (e instanceof RetrofitException) {
                            String message = e.getMessage();
                            SyncLocalSource.getINSTANCE().addErrorMessage(STAGED_FORMS, message);
                        }
                    }
                });
    }

    public void fetchODKForms() {
        int uid = Constant.DownloadUID.ODK_FORMS;
//        syncRepository.showProgress(uid);
        XMLFormDownloadReceiver xmlFormDownloadReceiver = new XMLFormDownloadReceiver(new Handler());
        xmlFormDownloadReceiver.setReceiver((resultCode, resultData) -> {
            switch (resultCode) {
                case DownloadProgress.STATUS_RUNNING:
                    EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_START));
                    break;
                case DownloadProgress.STATUS_PROGRESS_UPDATE:
                    DownloadProgress progress = (DownloadProgress) resultData.getSerializable(EXTRA_OBJECT);
                    Timber.i(progress.getMessage());
                    SyncLocalSource.getINSTANCE().updateProgress(Constant.DownloadUID.ALL_FORMS,progress.getTotal(),progress.getProgress());
                    break;
                case DownloadProgress.STATUS_ERROR:
                    EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_ERROR));
//                    syncRepository.setError(uid);
                    break;
                case DownloadProgress.STATUS_FINISHED_FORM:
                    EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_END));
//                    syncRepository.setSuccess(uid);
                    break;
            }


        });
        XMLFormDownloadService.start(Collect.getInstance(), xmlFormDownloadReceiver);


    }


    public void fetchAllForms() {

        ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
//        ProjectSitesRemoteSource
//                .getInstance()
//                .fetchProjecSites()
                .flatMap((Function<List<Project>, SingleSource<List<DownloadProgress>>>) projects -> {
                    /*note:
                     *1. ignored projects from flat map
                     *2. used tolist to wait to complete all odk forms download
                     */
                    return ODKFormRemoteSource.getInstance()
                            .fetchODKForms()
                            .map(downloadProgress -> {
                                //todo: broadcast odk form progress
                                Timber.i(downloadProgress.toString());
                                SyncLocalSource.getINSTANCE().updateProgress(ALL_FORMS, downloadProgress.getTotal(), downloadProgress.getProgress());
                                return downloadProgress;
                            })
                            .toList();
                })

                .toObservable()
                .flatMap(new Function<List<DownloadProgress>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(List<DownloadProgress> downloadProgresses) {
                        Single<ArrayList<GeneralForm>> general = GeneralFormRemoteSource.getInstance().fetchAllGeneralForms();
                        Single<ArrayList<ScheduleForm>> scheduled = ScheduledFormsRemoteSource.getInstance().fetchAllScheduledForms();
                        Single<ArrayList<Stage>> stage = StageRemoteSource.getInstance().fetchAllStages();

                        return Observable.merge(general.toObservable(), scheduled.toObservable(), stage.toObservable());
                    }
                })
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DisposableManager.add(d);
                        SyncLocalSource.getINSTANCE().markAsRunning(ALL_FORMS);
                    }

                    @Override
                    public void onNext(Object o) {
                        FieldSightNotificationLocalSource.getInstance().markFormsAsRead();
                        SyncLocalSource.getINSTANCE().markAsCompleted(ALL_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        String message;
                        if (e instanceof RetrofitException) {
                            message = ((RetrofitException) e).getKind().getMessage();
                        } else {
                            message = e.getMessage();
                        }

                        SyncLocalSource.getINSTANCE().addErrorMessage(ALL_FORMS, message);
                        SyncLocalSource.getINSTANCE().markAsFailed(ALL_FORMS);
                    }

                    @Override
                    public void onComplete() {
                        SyncLocalSource.getINSTANCE().markAsCompleted(ALL_FORMS);
                    }
                });

    }





    private void downloadOneItem(int syncableItem) {
        switch (syncableItem) {
            case GENERAL_FORMS:
                fetchGeneralForms();
                break;
            case SCHEDULED_FORMS:
                fetchScheduledForms();
                break;
            case STAGED_FORMS:
                fetchStagedForms();
                break;
            case Constant.DownloadUID.ODK_FORMS:
                fetchODKForms();
                break;
            case Constant.DownloadUID.PROJECT_SITES:
                ProjectSitesRemoteSource.getInstance().getAll();
                break;
            case Constant.DownloadUID.PROJECT_CONTACTS:
                ContactRemoteSource.getInstance().getAll();
                break;
            case SITE_TYPES:
                SiteTypeRemoteSource.getINSTANCE().getAll();
                break;
            case ALL_FORMS:
                fetchAllForms();
                break;
            case EDU_MATERIALS:
                EducationalMaterialsRemoteSource.getInstance().getAll();
                break;
            case PREV_SUBMISSION:
                LastSubmissionRemoteSource.getInstance().getAll();
                break;

        }
    }


}
