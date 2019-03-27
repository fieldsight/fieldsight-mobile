package org.bcss.collect.naxa.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsLocalSource;

import java.util.List;

public class LocalNotificationJob extends Job {

    public static final String TAG = "localNotificaionJob";
    private static final long FIFTEEN_MINUTES_PERIOD = 900000;
    private static final long ONE_DAY_PERIOD = 86400000;
    private static final long FLEX_PERIOD = 300000;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        String title = "Daily Reminder";
        List<ScheduleForm> scheduleForms = ScheduledFormsLocalSource.getInstance().getDailyForms();
//        if (scheduleForms.size() != 0) {
//            String message = Collect.getInstance().getString(R.string.msg_form_reminder_daily, scheduleForms.size());
//            FieldSightNotificationUtils.getINSTANCE().notifyNormal(title, message);
//        }

        String message = Collect.getInstance().getString(R.string.msg_form_reminder_daily, scheduleForms.size());
//        FieldSightNotificationUtils.getINSTANCE().notifyNormal(title, message);
        FieldSightNotificationUtils.getINSTANCE().notifyGroup();
        return Result.SUCCESS;
    }

    public static void schedulePeriodicJob(String selectedOption) {
        if (selectedOption.equals(Collect.getInstance().getString(R.string.never_value))) {
            JobManager.instance().cancelAllForTag(TAG);
        } else {
            long period = FIFTEEN_MINUTES_PERIOD;
            if (selectedOption.equals(Collect.getInstance().getString(R.string.every_24_hours_value))) {
                period = ONE_DAY_PERIOD;
            }

            new JobRequest.Builder(TAG)
                    .setPeriodic(period, FLEX_PERIOD)
                    .setUpdateCurrent(true)
                    .setRequirementsEnforced(true)
                    .build()
                    .schedule();
        }
    }

    public static void runJobImmediately() {
        new JobRequest.Builder(TAG)
                .startNow()
                .build()
                .schedule();
    }

}