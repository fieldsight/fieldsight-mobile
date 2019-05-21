package org.bcss.collect.naxa.v3.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.db.SiteDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import static org.bcss.collect.naxa.common.Constant.SiteStatus.IS_ONLINE;

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


    @Override
    public void save(SyncStat... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }





    @Override
    public void save(ArrayList<SyncStat> items) {

    }

    @Override
    public void updateAll(ArrayList<SyncStat> items) {

    }

    public void update(SyncStat stat) {
        AsyncTask.execute(()-> dao.updateAll(stat));
    }



}
