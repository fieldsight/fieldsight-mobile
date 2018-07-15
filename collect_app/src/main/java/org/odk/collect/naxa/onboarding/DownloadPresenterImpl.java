package org.odk.collect.naxa.onboarding;

public class DownloadPresenterImpl implements DownloadPresenter {

    private DownloadView downloadView;
    private DownloadModel downloadModel;

    public DownloadPresenterImpl(DownloadView downloadView) {
        this.downloadView = downloadView;
        this.downloadModel = new DownloadModelImpl();
    }

    @Override
    public void onDownloadItemClick(DownloadableItem downloadItem) {

    }

    @Override
    public void onToggleButtonClick() {
        downloadView.toggleAll();
    }

    @Override
    public void onDownloadSelectedButtonClick() {
        downloadModel.fetchProjectSites();
    }
}
