package org.bcss.collect.naxa.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobRequest;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsLocalSource;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DailyNotificaitonJob extends DailyJob {

    public static final String TAG = "DailyNotificaitonJob";

    public static void schedule(int hourOfDay) {
        int flexHour = hourOfDay + 1;
        DailyJob.schedule(new JobRequest.Builder(TAG)
                        .setUpdateCurrent(true),
                TimeUnit.HOURS.toMillis(hourOfDay),
                TimeUnit.HOURS.toMillis(flexHour));
    }

    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(Params params) {
        List<ScheduleForm> scheduleForms = ScheduledFormsLocalSource.getInstance().getDailyForms();
        String title = "Daily Reminder";
        String message = Collect.getInstance().getString(R.string.msg_form_reminder_daily, scheduleForms.size());

        FieldSightNotificationUtils.getINSTANCE().notifyNormal(title, message);
        return DailyJobResult.SUCCESS;
    }
}
