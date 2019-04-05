package org.bcss.collect.naxa.jobs;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.data.FieldSightNotificationBuilder;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
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

        FieldSightNotification fieldSightNotification;

        if (isActivated(SettingsKeys.KEY_NOTIFICATION_SWITCH_DAILY)) {
            scheduleForms = ScheduledFormsLocalSource.getInstance().getDailyForms();
            fieldSightNotification = new FieldSightNotificationBuilder()
                    .setNotificationType(Constant.NotificationType.DAILY_REMINDER)
                    .setSheduleFormsCount(String.valueOf(scheduleForms.size()))
                    .createFieldSightNotification();

            saveAndShowNotification(fieldSightNotification);
        }

        Timber.i("%s %s", isActivated(SettingsKeys.KEY_NOTIFICATION_SWITCH_WEEKLY), isWeeklySetToday());
        if (isActivated(SettingsKeys.KEY_NOTIFICATION_SWITCH_WEEKLY) && isWeeklySetToday()) {
            scheduleForms = ScheduledFormsLocalSource.getInstance().getWeeklyForms();
            fieldSightNotification = new FieldSightNotificationBuilder()
                    .setNotificationType(Constant.NotificationType.WEEKLY_REMINDER)
                    .setSheduleFormsCount(String.valueOf(scheduleForms.size()))
                    .createFieldSightNotification();

            saveAndShowNotification(fieldSightNotification);
        }

        if (isActivated(SettingsKeys.KEY_NOTIFICATION_SWITCH_MONTHLY) && isMonthlySetToday()) {
            scheduleForms = ScheduledFormsLocalSource.getInstance().getMonthlyForms();
            fieldSightNotification = new FieldSightNotificationBuilder()
                    .setNotificationType(Constant.NotificationType.MONTHLY_REMINDER)
                    .setSheduleFormsCount(String.valueOf(scheduleForms.size()))
                    .createFieldSightNotification();

            saveAndShowNotification(fieldSightNotification);
        }


        return DailyJobResult.SUCCESS;
    }

    private void saveAndShowNotification(FieldSightNotification fieldSightNotification) {
        Pair<String, String> titleContent = FieldSightNotificationLocalSource.getInstance().generateNotificationContent(fieldSightNotification);
        FieldSightNotificationLocalSource.getInstance().save(fieldSightNotification);
        FieldSightNotificationUtils.getINSTANCE().notifyNormal(titleContent.first, titleContent.second);
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
        int dayOfMonth = dateTime.getDayOfMonth();
        boolean isMonthlySetToday = false;

        switch (selectedDatePeriod) {
            case 0:
                isMonthlySetToday = dayOfMonth <= 10;
                break;
            case 1:
                isMonthlySetToday = dayOfMonth > 10 && dayOfMonth <= 20;
                break;
            case 2:
                isMonthlySetToday = dayOfMonth > 20;
                break;
        }
        return isMonthlySetToday;
    }
}
