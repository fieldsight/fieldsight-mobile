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

        switch (downloadItem.getUid()) {
            default:
                downloadModel.fetchProjectSites();
                break;
        }
    }
}
