package org.bcss.collect.naxa.v3.network;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;

import java.util.List;

@Dao
public interface SyncDaoV3 extends BaseDaoFieldSight<SyncStat> {

    @Query("SELECT * FROM syncstat")
    LiveData<List<SyncStat>> all();

    @Query("SELECT * FROM syncstat where project_id = :projectId")
     LiveData<List<SyncStat>> filterByProjectId(String projectId);

//    @Query("")
//    void updateSiteStatById(String projectid, String type, boolean stat);
//
//    @Query("")
//    void updateFailedurlById(String projectid, String type, String failedUrl);

    @Query("SELECT COUNT(*) FROM (SELECT * FROM syncstat WHERE status=:status)")
    LiveData<Integer> countByStatus(int status);

    @Insert
    void insertAll(SyncStat ...syncStats);

    @Delete
    void delete(SyncStat syncStat);

    @Update
    void updateAll(SyncStat ...syncStats);

    @Query("DELETE FROM syncstat")
    void delete();

    @Query("SELECT project_id, created_date, status FROM syncstat WHERE type=0 AND status > 0")
    LiveData<List<ProjectNameTuple>> getAllSiteSyncingProject();

}
