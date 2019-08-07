package org.fieldsight.naxa.sync;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.fieldsight.naxa.onboarding.SyncableItem;

import java.util.List;

import io.reactivex.Single;

@Deprecated
@Dao
public interface SyncOLD {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SyncableItem... items);

    @Query("SELECT * FROM sync")
    LiveData<List<SyncableItem>> getAllSyncableItems();


    @Query("UPDATE sync SET progressStatus=:value WHERE uid=:key")
    void updateProgress(int key, boolean value);

    @Query("UPDATE sync SET checked=:value WHERE uid=:key")
    void updateChecked(int key, boolean value);

    @Query("UPDATE sync SET checked=:value")
    void setAllCheckedTrue(boolean value);

    @Query("UPDATE sync SET lastSyncDateTime=:value WHERE uid=:key")
    void updateDate(int key, String value);

    @Query("UPDATE sync SET downloadingStatus=:status WHERE uid=:key")
    void updateStatus(int key, int status);


    @Query("DELETE FROM sync")
    void deleteAll();

    @Query("UPDATE sync SET checked=:value WHERE uid =:syncItemType")
    void setIsDataOutOfSync(String syncItemType, boolean value);

    @Query("SELECT * from sync WHERE uid=:uid")
    Single<SyncableItem> getById(int uid);

//    @Query("UPDATE sync SET downloadingStatus='asda' WHERE downloadingStatus =:statusToChange");
//    void updateAllUnknown(String statusToChange);

    @Query("UPDATE sync SET downloadingStatus=:newValue WHERE progressStatus=:isProgress")
    void updateAllUnknown(int newValue, boolean isProgress);

    @Query("UPDATE sync SET downloadingStatus = 2, progressStatus = 0, lastSyncDateTime =:date WHERE progressStatus= 1")
    void markAllRunningTaskAsFailed(String date);
}
