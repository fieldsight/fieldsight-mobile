package org.fieldsight.naxa;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.content.Loader;

import org.fieldsight.naxa.helpers.FSInstancesDao;
import org.fieldsight.naxa.login.model.Site;
import org.odk.collect.android.activities.InstanceUploaderListActivity;

import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;


public class FSInstanceUploaderListActivity extends InstanceUploaderListActivity {
    private Site loadedSite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Timber.i("is in oncreate");
        if (savedInstanceState != null) {
            loadedSite = savedInstanceState.getParcelable(EXTRA_OBJECT);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (loadedSite != null) {
            outState.putParcelable(EXTRA_OBJECT, loadedSite);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        FSInstancesDao fsInstancesDao = new FSInstancesDao();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            loadedSite = bundle.getParcelable(EXTRA_OBJECT);
        }

        if (showAllMode) {
            if (loadedSite != null) {
                return fsInstancesDao.getCompletedUndeletedInstancesCursorLoaderBySite(loadedSite.getId(), getSortingOrder());
            } else {
                return fsInstancesDao.getCompletedUndeletedInstancesCursorLoaderHideOfflineSite(getFilterText(), getSortingOrder());
            }
        } else {
            if (loadedSite != null) {
                return fsInstancesDao.getFinalizedInstancesCursorLoaderBySite(loadedSite.getId(), getSortingOrder());
            } else {
                return fsInstancesDao.getFinalizedInstancesCursorLoaderHideOfflineSite(getFilterText(), getSortingOrder());
            }
        }
    }
}