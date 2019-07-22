package org.bcss.collect.naxa.v3.network;

import org.bcss.collect.naxa.login.model.Project;

import java.util.List;

public interface SyncAdapterCallback {
    void onRequestInterrupt(int pos, Project project);
    void childDownloadListSelectionChange(Project project, List<Syncable> list);
    void onRetryButtonClicked(Project project, String[] failedUrls);
}
