package org.odk.collect.naxa.onboarding;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.event.DataSyncEvent;
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
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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


        Observable.just(syncableItemList)
                .flatMapIterable(new Function<ArrayList<SyncableItems>, Iterable<SyncableItems>>() {
                    @Override
                    public Iterable<SyncableItems> apply(ArrayList<SyncableItems> syncableItems) throws Exception {
                        return syncableItems;
                    }
                })
                .map(new Function<SyncableItems, Object>() {
                    @Override
                    public Object apply(SyncableItems syncableItem) throws Exception {
                        switch (syncableItem.getUid()) {
                            case Constant.DownloadUID.GENERAL_FORMS:
                                downloadModel.fetchGeneralForms();
                                Timber.i("Downloading general forms");
                                break;
                            case Constant.DownloadUID.SCHEDULED_FORMS:
                                Timber.i("Downloading scheduled forms");
                                ScheduledFormsRemoteSource.getInstance().getAll();
                                break;
                            case Constant.DownloadUID.STAGED_FORMS:
                                Timber.i("Downloading staged forms");
                                StageRemoteSource.getInstance().getAll();
                                break;
                            case Constant.DownloadUID.ODK_FORMS:
                                Timber.i("Downloading odk forms");
                                downloadModel.fetchODKForms(syncRepository);
                                break;
                            case Constant.DownloadUID.PROJECT_SITES:
                                Timber.i("Downloading project sites");
                                downloadModel.fetchProjectSites();
                                break;
                            case Constant.DownloadUID.PROJECT_CONTACTS:
                                syncRepository.setSuccess(Constant.DownloadUID.PROJECT_CONTACTS);
                                downloadModel.fetchProjectContacts();
                                break;
                        }

                        return Observable.empty();
                    }
                })
                .toList()
                .subscribe(new DisposableSingleObserver<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


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
