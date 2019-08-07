package org.fieldsight.naxa.onboarding;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.fieldsight.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.contact.ContactRemoteSource;
import org.fieldsight.naxa.educational.EducationalMaterialsRemoteSource;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.previoussubmission.LastSubmissionRemoteSource;
import org.fieldsight.naxa.site.SiteTypeRemoteSource;
import org.fieldsight.naxa.sync.SyncRepository;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import static org.fieldsight.naxa.common.Constant.DownloadUID.ALL_FORMS;
import static org.fieldsight.naxa.common.Constant.DownloadUID.EDU_MATERIALS;
import static org.fieldsight.naxa.common.Constant.DownloadUID.PREV_SUBMISSION;
import static org.fieldsight.naxa.common.Constant.DownloadUID.SITE_TYPES;


public class DownloadPresenterImpl implements DownloadPresenter {

    private DownloadView downloadView;
    private DownloadModel downloadModel;
    private SyncRepository syncRepository;
    private MutableLiveData<Boolean> isDownloading = new MutableLiveData<>();

    public DownloadPresenterImpl(DownloadView downloadView) {
        this.downloadView = downloadView;
        this.downloadModel = new DownloadModelImpl();
        syncRepository = SyncRepository.getInstance();

        int count = (ServiceGenerator.getQueuedAPICount() + ServiceGenerator.getRunningAPICount());
        if (count == 0) {
            syncRepository.setAllRunningTaskAsFailed();
        }

        LiveData<List<SyncableItem>> livedata = syncRepository.getAllSyncItems();
        livedata.observe(downloadView.getLifeCycleOwner(),
                new Observer<List<SyncableItem>>() {
                    @Override
                    public void onChanged(@Nullable List<SyncableItem> syncableItemList) {
                        downloadView.addAdapter(syncableItemList);
                    }
                });
    }

    @Override
    public void onToggleButtonClick(ArrayList<SyncableItem> syncableItemList) {
        for (SyncableItem items : syncableItemList) {
            if (items.isChecked()) {
                syncRepository.setChecked(items.getUid(), false);
            } else {
                syncRepository.setChecked(items.getUid(), true);
            }
        }
    }

    @Override
    public void onDownloadButtonClick(ArrayList<SyncableItem> syncableItemList) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) Collect.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = null;

        if (connectivityManager != null) {
            ni = connectivityManager.getActiveNetworkInfo();
        }

        if (ni == null || !ni.isConnected()) {
            ToastUtils.showShortToast(R.string.no_connection);
            return;
        }

        for (SyncableItem syncableItem : syncableItemList) {
            if (syncableItem.isChecked()) {
                downloadOneItem(syncableItem.getUid());
            }
        }

    }

    @Override
    public void startDownload(int uid) {
        downloadOneItem(uid);
    }

    private void downloadOneItem(int syncableItem) {
        switch (syncableItem) {
            case Constant.DownloadUID.GENERAL_FORMS:
                downloadModel.fetchGeneralForms();
                break;
            case Constant.DownloadUID.SCHEDULED_FORMS:
                downloadModel.fetchScheduledForms();
                break;
            case Constant.DownloadUID.STAGED_FORMS:
                downloadModel.fetchStagedForms();
                break;
            case Constant.DownloadUID.ODK_FORMS:
                downloadModel.fetchODKForms(syncRepository);
                break;
            case Constant.DownloadUID.PROJECT_SITES:
                downloadModel.fetchProjectSites();
                break;
            case Constant.DownloadUID.PROJECT_CONTACTS:
                ContactRemoteSource.getInstance().getAll();
                break;
            case SITE_TYPES:
                SiteTypeRemoteSource.getINSTANCE().getAll();
                break;
            case ALL_FORMS:
                downloadModel.fetchAllForms();
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
