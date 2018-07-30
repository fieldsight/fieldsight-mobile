package org.odk.collect.naxa.onboarding;

import java.util.ArrayList;

public interface DownloadPresenter {

    void onToggleButtonClick(ArrayList<SyncableItems> list);

    void onDownloadButtonClick(ArrayList<SyncableItems> list);
}
