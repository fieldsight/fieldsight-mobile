package org.odk.collect.naxa.onboarding;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.Constant;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

public class DownloadPresenterImpl implements DownloadPresenter {

    private DownloadView downloadView;
    private DownloadModel downloadModel;

    DownloadPresenterImpl(DownloadView downloadView) {
        this.downloadView = downloadView;
        this.downloadModel = new DownloadModelImpl();
    }

    @Override
    public void onDownloadItemClick(SyncableItems downloadItem) {

    }

    @Override
    public void onToggleButtonClick() {

        downloadView.toggleAll();
    }

    @Override
    public void onDownloadSelectedButtonClick(ArrayList<SyncableItems> list) {
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

        Observable.just(list)
                .flatMapIterable((Function<ArrayList<SyncableItems>, Iterable<SyncableItems>>) syncableItems -> syncableItems)
                .filter(SyncableItems::getIsSelected)
                .flatMap(syncableItems -> {
                    switch (syncableItems.getUid()) {
                        case Constant.DownloadUID.PROJECT_SITES:
                            downloadModel.fetchProjectSites();
                            break;
                        case Constant.DownloadUID.ODK_FORMS:
                            downloadModel.fetchODKForms();
                            break;
                        case Constant.DownloadUID.GENERAL_FORMS:
                            downloadModel.fetchGeneralForms();
                            break;
                        case Constant.DownloadUID.PROJECT_CONTACTS:
                            downloadModel.fetchProjectContacts();
                            break;

                    }
                    return Observable.empty();
                })
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShortToastInMiddle(Collect.getInstance().getString(R.string.error_occured));
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }
}
