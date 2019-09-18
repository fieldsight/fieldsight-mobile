package org.bcss.collect.naxa.v3.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.FieldSightDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Action;

public class SyncLocalSourcev3 implements BaseLocalDataSource<SyncStat> {


    private static SyncLocalSourcev3 INSTANCE;
    private SyncDaoV3 dao;


    private SyncLocalSourcev3() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSyncDaoV3();
    }


    public static SyncLocalSourcev3 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SyncLocalSourcev3();
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<SyncStat>> getAll() {
        return dao.all();
    }


    public void delete(SyncStat stat) {
        MutableLiveData<Integer> affectedRowsMutData = new MutableLiveData<>();
        AsyncTask.execute(() -> dao.delete(stat));
    }

    public LiveData<Integer> getCountByStatus(int status) {
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

}
