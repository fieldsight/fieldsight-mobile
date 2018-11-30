package org.bcss.collect.naxa.sync;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import org.bcss.collect.naxa.sync.Sync;

import java.util.List;

public class DownloadableItemsDiffCallbackNew extends DiffUtil.Callback {

    private List<Sync> oldItems;
    private List<Sync> newItems;


    public DownloadableItemsDiffCallbackNew(List<Sync> newItems, List<Sync> oldItems) {
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
        int oldUId = oldItems.get(oldItemPosition).getUid();
        int newUId = newItems.get(newItemPosition).getUid();

        return oldUId == newUId;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
