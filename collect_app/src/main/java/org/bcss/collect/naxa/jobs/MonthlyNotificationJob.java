package org.bcss.collect.naxa.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsLocalSource;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MonthlyNotificationJob extends Job {

    public static final String TAG = "MonthlyNotificationJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        List<ScheduleForm> scheduleForms = ScheduledFormsLocalSource.getInstance().getDailyForms();
        String title = "Monthly Reminder";
        String message = Collect.getInstance().getString(R.string.msg_form_reminder_weekly, scheduleForms.size());

        FieldSightNotificationUtils.getINSTANCE().notifyNormal(title, message);
        return Job.Result.SUCCESS;
    }

    public static void schedule() {
        long flexPeriod = TimeUnit.MINUTES.toMillis(15);
        new JobRequest.Builder(MonthlyNotificationJob.TAG)
                .setPeriodic(TimeUnit.DAYS.toMillis(30), flexPeriod)
                .setUpdateCurrent(true)
                .setRequirementsEnforced(true)
                .build()
                .schedule();

    }

}
