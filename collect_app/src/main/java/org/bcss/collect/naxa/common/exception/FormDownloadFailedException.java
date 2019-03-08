package org.bcss.collect.naxa.common.exception;

public class FormDownloadFailedException extends RuntimeException {

    public FormDownloadFailedException(String message) {
        super(message);
    }

    public FormDownloadFailedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}