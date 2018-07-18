package org.odk.collect.naxa.onboarding;

import java.util.ArrayList;

public interface DownloadPresenter {
    void onDownloadItemClick(SyncableItems downloadItem);

    void onToggleButtonClick();

    void onDownloadSelectedButtonClick(ArrayList<SyncableItems> list);
}
