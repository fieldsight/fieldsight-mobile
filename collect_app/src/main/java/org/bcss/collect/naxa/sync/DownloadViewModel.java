package org.bcss.collect.naxa.sync;

import android.arch.lifecycle.ViewModel;
import android.os.Handler;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DisposableManager;
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
import org.bcss.collect.naxa.site.db.SiteRemoteSource;
import org.bcss.collect.naxa.stages.data.Stage;
import org.bcss.collect.naxa.stages.data.StageRemoteSource;
import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.ResponseUtils.isListOfType;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.ALL_FORMS;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.EDITED_SITES;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.EDU_MATERIALS;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.GENERAL_FORMS;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.OFFLINE_SITES;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.PREV_SUBMISSION;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.SCHEDULED_FORMS;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.SITE_TYPES;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.STAGED_FORMS;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;

public class DownloadViewModel extends ViewModel {

    private final CompositeDisposable disposables = new CompositeDisposable();

    public void queueSyncTask(List<DownloadableItem> downloadableItems) {
        for (DownloadableItem downloadableItem : downloadableItems) {
            downloadOneItem(downloadableItem.getUid());
        }

    }


    public void cancelAllTask() {
        DisposableManager.dispose();
        DownloadableItemLocalSource.getINSTANCE().markAllAsPending();
    }


    public void fetchGeneralForms() {

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
                })
                .subscribe(new SingleObserver<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DisposableManager.add(d);
                        DownloadableItemLocalSource.getINSTANCE().markAsRunning(GENERAL_FORMS);
                    }

                    @Override
                    public void onSuccess(Object o) {
                        DownloadableItemLocalSource.getINSTANCE().markAsCompleted(GENERAL_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        DownloadableItemLocalSource.getINSTANCE().markAsFailed(GENERAL_FORMS);
                        if (e instanceof RetrofitException) {
                            String message = e.getMessage();
                            DownloadableItemLocalSource.getINSTANCE().addErrorMessage(GENERAL_FORMS, message);
                        }
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
                        DownloadableItemLocalSource.getINSTANCE().markAsRunning(SCHEDULED_FORMS);
                    }

                    @Override
                    public void onSuccess(ArrayList<ScheduleForm> scheduleForms) {
                        DownloadableItemLocalSource.getINSTANCE().markAsCompleted(SCHEDULED_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DownloadableItemLocalSource.getINSTANCE().markAsFailed(SCHEDULED_FORMS);
                        if (e instanceof RetrofitException) {
                            String message = e.getMessage();
                            DownloadableItemLocalSource.getINSTANCE().addErrorMessage(SCHEDULED_FORMS, message);
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
                        DownloadableItemLocalSource.getINSTANCE().markAsRunning(STAGED_FORMS);
                    }

                    @Override
                    public void onSuccess(ArrayList<Stage> scheduleForms) {
                        DownloadableItemLocalSource.getINSTANCE().markAsCompleted(STAGED_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DownloadableItemLocalSource.getINSTANCE().markAsFailed(STAGED_FORMS);
                        if (e instanceof RetrofitException) {
                            String message = e.getMessage();
                            DownloadableItemLocalSource.getINSTANCE().addErrorMessage(STAGED_FORMS, message);
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
                    DownloadableItemLocalSource.getINSTANCE().updateProgress(Constant.DownloadUID.ALL_FORMS, progress.getTotal(), progress.getProgress());
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

    private void fetchAllFormsV2() {


        Observable<String[]> odkFormsObservable = ODKFormRemoteSource.getInstance().getXMLForms();
        Single<ArrayList<GeneralForm>> general = GeneralFormRemoteSource.getInstance().fetchAllGeneralForms();
        Single<ArrayList<ScheduleForm>> scheduled = ScheduledFormsRemoteSource.getInstance().fetchAllScheduledForms();
        Single<ArrayList<Stage>> stage = StageRemoteSource.getInstance().fetchAllStages();

        DisposableObserver<Serializable> dis = Observable.concat(odkFormsObservable, general.toObservable(), scheduled.toObservable(), stage.toObservable())
                .doOnSubscribe(disposable -> DownloadableItemLocalSource.getINSTANCE().markAsRunning(ALL_FORMS, "Preparing to start"))
                .subscribeWith(new DisposableObserver<Serializable>() {
                    @Override
                    public void onNext(Serializable serializable) {
                        if (serializable instanceof String[]) {
                            //do something
                            String[] string = (String[]) serializable;
                            String message = String.format("Downloading %s", string[0]);
                            int current = Integer.parseInt(string[1]);
                            int total = Integer.parseInt(string[2]);

                            DownloadableItemLocalSource.getINSTANCE().setProgress(ALL_FORMS, current, total);
                            DownloadableItemLocalSource.getINSTANCE().markAsRunning(ALL_FORMS, message);

                        } else if (serializable instanceof ArrayList) {
                            if (isListOfType((Collection<?>) serializable, GeneralForm.class)) {
                                DownloadableItemLocalSource.getINSTANCE().markAsRunning(ALL_FORMS, "Syncing General form(s)");
                            } else if (isListOfType((Collection<?>) serializable, ScheduleForm.class)) {
                                DownloadableItemLocalSource.getINSTANCE().markAsRunning(ALL_FORMS, "Syncing Scheduled form(s)");
                            } else if (isListOfType((Collection<?>) serializable, Stage.class)) {

                                DownloadableItemLocalSource.getINSTANCE().markAsRunning(ALL_FORMS, "Syncing Staged form(s)");
                                DownloadableItemLocalSource.getINSTANCE().markAsRunning(ALL_FORMS, "Downloads all forms for assigned sites");
                                DownloadableItemLocalSource.getINSTANCE().markAsCompleted(ALL_FORMS);

                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        String message;
                        if (e instanceof RetrofitException) {
                            message = ((RetrofitException) e).getKind().getMessage();
                        } else {
                            message = e.getMessage();
                        }
                        DownloadableItemLocalSource.getINSTANCE().markAsFailed(ALL_FORMS, message);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        DisposableManager.add(dis);
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
                     *2. used tolist to wait to complete all odk forms download
                     */
                    return ODKFormRemoteSource.getInstance()
                            .fetchODKForms()
                            .map(downloadProgress -> {
                                //todo: broadcast odk form progress
                                Timber.i(downloadProgress.toString());
                                DownloadableItemLocalSource.getINSTANCE().updateProgress(ALL_FORMS, downloadProgress.getTotal(), downloadProgress.getProgress());
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
                        DownloadableItemLocalSource.getINSTANCE().markAsRunning(ALL_FORMS);
                    }

                    @Override
                    public void onNext(Object o) {
                        FieldSightNotificationLocalSource.getInstance().markFormsAsRead();
                        DownloadableItemLocalSource.getINSTANCE().markAsCompleted(ALL_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        String message;
                        if (e instanceof RetrofitException) {
                            message = ((RetrofitException) e).getKind().getMessage();
                        } else {
                            message = e.getMessage();
                        }

                        DownloadableItemLocalSource.getINSTANCE().markAsFailed(ALL_FORMS, message);
                    }

                    @Override
                    public void onComplete() {
                        DownloadableItemLocalSource.getINSTANCE().markAsCompleted(ALL_FORMS);
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
            case EDITED_SITES:
                SiteRemoteSource.getInstance().updateAllEditedSite();
                break;
            case OFFLINE_SITES:
                SiteRemoteSource.getInstance().uploadAllOfflineSite();
                break;

        }
    }


    void setAllRunningTaskAsFailed() {
        DownloadableItemLocalSource.getINSTANCE().setAllRunningTaskAsFailed();
    }
}
