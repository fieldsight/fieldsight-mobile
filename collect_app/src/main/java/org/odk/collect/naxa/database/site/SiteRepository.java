package org.odk.collect.naxa.database.site;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.naxa.database.FieldSightRoomDatabase;

import java.util.List;

public class SiteRepository {

    private SiteDao mSiteDao;
    private LiveData<List<SiteModel>> mAllSiteModel;

    public SiteRepository(Application application) {
        FieldSightRoomDatabase database = FieldSightRoomDatabase.getDatabase(application);
        this.mSiteDao = database.siteDao();
        this.mAllSiteModel = mSiteDao.getAllSites();
    }

    LiveData<List<SiteModel>> getAllWords() {
        return mAllSiteModel;
    }

    public void insert(SiteModel siteModel) {
        new insertAsyncTask(mSiteDao).execute(siteModel);
    }

    private static class insertAsyncTask extends AsyncTask<SiteModel, Void, Void> {

        private SiteDao mAsyncTaskDao;

        insertAsyncTask(SiteDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SiteModel... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
