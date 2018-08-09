package org.bcss.collect.naxa.site;

import android.support.v7.util.DiffUtil;

import org.bcss.collect.naxa.login.model.Site;

import java.util.List;

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
        Site oldSite = oldList.get(oldItemPosition);
        Site newSite = newList.get(newItemPosition);

        return oldSite.getId().equals(newSite.getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Site oldSite = oldList.get(oldItemPosition);
        Site newSite = newList.get(newItemPosition);

        return newSite.equals(oldSite);


    }

}
