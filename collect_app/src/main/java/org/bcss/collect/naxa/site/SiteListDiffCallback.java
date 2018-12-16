package org.bcss.collect.naxa.site;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import org.bcss.collect.naxa.login.model.Site;

import java.util.List;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

/**
 * Created on 12/21/17
 * by nishon.tan@gmail.com
 */

public class SiteListDiffCallback extends DiffUtil.Callback {

    private List<Site> oldList;
    private List<Site> newList;

    public SiteListDiffCallback(List<Site> oldList, List<Site> newList) {
        this.oldList = oldList;
        this.newList = newList;
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
        Site oldItem = oldList.get(oldItemPosition);
        Site newItem = newList.get(newItemPosition);

        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Site oldItem = oldList.get(oldItemPosition);
        Site newItem = newList.get(newItemPosition);
        return newItem.equals(oldItem);
    }


    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Site oldItem = oldList.get(oldItemPosition);
        Site newItem = newList.get(newItemPosition);

        Bundle bundle = new Bundle();
        if(!oldItem.equals(newItem)){
            bundle.putParcelable(EXTRA_OBJECT,newItem);

        }

        return super.getChangePayload(oldItemPosition,newItemPosition);
    }

}
