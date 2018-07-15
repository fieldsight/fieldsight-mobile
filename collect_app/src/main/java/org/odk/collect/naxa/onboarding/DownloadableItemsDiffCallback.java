package org.odk.collect.naxa.onboarding;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import org.odk.collect.naxa.generalforms.GeneralForm;

import java.util.List;

public class DownloadableItemsDiffCallback extends DiffUtil.Callback {

    private List<DownloadableItem> oldItems;
    private List<DownloadableItem> newItems;


    public DownloadableItemsDiffCallback(List<DownloadableItem> newItems, List<DownloadableItem> oldItems) {
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
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
