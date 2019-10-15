package org.fieldsight.naxa.sync;



import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DownloadableItemsDiffCallbackNew extends DiffUtil.Callback {

    private final List<DownloadableItem> oldItems;
    private final List<DownloadableItem> newItems;


    public DownloadableItemsDiffCallbackNew(List<DownloadableItem> newItems, List<DownloadableItem> oldItems) {
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
