package org.bcss.collect.naxa.notificationslist;

import android.support.v7.util.DiffUtil;


import org.bcss.collect.naxa.data.FieldSightNotification;

import java.util.ArrayList;
import java.util.List;

public class FieldSightNotificationsDiffCallback extends DiffUtil.Callback {
    private final List<FieldSightNotification> newList;
    private final ArrayList<FieldSightNotification> oldList;

    public FieldSightNotificationsDiffCallback(List<FieldSightNotification> newList, ArrayList<FieldSightNotification> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        int oldId = oldList.get(oldItemPosition).getId();
        int newId = newList.get(newItemPosition).getId();
        return oldId == newId;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
