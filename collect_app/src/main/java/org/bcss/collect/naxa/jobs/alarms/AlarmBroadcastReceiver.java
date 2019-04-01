package org.bcss.collect.naxa.jobs.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsLocalSource;

import java.util.Calendar;
import java.util.List;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        List<ScheduleForm> scheduleForms = ScheduledFormsLocalSource.getInstance().getDailyForms();
        String title = "Daily Reminder";
        String message = Collect.getInstance().getString(R.string.msg_form_reminder_daily, scheduleForms.size());

        FieldSightNotificationUtils.getINSTANCE().notifyNormal(title, message);
    }

    public static void setRepeatingEveryWeek(Context context, Calendar calendar) {
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }
}
