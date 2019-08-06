package org.bcss.collect.naxa.common.exception;

public class FormDownloadFailedException extends Throwable {

    public FormDownloadFailedException(String message) {
        super(message);
    }

    public FormDownloadFailedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}