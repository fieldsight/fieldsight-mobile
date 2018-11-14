package org.bcss.collect.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.bcss.collect.android.tasks.MediaLoadingTask;
import org.bcss.collect.android.activities.FormEntryActivity;

public class MediaLoadingFragment extends Fragment {

    private MediaLoadingTask mediaLoadingTask;
    private FormEntryActivity formEntryActivity;

    public void beginMediaLoadingTask(Uri uri) {
        mediaLoadingTask = new MediaLoadingTask(formEntryActivity);
        mediaLoadingTask.execute(uri);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.formEntryActivity = (FormEntryActivity) activity;
        if (mediaLoadingTask != null) {
            mediaLoadingTask.onAttach(formEntryActivity);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mediaLoadingTask != null) {
            mediaLoadingTask.onDetach();
        }
    }
}
