package org.bcss.collect.naxa.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class FieldSightJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case LocalNotificationJob.TAG:
                return new LocalNotificationJob();

            default:
                return null;
        }
    }
}
