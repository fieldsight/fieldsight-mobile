package org.fieldsight.naxa.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.loader.content.CursorLoader;

import org.fieldsight.naxa.helpers.FSInstancesDao;
import org.fieldsight.naxa.login.model.Site;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.fragments.DataManagerList;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class FSDataManagerList extends DataManagerList {
    private Site loadedSite;

    public static FSDataManagerList newInstance(Site loadedSite) {
        FSDataManagerList fsDataManagerList = new FSDataManagerList();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        fsDataManagerList.setArguments(bundle);
        return fsDataManagerList;

    }

    @Override
    protected CursorLoader getCursorLoader() {
        if (getArguments() != null) {
            loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
        }
        if (loadedSite != null) {
            return new FSInstancesDao().getSavedInstancesCursorLoaderSite(loadedSite.getId(), getSortingOrder());
        } else {
            return new InstancesDao().getSavedInstancesCursorLoader(getFilterText(), getSortingOrder());
        }
    }
}
