package org.bcss.collect.naxa.onboarding;

import org.bcss.collect.naxa.sync.SyncRepository;

public interface DownloadModel {
    void fetchGeneralForms();

    void fetchScheduledForms();

    void fetchStagedForms();

    void fetchProjectSites();

    void fetchODKForms(SyncRepository syncRepository);

    void fetchProjectContacts();

}
