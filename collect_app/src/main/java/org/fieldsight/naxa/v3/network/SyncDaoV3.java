package org.fieldsight.naxa.v3.network;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import org.fieldsight.naxa.common.database.BaseDaoFieldSight;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface SyncDaoV3 extends BaseDaoFieldSight<SyncStat> {

    @Query("SELECT * FROM syncstat")
    LiveData<List<SyncStat>> all();

    @Query("SELECT project_id FROM syncstat")
    String[] getProjectIds();

    @Query("SELECT * FROM syncstat where project_id = :projectId")
    LiveData<List<SyncStat>> filterByProjectId(String projectId);

//    @Query("")
//    void updateSiteStatById(String projectid, String type, boolean stat);
//
//    @Query("")
//    void updateFailedurlById(String projectid, String type, String failedUrl);

    @Query("SELECT COUNT(*) FROM (SELECT * FROM syncstat WHERE status in (:status))")
    LiveData<Integer> countByStatus(int... status);

    @Insert
    void insertAll(SyncStat... syncStats);

    @Delete
    void delete(SyncStat syncStat);

    @Update
    void updateAll(SyncStat... syncStats);

    @Query("DELETE FROM syncstat")
    void delete();

    @Query("SELECT project_id, created_date, status FROM syncstat WHERE status > 0 AND status < 4")
    LiveData<List<ProjectNameTuple>> getAllSiteSyncingProject();

    @Query("SELECT * from syncstat WHERE project_id=:projectId AND type=:type")
    Single<SyncStat> getFailedUrls(String projectId, int type);

    @Query("SELECT project_id FROM syncstat WHERE (type = 0 AND status=4) AND (type = 1 AND status =4) AND (type = 2 AND status = 4)")
    String[] getSyncedProjectIds();

    @Query("SELECT * FROM syncstat WHERE project_id in (:projectIds)")
    LiveData<List<SyncStat>> getSyncStatus(String... projectIds);
}
