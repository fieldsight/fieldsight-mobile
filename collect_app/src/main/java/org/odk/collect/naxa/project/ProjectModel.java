package org.odk.collect.naxa.project;

public interface ProjectModel {

    interface OnUserInformationDownloadListener {
        void onError(Exception e);
        void onSuccess();
    }

    void downloadUserInformation(OnUserInformationDownloadListener listener);
}
