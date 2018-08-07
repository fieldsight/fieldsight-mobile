package org.odk.collect.naxa.onboarding;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.ODKFormRemoteSource;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.generalforms.data.GeneralForm;
import org.odk.collect.naxa.generalforms.data.GeneralFormLocalSource;
import org.odk.collect.naxa.generalforms.data.GeneralFormRemoteSource;
import org.odk.collect.naxa.generalforms.data.GeneralFormRepository;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.project.data.ProjectRepository;
import org.odk.collect.naxa.project.data.ProjectSitesRemoteSource;
import org.odk.collect.naxa.scheduled.data.ScheduleForm;
import org.odk.collect.naxa.scheduled.data.ScheduledFormsRemoteSource;
import org.odk.collect.naxa.site.db.SiteRepository;
import org.odk.collect.naxa.stages.data.Stage;
import org.odk.collect.naxa.stages.data.StageRemoteSource;
import org.odk.collect.naxa.sync.SyncRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;

public class DownloadModelImpl implements DownloadModel {

    private SiteRepository siteRepository;
    private GeneralFormRepository generalFormRepository;
    private ProjectRepository projectRepository;
    private DownloadFormsTask downloadFormsTask;
    private HashMap<String, FormDetails> formNamesAndURLs;
    private ArrayList<HashMap<String, String>> formList;
    private DownloadFormListTask downloadFormListTask;

    public DownloadModelImpl() {
        this.generalFormRepository = GeneralFormRepository.getInstance(GeneralFormLocalSource.getInstance(), GeneralFormRemoteSource.getInstance());
        formList = new ArrayList<>();

    }

    @Deprecated
    @Override
    public void fetchGeneralForms() {

        ProjectSitesRemoteSource
                .getInstance()
                .fetchProjecSites()
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
                .flatMap(new Function<List<DownloadProgress>, Single<ArrayList<GeneralForm>>>() {
                    @Override
                    public Single<ArrayList<GeneralForm>> apply(List<DownloadProgress> downloadProgresses) throws Exception {
                        return GeneralFormRemoteSource.getInstance().fetchAllGeneralForms();
                    }
                })
                .subscribe(new SingleObserver<ArrayList<GeneralForm>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        SyncRepository.getInstance().showProgress(Constant.DownloadUID.GENERAL_FORMS);
                    }

                    @Override
                    public void onSuccess(ArrayList<GeneralForm> generalForms) {
                        SyncRepository.getInstance().setSuccess(Constant.DownloadUID.GENERAL_FORMS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        SyncRepository.getInstance().setFailed(Constant.DownloadUID.GENERAL_FORMS);
                    }
                });
    }

    @Override
    public void fetchScheduledForms() {
        ProjectSitesRemoteSource
                .getInstance()
                .fetchProjecSites()
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
                    public Single<ArrayList<ScheduleForm>> apply(List<DownloadProgress> downloadProgresses) throws Exception {
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
                        SyncRepository.getInstance().setFailed(Constant.DownloadUID.SCHEDULED_FORMS);
                    }
                });
    }

    @Override
    public void fetchStagedForms() {
        ProjectSitesRemoteSource
                .getInstance()
                .fetchProjecSites()
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
                .flatMap(new Function<List<DownloadProgress>, Single<ArrayList<Stage>>>() {
                    @Override
                    public Single<ArrayList<Stage>> apply(List<DownloadProgress> downloadProgresses) throws Exception {
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
                        SyncRepository.getInstance().setFailed(Constant.DownloadUID.STAGED_FORMS);
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
                    syncRepository.setFailed(uid);
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
    public void fetchProjectContacts() {
        int uid = Constant.DownloadUID.PROJECT_CONTACTS;
    }

}
