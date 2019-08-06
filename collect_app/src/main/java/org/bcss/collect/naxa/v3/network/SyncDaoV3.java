package org.bcss.collect.naxa.v3.network;





import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;

import java.util.List;

import io.reactivex.Single;

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
    void insertAll(SyncStat... syncStats);

    @Delete
    void delete(SyncStat syncStat);

    @Update
    void updateAll(SyncStat... syncStats);

    @Query("DELETE FROM syncstat")
    void delete();

    @Query("SELECT project_id, created_date, status FROM syncstat WHERE type=0 AND status > 0")
    LiveData<List<ProjectNameTuple>> getAllSiteSyncingProject();

    @Query("SELECT * from syncstat WHERE project_id=:projectId AND type=:type")
    Single<SyncStat> getFailedUrls(String projectId, int type);
}
