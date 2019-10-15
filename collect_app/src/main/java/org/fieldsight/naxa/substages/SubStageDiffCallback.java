package org.fieldsight.naxa.substages;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import org.fieldsight.naxa.previoussubmission.model.SubStageAndSubmission;

import java.util.List;


public class SubStageDiffCallback extends DiffUtil.Callback {

    private final List<SubStageAndSubmission> oldSubStages;
    private final List<SubStageAndSubmission> newSubStages;


    public SubStageDiffCallback(List<SubStageAndSubmission> newSubStages, List<SubStageAndSubmission> oldSubStages) {
        this.newSubStages = newSubStages;
        this.oldSubStages = oldSubStages;
    }

    @Override
    public int getOldListSize() {

        return oldSubStages.size();
    }

    @Override
    public int getNewListSize() {
        return newSubStages.size();
    }


    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldSubStages.get(oldItemPosition).getSubStage().getId()
                .equals(newSubStages.get(newItemPosition).getSubStage().getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldSubStages.get(oldItemPosition).equals(newSubStages.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

