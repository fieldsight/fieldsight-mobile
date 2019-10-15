package org.fieldsight.naxa.stages;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import org.fieldsight.naxa.stages.data.Stage;

import java.util.List;

/**
 * Created by nishon on 2/26/18.
 */

public class StagesDiffCallback extends DiffUtil.Callback {

    private final List<Stage> oldStages;
    private final List<Stage> newStages;


    public StagesDiffCallback(List<Stage> newStages, List<Stage> oldStages) {
        this.newStages = newStages;
        this.oldStages = oldStages;
    }

    @Override
    public int getOldListSize() {

        return oldStages.size();
    }

    @Override
    public int getNewListSize() {

        return newStages.size();
    }


    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldStages.get(oldItemPosition).getId()
                .equals(newStages.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldStages.get(oldItemPosition).equals(newStages.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

