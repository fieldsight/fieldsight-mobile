package org.fieldsight.naxa.v3.network;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import org.fieldsight.naxa.login.model.Project;
import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.FieldSightDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import timber.log.Timber;

public class SyncLocalSource3 implements BaseLocalDataSource<SyncStat> {


    private static SyncLocalSource3 syncLocalSource3;
    private final SyncDaoV3 dao;


    private SyncLocalSource3() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSyncDaoV3();
    }

    public void setProjectCancelled(String... projectId) {
        this.dao.setSyncCancelled(projectId);
    }

    public void removeCancelledProject() {
        this.dao.removeCancelledSync();
    }


    public synchronized static SyncLocalSource3 getInstance() {
        if (syncLocalSource3 == null) {
            syncLocalSource3 = new SyncLocalSource3();
        }
        return syncLocalSource3;
    }

    @Override
    public LiveData<List<SyncStat>> getAll() {
        return dao.all();
    }


    public void delete(SyncStat stat) {

        AsyncTask.execute(() -> dao.delete(stat));
    }

    public LiveData<Integer> getCountByStatus(int... status) {
        return dao.countByStatus(status);
    }


    @Override
    public void save(SyncStat... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    public void markAsQueued(String projectId, int type) {
        SyncStat syncStat = new SyncStat(projectId, String.valueOf(type) , "", false, Constant.DownloadStatus.QUEUED, System.currentTimeMillis());
        save(syncStat);
    }

    public void markAsFailed(String projectId, int type, String failedUrl) {
        SyncStat syncStat = new SyncStat(projectId, String.valueOf(type), failedUrl, false, Constant.DownloadStatus.FAILED, System.currentTimeMillis());
        save(syncStat);
    }

    public void markAsCompleted(String projectId, int type) {
        SyncStat syncStat = new SyncStat(projectId, String.valueOf(type), "", false, Constant.DownloadStatus.COMPLETED, System.currentTimeMillis());
        save(syncStat);
    }

    public void deleteByIds(String... projectIds) {
        dao.deleteByIds(projectIds);
    }

    @Override
    public void save(ArrayList<SyncStat> items) {
        throw new RuntimeException("Not Implemented yet");
    }

    @Override
    public void updateAll(ArrayList<SyncStat> items) {
        throw new RuntimeException("Not Implemented yet");
    }

    public void update(SyncStat stat) {
        AsyncTask.execute(() -> dao.updateAll(stat));
    }

    public void delete() {
        dao.delete();
    }

    public void deleteByid(String projectId) {
        dao.deleteById(projectId);
    }

    public LiveData<List<ProjectNameTuple>> getAllSiteSyncingProject() {
        return dao.getAllSiteSyncingProject();
    }

    public List<SyncStat> getRunningSyncStat () {
        return dao.getRunningSyncStatList();
    }

    public String[] getProjectIdsFromSyncStat() {
        return dao.getProjectIds();
    }

    public String[] getSyncedProjectIds() {
        return dao.getSyncedProjectIds();
    }

    public List<SyncStat> getAllList() {
        return dao.getAllItems();
    }

    public LiveData<List<SyncStat>> getSyncStatusByProjectIds(String... projectIds){
        return dao.getSyncStatus(projectIds);
    }



    public Single<SyncStat> getFailedUrls(String projectId, int type) {
        return dao.getFailedUrls(projectId, type);
    }

    public void updateDownloadProgress(String projectId, int progress, int totalFormsInProject) {
        Timber.i("SyncLocalService, =========>>  updateDownloadProgress :: projectId = %s, progress = %d, totalFormsInProject = %d ", projectId, progress, totalFormsInProject);
        SyncStat syncStat = new SyncStat(projectId, String.valueOf(1) , "", false, Constant.DownloadStatus.RUNNING, System.currentTimeMillis());
        syncStat.setProgress(progress);
        syncStat.setTotal(totalFormsInProject);
        save(syncStat);
    }

    public void setSyncCompleted(String... iDs) {
        dao.setSyncComplete(iDs);
    }
}
