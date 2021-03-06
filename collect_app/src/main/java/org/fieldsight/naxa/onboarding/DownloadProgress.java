package org.fieldsight.naxa.onboarding;

import java.io.Serializable;

public class DownloadProgress implements Serializable {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_PROGRESS_UPDATE = 2;
    public static final int STATUS_FINISHED_FORM = 3;

    private final String currentFile;
    private final int progress;
    private int total;
    private String message;
    private final boolean isIndeterminate;

    public DownloadProgress(String currentFile, int progress, int total, String message, boolean isIndeterminate) {
        this.currentFile = currentFile;
        this.progress = progress;
        this.total = total;
        this.message = message;
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

    public void setTotal(int total) {

        this.total = total;
    }
}
