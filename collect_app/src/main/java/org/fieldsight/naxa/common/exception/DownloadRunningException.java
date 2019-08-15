package org.fieldsight.naxa.common.exception;

public class DownloadRunningException extends RuntimeException {

    public DownloadRunningException(String message) {
        super(message);
    }

    public DownloadRunningException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
