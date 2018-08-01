package org.odk.collect.naxa.onboarding;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.generalforms.data.GeneralFormLocalSource;
import org.odk.collect.naxa.generalforms.data.GeneralFormRemoteSource;
import org.odk.collect.naxa.generalforms.data.GeneralFormRepository;
import org.odk.collect.naxa.project.data.ProjectRepository;
import org.odk.collect.naxa.project.data.ProjectSitesRemoteSource;
import org.odk.collect.naxa.site.db.SiteRepository;
import org.odk.collect.naxa.sync.SyncRepository;

import java.util.ArrayList;
import java.util.HashMap;

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
        new GeneralFormRemoteSource().getAll();
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
