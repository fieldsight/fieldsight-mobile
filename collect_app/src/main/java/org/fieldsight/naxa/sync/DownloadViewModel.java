package org.fieldsight.naxa.sync;

import android.os.Handler;

import androidx.lifecycle.ViewModel;

import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.DisposableManager;
import org.fieldsight.naxa.common.ODKFormRemoteSource;
import org.fieldsight.naxa.common.event.DataSyncEvent;
import org.fieldsight.naxa.common.rx.RetrofitException;
import org.fieldsight.naxa.contact.ContactRemoteSource;
import org.fieldsight.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.fieldsight.naxa.educational.EducationalMaterialsRemoteSource;
import org.fieldsight.naxa.generalforms.data.GeneralForm;
import org.fieldsight.naxa.generalforms.data.GeneralFormRemoteSource;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.onboarding.DownloadProgress;
import org.fieldsight.naxa.onboarding.XMLFormDownloadReceiver;
import org.fieldsight.naxa.onboarding.XMLFormDownloadService;
import org.fieldsight.naxa.previoussubmission.LastSubmissionRemoteSource;
import org.fieldsight.naxa.project.data.ProjectLocalSource;
import org.fieldsight.naxa.project.data.ProjectSitesRemoteSource;
import org.fieldsight.naxa.scheduled.data.ScheduleForm;
import org.fieldsight.naxa.scheduled.data.ScheduledFormsRemoteSource;
import org.fieldsight.naxa.site.SiteTypeRemoteSource;
import org.fieldsight.naxa.site.db.SiteRemoteSource;
import org.fieldsight.naxa.stages.data.Stage;
import org.fieldsight.naxa.stages.data.StageRemoteSource;
import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.application.Collect;

import java.util.ArrayList;
import java.util.List;

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
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.DownloadUID.ALL_FORMS;
import static org.fieldsight.naxa.common.Constant.DownloadUID.EDITED_SITES;
import static org.fieldsight.naxa.common.Constant.DownloadUID.EDU_MATERIALS;
import static org.fieldsight.naxa.common.Constant.DownloadUID.GENERAL_FORMS;
import static org.fieldsight.naxa.common.Constant.DownloadUID.OFFLINE_SITES;
import static org.fieldsight.naxa.common.Constant.DownloadUID.PREV_SUBMISSION;
import static org.fieldsight.naxa.common.Constant.DownloadUID.SCHEDULED_FORMS;
import static org.fieldsight.naxa.common.Constant.DownloadUID.SITE_TYPES;
import static org.fieldsight.naxa.common.Constant.DownloadUID.STAGED_FORMS;
import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;
import static org.fieldsight.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.fieldsight.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.fieldsight.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;

public class DownloadViewModel extends ViewModel {


    public void queueSyncTask(List<DownloadableItem> downloadableItems) {
        for (DownloadableItem downloadableItem : downloadableItems) {
            downloadOneItem(downloadableItem.getUid());
        }
    }


    public void cancelAllTask() {
        DisposableManager.dispose();
        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAllAsPending();
    }


    private void fetchGeneralForms() {

        ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
//                        ProjectSitesRemoteSource
//                                .getInstance()
//                                .fetchProjecSites()
                .flatMap((Function<List<Project>, SingleSource<?>>) projects -> {
                    /*note:
                     *1. ignored projects from flat map
                     *2. used tolist() to wait to complete all odk FORMS download
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
                })
                .subscribe(new SingleObserver<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DisposableManager.add(d);
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsRunning(GENERAL_FORMS);
                    }

                    @Override
                    public void onSuccess(Object o) {
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsCompleted(GENERAL_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsFailed(GENERAL_FORMS);
                        if (e instanceof RetrofitException) {
                            String message = e.getMessage();
                            DownloadableItemLocalSource.getDownloadableItemLocalSource().addErrorMessage(GENERAL_FORMS, message);
                        }
                    }
                });

    }


    private void fetchScheduledForms() {
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
                     *2. used tolist to wait to complete all odk FORMS download
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
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsRunning(SCHEDULED_FORMS);
                    }

                    @Override
                    public void onSuccess(ArrayList<ScheduleForm> scheduleForms) {
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsCompleted(SCHEDULED_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsFailed(SCHEDULED_FORMS);
                        if (e instanceof RetrofitException) {
                            String message = e.getMessage();
                            DownloadableItemLocalSource.getDownloadableItemLocalSource().addErrorMessage(SCHEDULED_FORMS, message);
                        }
                    }
                });
    }


    private void fetchStagedForms() {

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
                     *2. used tolist to wait to complete all odk FORMS download
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
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsRunning(STAGED_FORMS);
                    }

                    @Override
                    public void onSuccess(ArrayList<Stage> scheduleForms) {
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsCompleted(STAGED_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsFailed(STAGED_FORMS);
                        if (e instanceof RetrofitException) {
                            String message = e.getMessage();
                            DownloadableItemLocalSource.getDownloadableItemLocalSource().addErrorMessage(STAGED_FORMS, message);
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
                    DownloadableItemLocalSource.getDownloadableItemLocalSource().updateProgress(Constant.DownloadUID.ALL_FORMS, progress.getTotal(), progress.getProgress());
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




    @Deprecated
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
                     *2. used tolist to wait to complete all odk FORMS download
                     */
                    return ODKFormRemoteSource.getInstance()
                            .fetchODKForms()
                            .map(downloadProgress -> {
                                //todo: broadcast odk form progress
                                Timber.i(downloadProgress.toString());
                                DownloadableItemLocalSource.getDownloadableItemLocalSource().updateProgress(ALL_FORMS, downloadProgress.getTotal(), downloadProgress.getProgress());
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
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsRunning(ALL_FORMS);
                    }

                    @Override
                    public void onNext(Object o) {
                        FieldSightNotificationLocalSource.getInstance().markFormsAsRead();
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsCompleted(ALL_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        String message;
                        if (e instanceof RetrofitException) {
                            message = ((RetrofitException) e).getKind().getMessage();
                        } else {
                            message = e.getMessage();
                        }

                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsFailed(ALL_FORMS, message);
                    }

                    @Override
                    public void onComplete() {
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsCompleted(ALL_FORMS);
                    }
                });

    }

    public void downloadOneItem(int syncableItem) {
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
                fetchAllForms();
                break;
            case Constant.DownloadUID.PROJECT_SITES:
                ProjectSitesRemoteSource.getInstance().getAll();
                break;
            case Constant.DownloadUID.PROJECT_CONTACTS:
                ContactRemoteSource.getInstance().getAll();
                break;
            case SITE_TYPES:
                SiteTypeRemoteSource.getSiteTypeRemoteSource().getAll();
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
            case EDITED_SITES:
                SiteRemoteSource.getInstance().updateAllEditedSite();
                break;
            case OFFLINE_SITES:
                SiteRemoteSource.getInstance().uploadAllOfflineSite();
                break;

        }
    }


    void setAllRunningTaskAsFailed() {
        DownloadableItemLocalSource.getDownloadableItemLocalSource().setAllRunningTaskAsFailed();
    }
}
