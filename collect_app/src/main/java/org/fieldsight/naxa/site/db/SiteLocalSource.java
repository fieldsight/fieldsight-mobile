package org.fieldsight.naxa.site.db;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.odk.collect.android.application.Collect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.SiteStatus.IS_ONLINE;

public class SiteLocalSource implements BaseLocalDataSource<Site> {


    private static SiteLocalSource siteLocalSource;
    private final SiteDao dao;


    private SiteLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSiteDAO();
    }


    public synchronized static SiteLocalSource getInstance() {
        if (siteLocalSource == null) {
            siteLocalSource = new SiteLocalSource();
        }
        return siteLocalSource;
    }

    public int getOfflineSiteCount(String id) {
        return dao.getOfflineSiteCount(id);
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

    public LiveData<List<Site>> getAllParentSite(String projectId) {
        return dao.getParentSite(projectId);
    }

    public LiveData<List<String>> getAllDistinctProjectIds() {
        return dao.getAllDistictProject();
    }


    public LiveData<List<Site>> getByIdAndSiteStatus(String projectId, int status) {
        return dao.getByIdOfflineSites(projectId, status);
    }

    public LiveData<List<Site>> getByIdStatusAndCluster(String projectId, String cluster) {

        return dao.getSiteFromFilter(projectId, cluster);
    }

    public LiveData<List<Site>> getByIdStatusAndClusterAnStatus(String projectId, String cluster,int status) {

        return dao.getSiteFromFilterV2(projectId, cluster,status);
    }

    public List<Site> getSitesByParentId(String siteId) {
        return dao.getSiteByParentId(siteId);
    }

    public LiveData<Site> getBySiteIdAsLiveData(String siteId) {
        return dao.getSiteByIdAsLive(siteId);
    }

    public Site getBySiteId(String id) {
        return dao.getSiteById(id);
    }


    public boolean isSiteOffline(String siteId) {
        return dao.getSiteById(siteId).getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE;
    }


    //todo return affected rows count
    public LiveData<Integer> delete(Site site) {
        MutableLiveData<Integer> affectedRowsMutData = new MutableLiveData<>();
        AsyncTask.execute(() -> affectedRowsMutData.postValue(dao.delete(site)));

        return affectedRowsMutData;
    }


    public List<Site> searchSites(String searchQuery, String projectId) {

        return dao.searchSites(searchQuery, projectId);
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
        AsyncTask.execute(() -> {
            long[] rowAffected = dao.insert(items);
            Timber.i("Saving %d Total affected row = %d in saving ", items.size(), rowAffected.length);
        });
    }

    @Override
    public void updateAll(ArrayList<Site> items) {

    }

    public void setSiteAsNotFinalized(String siteId) {
        AsyncTask.execute(() -> {
            dao.updateSiteStatus(siteId, Constant.SiteStatus.IS_OFFLINE);
        });

    }

    public void setSiteAsFinalized(String siteId) {
        AsyncTask.execute(() -> {
            dao.updateSiteStatus(siteId, Constant.SiteStatus.IS_FINALIZED);
        });

    }

    public Observable<Integer> setSiteAsVerified(String oldSiteId) {
        return updateSiteStatus(oldSiteId, IS_ONLINE);
    }

    public Observable<Integer> updateSiteId(String oldSiteId, String newSiteId) {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() {
                return dao.updateSiteId(oldSiteId, newSiteId);
            }
        });
    }


    public Observable<Integer> updateSiteStatus(String siteId, int newStatus) {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() {
                return dao.updateSiteStatus(siteId, newStatus);
            }
        });
    }


    public void deleteSyncedSitesAsync() {
        AsyncTask.execute(() -> {
            dao.deleteSyncedSites(IS_ONLINE);
        });
    }

    public void deleteSyncedSites(String... selectedProjectIds) {
        dao.deleteSyncedSites(IS_ONLINE);

    }

    public void updateSiteIdAsync(String siteId, int siteStatus) {
        AsyncTask.execute(() -> {
            dao.updateSiteStatus(siteId, siteStatus);
        });
    }

    public Single<List<Site>> getAllByStatus(int siteStatus) {
        return dao.getAllByStatus(siteStatus);
    }

    public Single<Site> getSiteByIdAsSingle(String siteId) {
        return dao.getSiteByIdAsSingle(siteId);
    }
}
