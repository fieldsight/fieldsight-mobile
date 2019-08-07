package org.fieldsight.naxa.onboarding;

import java.util.ArrayList;

public interface DownloadPresenter {

    void onToggleButtonClick(ArrayList<SyncableItem> list);

    void onDownloadButtonClick(ArrayList<SyncableItem> list);

    void startDownload(int uid);
}
