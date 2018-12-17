package org.bcss.collect.naxa.submissions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class TitleDescModelDiffCallback extends DiffUtil.Callback {

    private List<ViewModel> oldItems;
    private List<ViewModel> newItems;

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
