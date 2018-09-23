package org.bcss.collect.naxa.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.notificationslist.NotificationListActivity;

import java.util.concurrent.atomic.AtomicInteger;


public class NotificationUtils {


    public static int DEFAULT_DOWNLOAD_NOTIFICATION_ID = 1;
    private static int uniqueMessageId = DEFAULT_DOWNLOAD_NOTIFICATION_ID + 1;
    private static NotificationManager mNotifyManager;
    private static NotificationCompat.Builder mBuilder;
    public static String DOWNLOAD_ACTION = "download.action";
    private static int smallIcon = R.drawable.ic_notification_icon;

    public NotificationUtils() {

    }

    private static int getUniqueMessageId() {
        return uniqueMessageId++;
    }


    public int notifyAction(Context ctx, String title, String content, String actionName) {
        int id = getUniqueMessageId();
        sendNotification(
                id,
                ctx,
                createNotification(ctx.getApplicationContext(), content,
                        title, false,
                        null, actionName));
        return id;
    }


    public static int notifyNormal(Context ctx, String title, String content) {

        int id = getUniqueMessageId();
        sendNotification(
                id,
                ctx,
                createNotification(ctx.getApplicationContext(), content,
                        title, false,
                        null, null));

        return id;
    }

    public int notifyOnGoing(Context ctx, String title, String content, String actionName) {
        int id = getUniqueMessageId();
        sendNotification(
                id,
                ctx,
                createNotification(ctx.getApplicationContext(), content,
                        title, true,
                        null, actionName));

        return id;
    }

    public static int createProgressNotification(Context ctx, String text) {


        mNotifyManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(ctx);
        mBuilder.setContentTitle(ctx.getString(R.string.app_name))
                .setContentText(text)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker(text);

        mBuilder.setProgress(100, 0, false);


        return 8789;
    }

    public static void createStackNotification(Context context, String groupId, Intent intent, String contentTitle, String contentText) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent p = intent != null ? PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT) : null;

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(p)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(smallIcon)
                .setGroup(groupId)
                .setShowWhen(true)
                .setColor(Color.BLUE)
                .setLocalOnly(true)
                .setAutoCancel(true);

        Notification n = builder.build();
        manager.notify(getID(), n);


    }


    public static int createUploadNotification(int notificationId, String title) {
        Context context = Collect.getInstance();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setOngoing(true)
                .setProgress(100, 0, true)
                .setGroup("upload");

        Notification n = builder.build();

        if (manager != null) {
            manager.notify(notificationId, n);
        }

        return notificationId;
    }


    public static int createProgressNotification(int notificationId, String title) {
        Context context = Collect.getInstance();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setOngoing(true)
                .setProgress(100, 0, true)
                .setGroup("download");

        Notification n = builder.build();

        if (manager != null) {
            manager.notify(notificationId, n);
        }

        return notificationId;
    }


    public static int createProgressNotification() {
        // int id = getID();
        int id = 5929;
        Context context = Collect.getInstance();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setAutoCancel(true)
                .setContentTitle("Looking for educational materials")
                .setProgress(100, 0, true)
                .setGroup("download");

        Notification n = builder.build();

        if (manager != null) {
            manager.notify(id, n);
        }

        return id;

    }

    static void finishedProgressNotification(int notificationId, String msg) {
        Context context = Collect.getInstance();
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentText(msg);

        Notification n = builder.build();
        n.flags = Notification.FLAG_ONGOING_EVENT;


        if (manager != null) {
            manager.notify(notificationId, n);
        }

    }


    public static void notifyHeadsUp(String title, String msg) {
        int notificationId = getID();
        Context context = Collect.getInstance();
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setTicker(msg)
                .setContentText(msg);

        if (Build.VERSION.SDK_INT >= 21) {
            builder.setVibrate(new long[0]);
        } else {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        Notification n = builder.build();


        if (manager != null) {
            manager.notify(notificationId, n);
        }


    }

    public static void updateProgressNotification(int notificationId, String msg, int total, int progress) {

        Context context = Collect.getInstance();
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentText(msg)
                .setProgress(total, progress, false);

        Notification n = builder.build();


        if (manager != null) {
            manager.notify(notificationId, n);
        }
    }

    public static int createProgressNotification(Context ctx) {


        mNotifyManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(ctx);
        mBuilder.setContentTitle(ctx.getString(R.string.app_name))
                .setContentText("Downloading Forms")
                .setSmallIcon(android.R.drawable.stat_sys_download).setTicker("Downloading Forms");

        mBuilder.setProgress(100, 0, false);


        return DEFAULT_DOWNLOAD_NOTIFICATION_ID;
    }


    public static void updateProgressNotification(int notificaitonId, int progressValue, int totalValue, String msg) {

        mBuilder.setProgress(totalValue, progressValue, false);
        mBuilder.setContentText(msg);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotifyManager.notify(notificaitonId, notification);
    }

    public static void stopProgressNotification(int notificaitonId, String msg) {
        mBuilder.setContentText(msg);
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mNotifyManager.notify(notificaitonId, mBuilder.build());
    }


    private static void sendNotification(int id, Context ctx,
                                         Notification notification) {
        NotificationManager mNotificationManager = (NotificationManager) ctx
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(id, notification);
    }


    /**
     * Creates a notification given a context and ticker and context parameters
     *
     * @param ctx                   the application context
     * @param tickerText            the string to use for the ticker text
     * @param contentText           the string to use for the content text
     * @param isOngoingNotification whether or not this is an ongoing notification (i.e.
     *                              server status) or transient (i.e. server message) notification
     * @param notificationSound     the notification sound to use (not used for ongoing
     *                              notifications. Default used if equal to null)
     **/
    private static Notification createNotification(Context ctx,
                                                   CharSequence tickerText, CharSequence contentText,
                                                   boolean isOngoingNotification, String notificationSound, String actionName) {
        int icon = R.drawable.ic_notification_icon;
        long when = System.currentTimeMillis();

        Intent toNotifications = new Intent(ctx, NotificationListActivity.class);
        toNotifications.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingToNotifications = PendingIntent.getActivity(ctx, 0, toNotifications,
                PendingIntent.FLAG_ONE_SHOT);


        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(icon).setContentText(tickerText).setWhen(when)
                .setContentTitle(contentText)
                .setTicker(tickerText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(tickerText)).setContentIntent(pendingToNotifications);


        Notification notification = notificationBuilder.build();

        if (isOngoingNotification) {
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.defaults = 0;
            notification.tickerText = null;
        } else {
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults = Notification.DEFAULT_ALL;

            if (notificationSound != null) {
                notification.defaults = notification.defaults
                        & ~Notification.DEFAULT_SOUND;
                notification.sound = Uri.parse(notificationSound);
            }
        }
        return notification;
    }

    /**
     * Cancels a notification
     *
     * @param id the notification number to cancel
     **/
    public static void cancelNotification(int id) {
        Context context = Collect.getInstance();
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            manager.cancel(id);
        }
    }

    private final static AtomicInteger c = new AtomicInteger(0);

    public static int getID() {
        return c.incrementAndGet();
    }

    public static int notifyAPI(String title, String msg) {
        int id = 5930;
        Context context = Collect.getInstance();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentTitle(msg)
                .setProgress(100, 0, true)
                .setGroup("download");
        Notification n = builder.build();

        if (manager != null) {
            manager.notify(id, n);
        }

        return id;
    }


}


