package org.odk.collect.naxa.sync;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.onboarding.SyncableItems;

import java.util.List;

@Dao
public interface SyncDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SyncableItems... items);

    @Query("SELECT * FROM sync")
    LiveData<List<SyncableItems>> getAllSyncableItems();
}
