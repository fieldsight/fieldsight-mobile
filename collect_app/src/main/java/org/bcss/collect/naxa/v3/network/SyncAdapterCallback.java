package org.bcss.collect.naxa.v3.network;

import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.v3.adapter.SyncAdapterv3;

import java.util.HashMap;
import java.util.List;

public interface SyncAdapterCallback {
    void onRequestInterrupt(int pos, Project project);
    void childDownloadListSelectionChange(Project project, List<Syncable> list);
}
