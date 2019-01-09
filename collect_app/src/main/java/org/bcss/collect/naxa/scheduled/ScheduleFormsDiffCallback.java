package org.bcss.collect.naxa.scheduled;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import org.bcss.collect.naxa.previoussubmission.model.ScheduledFormAndSubmission;

import java.util.List;

/**
 * Created by nishon on 2/15/18.
 */

public class ScheduleFormsDiffCallback extends DiffUtil.Callback {

    private List<ScheduledFormAndSubmission> oldScheduleForm;
    private List<ScheduledFormAndSubmission> newScheduleForm;

    public ScheduleFormsDiffCallback(List<ScheduledFormAndSubmission> oldScheduleForm, List<ScheduledFormAndSubmission> newScheduleForm) {
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
        return oldScheduleForm.get(oldItemPosition).getScheduleForm().getFsFormId()
                .equals(newScheduleForm.get(newItemPosition).getScheduleForm().getFsFormId());
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
