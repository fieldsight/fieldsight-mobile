package org.bcss.collect.naxa.onboarding;

import java.io.Serializable;

public class DownloadProgress implements Serializable {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_PROGRESS_UPDATE = 2;
    public static final int STATUS_FINISHED_FORM = 3;

    private String currentFile;
    private int progress;
    private int total;
    private String message;
    private boolean isIndeterminate;

    DownloadProgress(String message, int total, boolean isIndeterminate) {
        this.message = currentFile;
        this.total = total;
        this.isIndeterminate = isIndeterminate;
    }

    public boolean isIndeterminate() {
        return isIndeterminate;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCurrentFile() {
        return currentFile;
    }

    @Override
    public String toString() {
        return "DownloadProgress{" +
                "currentFile='" + currentFile + '\'' +
                ", progress=" + progress +
                ", total=" + total +
                ", message='" + message + '\'' +
                '}';
    }

    public int getProgress() {
        return progress;
    }

    public int getTotal() {
        return total;
    }

    public String getMessage() {
        return message;
    }

}
