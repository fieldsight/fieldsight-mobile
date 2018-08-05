package org.odk.collect.naxa.onboarding;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.generalforms.data.GeneralFormRemoteSource;
import org.odk.collect.naxa.project.data.ProjectSitesRemoteSource;
import org.odk.collect.naxa.scheduled.data.ScheduledFormsRemoteSource;
import org.odk.collect.naxa.stages.data.StageRemoteSource;
import org.odk.collect.naxa.sync.SyncRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class DownloadPresenterImpl implements DownloadPresenter {

    private DownloadView downloadView;
    private DownloadModel downloadModel;
    private SyncRepository syncRepository;

    public DownloadPresenterImpl(DownloadView downloadView) {
        this.downloadView = downloadView;
        this.downloadModel = new DownloadModelImpl();
        syncRepository = new SyncRepository(Collect.getInstance());
        syncRepository.setAllCheckedTrue();
        syncRepository.getAllSyncItems().observe(downloadView.getLifeCycleOwner(), new Observer<List<SyncableItems>>() {
            @Override
            public void onChanged(@Nullable List<SyncableItems> syncableItemsList) {
                downloadView.setUpRecyclerView(syncableItemsList);
            }
        });
    }

    @Override
    public void onToggleButtonClick(ArrayList<SyncableItems> syncableItemList) {
        for (SyncableItems items : syncableItemList) {
            if (items.getIsSelected()) {
                syncRepository.setChecked(items.getUid(), false);
            } else {
                syncRepository.setChecked(items.getUid(), true);
            }
        }
    }

    @Override
    public void onDownloadButtonClick(ArrayList<SyncableItems> syncableItemList) {
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


        for (SyncableItems syncableItem : syncableItemList) {
            if (syncableItem.isChecked()) {
                syncRepository.showProgress(syncableItem.getUid());
                switch (syncableItem.getUid()) {
                    case Constant.DownloadUID.GENERAL_FORMS:
                        downloadModel.fetchGeneralForms();
                        break;
                    case Constant.DownloadUID.SCHEDULED_FORMS:
                        ScheduledFormsRemoteSource.getInstance().getAll();
                        break;
                    case Constant.DownloadUID.STAGED_FORMS:
                        StageRemoteSource.getInstance().getAll();
                        break;
                    case Constant.DownloadUID.ODK_FORMS:
                        downloadModel.fetchODKForms(syncRepository);
                        break;
                    case Constant.DownloadUID.PROJECT_SITES:
                        downloadModel.fetchProjectSites();
                        break;
                    case Constant.DownloadUID.PROJECT_CONTACTS:
                        syncRepository.setSuccess(Constant.DownloadUID.PROJECT_CONTACTS);
                        downloadModel.fetchProjectContacts();
                        break;
                }
            }
        }

    }
}
