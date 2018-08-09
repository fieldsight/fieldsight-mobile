package org.bcss.collect.naxa.onboarding;

import android.arch.lifecycle.LifecycleOwner;

import java.util.List;

interface DownloadView {

    void setUpRecyclerView(List<SyncableItems> syncableItems);

    LifecycleOwner getLifeCycleOwner();
}
