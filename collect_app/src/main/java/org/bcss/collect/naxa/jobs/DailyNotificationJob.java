package org.bcss.collect.naxa.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobRequest;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.preferences.SettingsKeys;
import org.bcss.collect.naxa.preferences.SettingsSharedPreferences;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsLocalSource;
import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static org.bcss.collect.naxa.preferences.SettingsKeys.KEY_NOTIFICATION_TIME_DAILY;

public class DailyNotificationJob extends DailyJob {


    public static final String TAG = "DailyNotificationJob";

    public static void schedule() {

        String time = String.valueOf(SettingsSharedPreferences.getInstance().get(KEY_NOTIFICATION_TIME_DAILY));
        String[] minuteAndTime = time.split(":");
        int hourOfDay = Integer.parseInt(minuteAndTime[0]);
        int minute = Integer.parseInt(minuteAndTime[1]);

        long startTime = TimeUnit.HOURS.toMillis(hourOfDay) + TimeUnit.MINUTES.toMillis(minute);
        long endTime = TimeUnit.MINUTES.toMillis(15);

        DailyJob.schedule(new JobRequest.Builder(TAG)
                        .setUpdateCurrent(true)
                , startTime, endTime
        );
    }

    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(Params params) {
        List<ScheduleForm> scheduleForms;
        String title;
        String message;

        if (isActivated(SettingsKeys.KEY_NOTIFICATION_SWITCH_DAILY)) {
            scheduleForms = ScheduledFormsLocalSource.getInstance().getDailyForms();
            title = "Daily Reminder";
            message = Collect.getInstance().getString(R.string.msg_form_reminder_daily, scheduleForms.size());
            FieldSightNotificationUtils.getINSTANCE().notifyNormal(title, message);
        }

        Timber.i("%s %s", isActivated(SettingsKeys.KEY_NOTIFICATION_SWITCH_WEEKLY), isWeeklySetToday());
        if (isActivated(SettingsKeys.KEY_NOTIFICATION_SWITCH_WEEKLY) && isWeeklySetToday()) {
            title = "Weekly Reminder";
            scheduleForms = ScheduledFormsLocalSource.getInstance().getDailyForms();
            message = Collect.getInstance().getString(R.string.msg_form_reminder_weekly, scheduleForms.size());
            FieldSightNotificationUtils.getINSTANCE().notifyNormal(title, message);
        }

        if (isActivated(SettingsKeys.KEY_NOTIFICATION_SWITCH_MONTHLY) && isMonthlySetToday()) {
            title = "Monthly Reminder";
            scheduleForms = ScheduledFormsLocalSource.getInstance().getDailyForms();
            message = Collect.getInstance().getString(R.string.msg_form_reminder_monthly, scheduleForms.size());
            FieldSightNotificationUtils.getINSTANCE().notifyNormal(title, message);
        }


        return DailyJobResult.SUCCESS;
    }

    private boolean isWeeklySetToday() {
        Integer selectedDate = (Integer) SettingsSharedPreferences.getInstance().get(SettingsKeys.KEY_NOTIFICATION_TIME_WEEKLY);
        DateTime dateTime = new DateTime();
        int dayOfWeek = dateTime.getDayOfWeek();
        return selectedDate == dayOfWeek;
    }

    private boolean isActivated(String key) {

        return (boolean) SettingsSharedPreferences.getInstance().get(key);
    }


    private boolean isMonthlySetToday() {
        Integer selectedDatePeriod = (Integer) SettingsSharedPreferences.getInstance().get(SettingsKeys.KEY_NOTIFICATION_TIME_MONTHLY);
        DateTime dateTime = new DateTime();
        int dayOfWeek = dateTime.getDayOfMonth();
        boolean isMonthlySetToday = false;

        switch (selectedDatePeriod) {
            case 0:
                isMonthlySetToday = dayOfWeek <= 10;
                break;
            case 1:
                isMonthlySetToday = dayOfWeek > 10 && dayOfWeek <= 20;
                break;
            case 2:
                isMonthlySetToday = dayOfWeek > 20;
                break;
        }
        return isMonthlySetToday;
    }
}
