package org.bcss.collect.naxa.common.exception;

public class FormDownloadFailedException extends Exception {

    private String failedUrls;

    public FormDownloadFailedException(String message, String failedUrls) {
        super(message);
        this.failedUrls = failedUrls;
    }

    public String getFailedUrls() {
        return failedUrls;
    }
}