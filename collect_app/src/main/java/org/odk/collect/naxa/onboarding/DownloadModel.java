package org.odk.collect.naxa.onboarding;

import org.odk.collect.naxa.login.LoginModel;

public interface DownloadModel {
    void fetchGeneralForms();

    void fetchProjectSites();

    void fetchODKForms();

    void fetchProjectContacts();


}
