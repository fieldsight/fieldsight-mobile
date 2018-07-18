package org.odk.collect.naxa.site.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.naxa.common.FieldSightDatabase;
import org.odk.collect.naxa.login.model.Site;

import java.util.List;

public class SiteRepository {

    private SiteDao mSiteDao;

    public SiteRepository(Application application) {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(application);
        this.mSiteDao = database.getSiteDAO();
    }


    public List<Site> searchSites(String searchQuery){
        return mSiteDao.searchSites(searchQuery);
    }

    public LiveData<List<Site>> getAllSites() {
        return mSiteDao.getSites();
    }

    public void insert(Site...siteModel) {
        new insertAsyncTask(mSiteDao).execute(siteModel);
    }

    public LiveData<List<Site>> getSiteByProjectId(String projectID) {
        return mSiteDao.getSiteByProjectId(projectID);
    }

    private static class insertAsyncTask extends AsyncTask<Site, Void, Void> {

        private SiteDao mAsyncTaskDao;

        insertAsyncTask(SiteDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Site... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
