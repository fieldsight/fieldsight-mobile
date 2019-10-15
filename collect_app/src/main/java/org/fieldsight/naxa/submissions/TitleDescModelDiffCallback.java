package org.fieldsight.naxa.submissions;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class TitleDescModelDiffCallback extends DiffUtil.Callback {

    private final List<ViewModel> oldItems;
    private final List<ViewModel> newItems;

    public TitleDescModelDiffCallback(List<ViewModel> newItems, List<ViewModel> oldItems) {
        this.newItems = newItems;
        this.oldItems = oldItems;
    }
    
    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ViewModel old = oldItems.get(oldItemPosition);
        ViewModel newItem = newItems.get(newItemPosition);

        return old.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ViewModel old = oldItems.get(oldItemPosition);
        ViewModel newItem = newItems.get(newItemPosition);
        return newItem.equals(old);
    }


    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        ViewModel old = oldItems.get(oldItemPosition);
        ViewModel newItem = newItems.get(newItemPosition);

        Bundle bundle = new Bundle();
        if(!old.equals(newItem)){
            bundle.putParcelable(EXTRA_OBJECT,newItem);
        }

        return super.getChangePayload(oldItemPosition,newItemPosition);
    }
}
