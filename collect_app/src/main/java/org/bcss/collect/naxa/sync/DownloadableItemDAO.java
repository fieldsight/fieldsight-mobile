package org.bcss.collect.naxa.sync;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;

import java.util.List;

import io.reactivex.Single;

@Dao
public abstract class DownloadableItemDAO implements BaseDaoFieldSight<DownloadableItem> {
    @Query("SELECT * from sync ORDER BY title ASC")
    public abstract LiveData<List<DownloadableItem>> getAll();

    @Query("UPDATE sync SET checked='1' WHERE downloadingStatus != 5 ")
    abstract void markAllAsChecked();

    @Query("UPDATE sync SET checked='0' WHERE downloadingStatus != 5")
    abstract void markAllAsUnChecked();

    @Query("SELECT COUNT(checked) from sync where checked = '1'")
    abstract Single<Integer> selectedItemsCount();

    @Query("SELECT COUNT(checked) from sync where checked = '1'")
    abstract LiveData<Integer> selectedItemsCountLive();

    @Query("UPDATE sync SET checked = '0' WHERE uid=:uid")
    public abstract void markAsUnchecked(int uid);

    @Query("UPDATE sync SET checked = '1' WHERE uid=:uid")
    public abstract void markAsChecked(int uid);

    @Query("SELECT * from sync where checked = '1'")
    public abstract Single<List<DownloadableItem>> getAllChecked();

    @Query("UPDATE sync set downloadingStatus=:failed,lastSyncDateTime =:now WHERE uid=:uid")
    public abstract void markSelectedAsFailed(int uid, int failed, String now);

    @Query("UPDATE sync set downloadingStatus=:failed,lastSyncDateTime =:now,errorMessage =:message WHERE uid=:uid")
    public abstract void markFailedWithMsg(int uid, int failed, String now, String message);

    @Query("UPDATE sync set downloadingStatus=:completed,lastSyncDateTime =:now  WHERE uid=:uid")
    public abstract void markSelectedAsCompleted(int uid, int completed, String now);

    @Query("UPDATE sync set downloadingStatus=:running WHERE uid=:uid")
    public abstract void markSelectedAsRunning(int uid, int running);


    @Query("UPDATE sync set downloadingStatus=3,detail=:message  WHERE uid=:uid")
    public abstract void markSelectedAsRunning(int uid, String message);

    @Query("UPDATE sync set downloadingStatus=:pending")
    public abstract void markAllAsPending(int pending);

    @Query("UPDATE sync set syncTotal=:total,syncProgress=:progress WHERE uid=:uid  ")
    public abstract void updateProgress(int uid, int total, int progress);

    @Query("SELECT COUNT(checked) from sync where downloadingStatus =:running")
    public abstract LiveData<Integer> runningItemCountLive(int running);

    @Query("UPDATE sync set errorMessage=:errorMessage WHERE uid=:uid  ")
    public abstract void updateErrorMessage(int uid, String errorMessage);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertOrIgnore(DownloadableItem... items);

    @Query("DELETE from sync where uid=:uid")
    public abstract void deleteById(int uid);

    //    public static final int PENDING = 1;
    //    public static final int FAILED = 2;
    @Query("UPDATE sync SET downloadingStatus = 2, lastSyncDateTime =:date WHERE downloadingStatus= 3")
    public abstract void setAllRunningTaskAsFailed(String date);

    @Query("UPDATE sync set downloadingStatus=:disabled,lastSyncDateTime =:formattedDate,detail=:message WHERE uid=:uid")
    public abstract void markSelectedAsDisabled(int uid, int disabled, String formattedDate, String message);



}
