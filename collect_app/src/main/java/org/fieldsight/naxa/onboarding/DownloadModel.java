package org.fieldsight.naxa.onboarding;

import org.fieldsight.naxa.sync.SyncRepository;

public interface DownloadModel {
    void fetchGeneralForms();

    void fetchScheduledForms();

    void fetchStagedForms();

    void fetchProjectSites();

    void fetchODKForms(SyncRepository syncRepository);

    void fetchAllForms();

}
