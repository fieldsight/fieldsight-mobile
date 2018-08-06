package org.odk.collect.naxa.stages.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.odk.collect.naxa.common.database.BaseDaoFieldSight;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class StageFormDAO implements BaseDaoFieldSight<Stage> {
    @Query("SELECT * FROM stages")
    public abstract LiveData<List<Stage>> getAllStages();

    @Query("DELETE FROM stages")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<Stage> items) {
        deleteAll();
        insert(items);
    }

    @Query("SELECT * FROM stages WHERE site =:siteId ")
    public abstract LiveData<List<Stage>> getBySiteId(String siteId);

    @Query("SELECT * FROM stages WHERE project =:projectId ")
    public abstract LiveData<List<Stage>> getByProjectId(String projectId);
}
