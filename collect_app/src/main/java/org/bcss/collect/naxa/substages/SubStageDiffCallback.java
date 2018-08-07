package org.bcss.collect.naxa.substages;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import org.bcss.collect.naxa.stages.data.SubStage;

import java.util.List;


public class SubStageDiffCallback extends DiffUtil.Callback {

    private List<SubStage> oldSubStages;
    private List<SubStage> newSubStages;


    public SubStageDiffCallback(List<SubStage> newSubStages, List<SubStage> oldSubStages) {
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
        return oldSubStages.get(oldItemPosition).getId()
                .equals(newSubStages.get(newItemPosition).getId());
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

