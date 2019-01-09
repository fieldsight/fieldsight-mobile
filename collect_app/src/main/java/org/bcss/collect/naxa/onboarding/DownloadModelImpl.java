package org.bcss.collect.naxa.onboarding;

import android.os.Handler;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.ODKFormRemoteSource;
import org.bcss.collect.naxa.common.event.DataSyncEvent;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.generalforms.data.GeneralFormRemoteSource;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.project.data.ProjectSitesRemoteSource;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsRemoteSource;
import org.bcss.collect.naxa.stages.data.Stage;
import org.bcss.collect.naxa.stages.data.StageRemoteSource;
import org.bcss.collect.naxa.sync.SyncRepository;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.ALL_FORMS;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;

public class DownloadModelImpl implements DownloadModel {


    public DownloadModelImpl() {


    }

    @Deprecated
    @Override
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
                    @Override
                    public SingleSource<?> apply(Object o) {
                        return GeneralFormRemoteSource.getInstance().fetchAllGeneralForms();
                    }
                })
                .subscribe(new SingleObserver<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        SyncRepository.getInstance().showProgress(Constant.DownloadUID.GENERAL_FORMS);
                    }

                    @Override
                    public void onSuccess(Object o) {
                        SyncRepository.getInstance().setSuccess(Constant.DownloadUID.GENERAL_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        SyncRepository.getInstance().setError(Constant.DownloadUID.GENERAL_FORMS);
                    }
                });

    }

    @Override
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
                        SyncRepository.getInstance().showProgress(Constant.DownloadUID.SCHEDULED_FORMS);
                    }

                    @Override
                    public void onSuccess(ArrayList<ScheduleForm> scheduleForms) {
                        SyncRepository.getInstance().setSuccess(Constant.DownloadUID.SCHEDULED_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        SyncRepository.getInstance().setError(Constant.DownloadUID.SCHEDULED_FORMS);
                    }
                });
    }

    @Override
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
                        SyncRepository.getInstance().showProgress(Constant.DownloadUID.STAGED_FORMS);
                    }

                    @Override
                    public void onSuccess(ArrayList<Stage> scheduleForms) {
                        SyncRepository.getInstance().setSuccess(Constant.DownloadUID.STAGED_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        SyncRepository.getInstance().setError(Constant.DownloadUID.STAGED_FORMS);
                    }
                });
    }


    @Override
    public void fetchProjectSites() {
        new ProjectSitesRemoteSource().getAll();
    }


    @Override
    public void fetchODKForms(SyncRepository syncRepository) {
        int uid = Constant.DownloadUID.ODK_FORMS;
        syncRepository.showProgress(uid);
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
                    syncRepository.setError(uid);
                    break;
                case DownloadProgress.STATUS_FINISHED_FORM:
                    EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_END));
                    syncRepository.setSuccess(uid);
                    break;
            }


        });
        XMLFormDownloadService.start(Collect.getInstance(), xmlFormDownloadReceiver);

    }


    @Override
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
                        SyncRepository.getInstance().showProgress(ALL_FORMS);
                    }

                    @Override
                    public void onNext(Object o) {
                        SyncRepository.getInstance().setSuccess(ALL_FORMS);
                        FieldSightNotificationLocalSource.getInstance().markFormsAsRead();
                    }

                    @Override
                    public void onError(Throwable e) {
                        SyncRepository.getInstance().setError(ALL_FORMS);
                    }

                    @Override
                    public void onComplete() {
                        SyncRepository.getInstance().setSuccess(ALL_FORMS);
                    }
                });

    }

}
