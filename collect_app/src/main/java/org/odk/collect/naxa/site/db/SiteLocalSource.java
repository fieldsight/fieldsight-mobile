package org.odk.collect.naxa.site.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.FieldSightDatabase;
import org.odk.collect.naxa.common.SingleLiveEvent;
import org.odk.collect.naxa.login.model.Site;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SiteLocalSource implements BaseLocalDataSource<Site> {


    private static SiteLocalSource INSTANCE;
    private SiteDao dao;


    private SiteLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSiteDAO();
    }


    public static SiteLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SiteLocalSource();
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<Site>> getAll() {
        //not implemented
        return null;
    }

    public LiveData<List<Site>> getById(String projectId) {
        return dao.getSiteByProjectId(projectId);
    }

    //todo return affected rows count
    public void delete(Site site) {
        AsyncTask.execute(() -> dao.delete(site));
    }


    public List<Site> searchSites(String searchQuery) {
        return dao.searchSites(searchQuery);
    }

    @Override
    public void save(Site... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<Site> items) {
        //AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<Site> items) {

    }

    public void setSiteAsNotFinalized(String siteId) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                long i = dao.update(siteId, Constant.SiteStatus.IS_UNVERIFIED_SITE);
                Timber.i("Nishon %s", i);
            }
        });

    }

    public void setSiteAsFinalized(String siteId) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                long i = dao.update(siteId, Constant.SiteStatus.IS_FINALIZED);
                Timber.i("Nishon %s", i);
            }
        });

    }
}
