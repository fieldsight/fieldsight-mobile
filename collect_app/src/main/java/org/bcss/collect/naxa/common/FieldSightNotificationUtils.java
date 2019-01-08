package org.bcss.collect.naxa.common;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.app.NotificationChannel;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.odk.collect.android.utilities.IconUtils;
import org.bcss.collect.naxa.notificationslist.NotificationListActivity;

import java.util.concurrent.atomic.AtomicInteger;


public class FieldSightNotificationUtils {
    private NotificationManager notifManager;
    private static final String CHANNEL_ID = "fieldsight_notification_channel";

    private static FieldSightNotificationUtils INSTANCE;
    private static AtomicInteger notificationUID;

    public static FieldSightNotificationUtils getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new FieldSightNotificationUtils();
        }

        return INSTANCE;
    }

    private FieldSightNotificationUtils() {
        notificationUID = new AtomicInteger();
    }

    private static int getNotificationUID() {
        return notificationUID.addAndGet(1);
    }

    public void notifyNormal(String title, String body) {
        int id = getNotificationUID();
        NotificationCompat.Builder notification = getNotification(title, body, false, ProgressType.NONE);
        notify(id, notification);
    }

    public int notifyProgress(String title, String body, ProgressType progressType) {
        int id = getNotificationUID();
        NotificationCompat.Builder notification = getNotification(title, body, true, progressType);
        notify(id, notification);
        return id;
    }

    public void notifyHeadsUp(String title, String body) {
        int id = getNotificationUID();
        NotificationCompat.Builder notification = getNotification(title, body, true, ProgressType.NONE);
        notify(id, notification);

    }

    public static void createChannels(Context collect) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = collect.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(new NotificationChannel(
                        CHANNEL_ID,
                        collect.getString(R.string.notification_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT)
                );
            }
        }

    }

    private static NotificationCompat.Builder getNotification(String title, String body, boolean isHighPriority, ProgressType progressType) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(Collect.getInstance(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setChannelId(CHANNEL_ID);

        if (isHighPriority) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }


        switch (progressType) {
            case UPLOAD:
                builder.setOngoing(true);
                builder.setProgress(100, 0, true);
                builder.setSmallIcon(android.R.drawable.stat_sys_upload);
                break;
            case DOWNLOAD:
                builder.setOngoing(true);
                builder.setProgress(100, 0, true);
                builder.setSmallIcon(android.R.drawable.stat_sys_download);
                break;
            case NONE:
                builder.setSmallIcon(IconUtils.getNotificationAppIconFieldSight());
                break;

        }

        Context context = Collect.getInstance().getApplicationContext();
        Intent intent = new Intent(context, NotificationListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);

        return builder;
    }


    private void notify(int id, NotificationCompat.Builder notification) {
        getManager().notify(id, notification.build());
    }

    private NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) Collect.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }


    public void cancelNotification(int id) {
        NotificationManager manager = getManager();

        if (manager != null) {
            manager.cancel(id);
        }
    }

    public enum ProgressType {
        UPLOAD,
        DOWNLOAD,
        NONE
    }

}
