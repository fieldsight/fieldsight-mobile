package org.bcss.collect.naxa.site.db;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.database.FieldSightConfigDatabase;
import org.bcss.collect.naxa.common.database.SiteUploadHistory;
import org.bcss.collect.naxa.common.database.SiteUploadHistoryDAO;

import java.util.ArrayList;
import java.util.List;

class SiteUploadHistoryLocalSource implements BaseLocalDataSource<SiteUploadHistory> {
    public static SiteUploadHistoryLocalSource INSTANCE;
    private SiteUploadHistoryDAO dao;

    public static SiteUploadHistoryLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SiteUploadHistoryLocalSource();
        }
        return INSTANCE;
    }

    public SiteUploadHistoryLocalSource() {
        this.dao = FieldSightConfigDatabase.getDatabase(Collect.getInstance()).getSiteUploadHistoryDao();
    }


    @Override
    public LiveData<List<SiteUploadHistory>> getAll() {
        return dao.getAll();
    }

    @Override
    public void save(SiteUploadHistory... items) {
        AsyncTask.execute(()->{
            dao.insert(items);
        });
    }

    @Override
    public void save(ArrayList<SiteUploadHistory> items) {
        AsyncTask.execute(()->{
            dao.insert(items);
        });
    }

    @Override
    public void updateAll(ArrayList<SiteUploadHistory> items) {

    }
}
