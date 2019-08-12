package org.fieldsight.naxa.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.loader.content.CursorLoader;

import org.fieldsight.naxa.helpers.FSInstancesDao;
import org.fieldsight.naxa.login.model.Site;
import org.odk.collect.android.dao.InstancesDao;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class DataManagerList extends org.odk.collect.android.fragments.DataManagerList {
    private Site loadedSite;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected CursorLoader getCursorLoader() {
        if (loadedSite != null) {
            return new FSInstancesDao().getSavedInstancesCursorLoaderSite(loadedSite.getId(), getSortingOrder());
        } else {
            return new InstancesDao().getSavedInstancesCursorLoader(getFilterText(), getSortingOrder());
        }
    }
}
