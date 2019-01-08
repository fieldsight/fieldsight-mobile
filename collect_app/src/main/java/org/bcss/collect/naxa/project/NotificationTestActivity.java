package org.bcss.collect.naxa.project;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import org.bcss.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;

import java.util.TimerTask;

public class NotificationTestActivity extends CollectAbstractActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_notification);

    }

    public void showNotification(View view) {
        FieldSightNotificationUtils.getINSTANCE().notifyNormal("Normal", "Body");
    }


    public void showHeadsUp(View view) {
        FieldSightNotificationUtils.getINSTANCE().notifyHeadsUp("Head's up", "Body");
    }

    public void showProgress(View view) {
        FieldSightNotificationUtils.getINSTANCE().notifyProgress("A long running task", "running", FieldSightNotificationUtils.ProgressType.UPLOAD);
    }

    public void showDownload(View view) {
        int id = FieldSightNotificationUtils.getINSTANCE().notifyProgress("A long running task", "running", FieldSightNotificationUtils.ProgressType.DOWNLOAD);
        new Handler().postDelayed(() -> {
            FieldSightNotificationUtils.getINSTANCE().cancelNotification(id);
        }, 2000);

    }

}
