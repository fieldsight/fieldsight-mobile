package org.bcss.collect.naxa.site.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.login.model.Site;

import java.util.ArrayList;
import java.util.List;

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


    public LiveData<List<Site>> getByIdAndSiteStatus(String projectId, int status) {
        return dao.getByIdOfflineSites(projectId, status);
    }

    public LiveData<List<Site>> getByIdStatusAndCluster(String projectId, String cluster) {

        return dao.getSiteFromFilter(projectId, cluster);
    }

    public LiveData<List<Site>> getBySiteId(String siteId) {
        return dao.getSiteById(siteId);
    }

    //todo return affected rows count
    public LiveData<Integer> delete(Site site) {
        MutableLiveData<Integer> affectedRowsMutData = new MutableLiveData<>();
        AsyncTask.execute(() -> affectedRowsMutData.postValue(dao.delete(site)));

        return affectedRowsMutData;
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
        AsyncTask.execute(() -> {
            long i = dao.updateSiteStatus(siteId, Constant.SiteStatus.IS_UNVERIFIED_SITE);
        });

    }

    public void setSiteAsFinalized(String siteId) {
        AsyncTask.execute(() -> {
            long i = dao.updateSiteStatus(siteId, Constant.SiteStatus.IS_FINALIZED);
        });

    }

    public void setSiteAsVerified(String siteId) {
        AsyncTask.execute(() -> {
            long i = dao.updateSiteStatus(siteId, Constant.SiteStatus.IS_OFFLINE_SITE_SYNCED);
        });
    }

    public void setSiteId(String oldSiteId, String newSiteId) {
        AsyncTask.execute(() -> dao.updateSiteId(oldSiteId, newSiteId));
    }


}
