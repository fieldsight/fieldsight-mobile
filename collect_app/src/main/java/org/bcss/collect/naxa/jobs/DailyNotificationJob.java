package org.bcss.collect.naxa.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobRequest;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.preferences.SettingsSharedPreferences;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsLocalSource;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
        List<ScheduleForm> scheduleForms = ScheduledFormsLocalSource.getInstance().getDailyForms();
        String title = "Daily Reminder";
        String message = Collect.getInstance().getString(R.string.msg_form_reminder_weekly, scheduleForms.size());

        FieldSightNotificationUtils.getINSTANCE().notifyNormal(title, message);
        return DailyJobResult.SUCCESS;
    }


}
