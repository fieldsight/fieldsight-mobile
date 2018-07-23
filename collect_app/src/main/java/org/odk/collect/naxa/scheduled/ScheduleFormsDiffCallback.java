package org.odk.collect.naxa.scheduled;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import org.odk.collect.naxa.scheduled.data.ScheduleForm;

import java.util.List;

/**
 * Created by nishon on 2/15/18.
 */

public class ScheduleFormsDiffCallback extends DiffUtil.Callback {

    private List<ScheduleForm> oldScheduleForm;
    private List<ScheduleForm> newScheduleForm;

    public ScheduleFormsDiffCallback(List<ScheduleForm> oldScheduleForm, List<ScheduleForm> newScheduleForm) {
        this.oldScheduleForm = oldScheduleForm;
        this.newScheduleForm = newScheduleForm;
    }

    @Override
    public int getOldListSize() {
        return oldScheduleForm.size();
    }

    @Override
    public int getNewListSize() {
        return newScheduleForm.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldScheduleForm.get(oldItemPosition).getFsFormId()
                .equals(newScheduleForm.get(newItemPosition).getFsFormId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldScheduleForm.get(oldItemPosition).equals(newScheduleForm.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
