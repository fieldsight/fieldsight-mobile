package org.odk.collect.naxa.onboarding;

import org.odk.collect.naxa.sync.SyncRepository;

public interface DownloadModel {
    void fetchGeneralForms();

    void fetchProjectSites();

    void fetchODKForms(SyncRepository syncRepository);

    void fetchProjectContacts();

}
