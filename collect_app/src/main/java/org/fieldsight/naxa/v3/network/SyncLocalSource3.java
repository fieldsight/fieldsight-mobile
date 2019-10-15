package org.fieldsight.naxa.v3.network;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.FieldSightDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

public class SyncLocalSource3 implements BaseLocalDataSource<SyncStat> {


    private static SyncLocalSource3 INSTANCE;
    private final SyncDaoV3 dao;


    private SyncLocalSource3() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSyncDaoV3();
    }


    public static SyncLocalSource3 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SyncLocalSource3();
        }
        return INSTANCE;
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
        SyncStat syncStat = new SyncStat(projectId, type + "", "", false, Constant.DownloadStatus.QUEUED, System.currentTimeMillis());
        save(syncStat);
    }

    public void markAsFailed(String projectId, int type, String failedUrl) {
        SyncStat syncStat = new SyncStat(projectId, type + "", failedUrl, false, Constant.DownloadStatus.FAILED, System.currentTimeMillis());
        save(syncStat);
    }

    public void markAsCompleted(String projectId, int type) {
        SyncStat syncStat = new SyncStat(projectId, type + "", "", false, Constant.DownloadStatus.COMPLETED, System.currentTimeMillis());
        save(syncStat);
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

    public LiveData<List<ProjectNameTuple>> getAllSiteSyncingProject() {
        return dao.getAllSiteSyncingProject();
    }

    public Single<SyncStat> getFailedUrls(String projectId, int type) {
        return dao.getFailedUrls(projectId, type);
    }

    public void updateDownloadProgress(String projectId, int progress, int totalFormsInProject) {
        SyncStat syncStat = new SyncStat(projectId, 1 + "", "", false, Constant.DownloadStatus.RUNNING, System.currentTimeMillis());
        syncStat.setProgress(progress);
        syncStat.setTotal(totalFormsInProject);
        save(syncStat);
    }
}
