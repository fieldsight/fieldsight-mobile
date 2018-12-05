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
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Action;

public class SiteUploadHistoryLocalSource implements BaseLocalDataSource<SiteUploadHistory> {
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


    public Completable saveUsingCompletable(SiteUploadHistory... items) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                dao.insert(items);
            }
        });
    }

    @Override
    public void save(SiteUploadHistory... items) {
        AsyncTask.execute(() -> {
            dao.insert(items);
        });
    }

    public Observable<Long[]> saveAsObservable(SiteUploadHistory...siteUploadHistories){
        return Observable.fromCallable(new Callable<Long[]>() {
            @Override
            public Long[] call() throws Exception {
                return dao.insertAndReturnIds(siteUploadHistories);
            }
        });
    }


    public Completable saveAsCompletable(SiteUploadHistory... items) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                dao.insert(items);
            }
        });
    }

    @Override
    public void save(ArrayList<SiteUploadHistory> items) {
        AsyncTask.execute(() -> {
            dao.insert(items);
        });
    }

    @Override
    public void updateAll(ArrayList<SiteUploadHistory> items) {

    }

    public SiteUploadHistory getById(String siteId) {
        return dao.getBySiteId(siteId);
    }
}
