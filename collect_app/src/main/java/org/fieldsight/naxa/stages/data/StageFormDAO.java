package org.fieldsight.naxa.stages.data;

import androidx.lifecycle.LiveData;


import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.fieldsight.naxa.common.database.BaseDaoFieldSight;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;

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

    @Query("SELECT * FROM stages WHERE site =:siteId OR project =:projectId")
    public abstract LiveData<List<Stage>> getBySiteId(String siteId, String projectId);

    @Query("SELECT * FROM stages WHERE project =:projectId ")
    public abstract LiveData<List<Stage>> getByProjectId(String projectId);

    @Query("SELECT * FROM stages WHERE project =:projectId ")
    public abstract Maybe<List<Stage>> getByProjectIdMaybe(String projectId);


    @Query("SELECT * FROM stages WHERE project =:projectId OR site =:siteId")
    public abstract Maybe<List<Stage>> getBySiteIdMaybe(String siteId, String projectId);

    @Query("DELETE FROM stages WHERE project=:projectId")
    public abstract void deleteAllById(String projectId);

    @Query("DELETE FROM stages WHERE project=:siteId")
    public abstract void deleteAllBySiteId(String siteId);
}
