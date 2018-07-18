package org.odk.collect.naxa.onboarding;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.ToastUtils;

public class DownloadPresenterImpl implements DownloadPresenter {

    private DownloadView downloadView;
    private DownloadModel downloadModel;

    public DownloadPresenterImpl(DownloadView downloadView) {
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
    public void onDownloadSelectedButtonClick() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) Collect.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            ToastUtils.showShortToast(R.string.no_connection);
            return;
        }

        downloadModel.fetchODKForms();
        downloadModel.fetchProjectContacts();
    }
}
