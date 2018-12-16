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
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Action;

import static org.bcss.collect.naxa.common.Constant.SiteStatus.IS_ONLINE;

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

    public Single<List<Site>> getByIdAsSingle(String projectId) {
        return dao.getSiteByProjectIdAsSingle(projectId);
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

    public Completable saveAsCompletable(Site... sites) {
        return Completable.fromAction(() -> dao.insert(sites));
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
            long i = dao.updateSiteStatus(siteId, Constant.SiteStatus.IS_OFFLINE);
        });

    }

    public void setSiteAsFinalized(String siteId) {
        AsyncTask.execute(() -> {
            long i = dao.updateSiteStatus(siteId, Constant.SiteStatus.IS_FINALIZED);
        });

    }

    public Observable<Integer> setSiteAsVerified(String oldSiteId) {
        return updateSiteStatus(oldSiteId, IS_ONLINE);
    }

    public Observable<Integer> updateSiteId(String oldSiteId, String newSiteId) {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return dao.updateSiteId(oldSiteId, newSiteId);
            }
        });
    }


    public Observable<Integer> updateSiteStatus(String siteId, int newStatus) {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return dao.updateSiteStatus(siteId, newStatus);
            }
        });
    }


    public void deleteSyncedSitesAsync() {
        AsyncTask.execute(() -> {
            dao.deleteSyncedSites(IS_ONLINE);
        });
    }

    public Single<Site> getAllByStatus(int siteStatus) {
        return dao.getAllByStatus(siteStatus);
    }
}
