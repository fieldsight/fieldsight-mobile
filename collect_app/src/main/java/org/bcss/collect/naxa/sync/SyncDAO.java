package org.bcss.collect.naxa.sync;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.common.database.ProjectFilter;

import java.util.List;

import io.reactivex.Single;

@Dao
public abstract class SyncDAO implements BaseDaoFieldSight<Sync> {
    @Query("Select * from sync")
    public abstract LiveData<List<Sync>> getAll();

    @Query("UPDATE sync SET checked='1' ")
    abstract void markAllAsChecked();

    @Query("UPDATE sync SET checked='0' ")
    abstract void markAllAsUnChecked();

    @Query("SELECT COUNT(checked) from sync where checked = '1'")
    abstract Single<Integer> selectedItemsCount();

    @Query("SELECT COUNT(checked) from sync where checked = '1'")
    abstract LiveData<Integer> selectedItemsCountLive();

    @Query("UPDATE sync SET checked = '0' WHERE uid=:uid")
    public abstract void markAsUnchecked(int uid);

    @Query("UPDATE sync SET checked = '1' WHERE uid=:uid")
    public abstract void markAsChecked(int uid);
}
