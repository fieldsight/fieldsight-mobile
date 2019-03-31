package org.bcss.collect.naxa.jobs.alarms;

import android.content.Context;
import android.support.annotation.NonNull;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsLocalSource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DailyNotificationWorker extends Worker {
    public DailyNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
//        https://stackoverflow.com/questions/50363541/schedule-a-work-on-a-specific-time-with-workmanager
        List<ScheduleForm> scheduleForms = ScheduledFormsLocalSource.getInstance().getDailyForms();
        String title = "Daily Reminder";
        String message = Collect.getInstance().getString(R.string.msg_form_reminder_daily, scheduleForms.size());

        FieldSightNotificationUtils.getINSTANCE().notifyNormal(title, message);
        return Result.SUCCESS;
    }

    private void requestDailyNotification() {
        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.Builder(DailyNotificationWorker.class, 1, TimeUnit.HOURS)
                .addTag(DailyNotificationWorker.class.getName())
                .build();


        WorkManager.getInstance().enqueue(notificationWork);
    }
}
