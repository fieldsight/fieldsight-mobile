package org.bcss.collect.naxa.onboarding;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

interface DownloadView {

    void addAdapter(List<SyncableItem> syncableItems);

    LifecycleOwner getLifeCycleOwner();
}
