package org.bcss.collect.naxa.sync;

public enum DownloadingStatus {
    NOT_DOWNLOADED("notDownloaded"),
    WAITING("waiting"),
    IN_PROGRESS("inProgress"),
    DOWNLOADED("downloaded");

    private String downloadStatus;

    DownloadingStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public static DownloadingStatus getValue(String status) {
        for (DownloadingStatus downloadingStatus : DownloadingStatus.values()) {
            if (downloadingStatus.getDownloadStatus().equalsIgnoreCase(status)) {
                return downloadingStatus;
            }
        }
        return null;
    }

}
