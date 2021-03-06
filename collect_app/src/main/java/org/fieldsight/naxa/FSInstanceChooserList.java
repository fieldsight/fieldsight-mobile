package org.fieldsight.naxa;


import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.content.Loader;

import org.fieldsight.naxa.helpers.FSInstancesDao;
import org.fieldsight.naxa.login.model.Site;
import org.odk.collect.android.activities.InstanceChooserList;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class FSInstanceChooserList extends InstanceChooserList {

    private Site loadedSite;

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        showProgressBar();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            loadedSite = bundle.getParcelable(EXTRA_OBJECT);
        }
        FSInstancesDao instancesDao = new FSInstancesDao();
        if (editMode) {
            if (loadedSite != null) {
                return instancesDao.getUnsentInstancesCursorLoaderBySite(loadedSite.getId(), getSortingOrder());
            } else {
                return instancesDao.getUnsentInstancesCursorLoader(getFilterText(), getSortingOrder());
            }
        } else {
            if (loadedSite != null) {
                return instancesDao.getSentInstancesCursorLoaderSite(loadedSite.getId(), getSortingOrder());
            } else {
                return instancesDao.getSentInstancesCursorLoader(getFilterText(), getSortingOrder());
            }
        }
    }
}