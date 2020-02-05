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

    @Query("SELECT * FROM syncstat WHERE cancel_by_user=0")
    LiveData<List<SyncStat>> all();

    @Query("SELECT project_id FROM syncstat WHERE cancel_by_user=0")
    String[] getProjectIds();

    @Query("SELECT * FROM syncstat where project_id = :projectId AND cancel_by_user=0")
    LiveData<List<SyncStat>> filterByProjectId(String projectId);

//    @Query("")
//    void updateSiteStatById(String projectid, String type, boolean stat);
//
//    @Query("")
//    void updateFailedurlById(String projectid, String type, String failedUrl);

    @Query("SELECT COUNT(*) FROM (SELECT * FROM syncstat WHERE status in (:status) AND cancel_by_user=0)")
    LiveData<Integer> countByStatus(int... status);

    @Insert
    void insertAll(SyncStat... syncStats);

    @Delete
    void delete(SyncStat syncStat);

    @Update
    void updateAll(SyncStat... syncStats);

    @Query("DELETE FROM syncstat")
    void delete();

    @Query("SELECT project_id, created_date, status FROM syncstat WHERE status > 0 AND status < 4 AND cancel_by_user=0")
    LiveData<List<ProjectNameTuple>> getAllSiteSyncingProject();

    @Query("SELECT * from syncstat WHERE project_id=:projectId AND type=:type AND cancel_by_user=0")
    Single<SyncStat> getFailedUrls(String projectId, int type);

    @Query("SELECT project_id FROM syncstat WHERE (type = 0 AND status=4) AND (type = 1 AND status =4) AND (type = 2 AND status = 4) AND cancel_by_user=0")
    String[] getSyncedProjectIds();

    @Query("SELECT * FROM syncstat WHERE project_id in (:projectIds) AND cancel_by_user=0")
    LiveData<List<SyncStat>> getSyncStatus(String... projectIds);

    @Query("SELECT * FROM syncstat WHERE status > 0 AND status < 4 AND cancel_by_user=0")
    List<SyncStat> getRunningSyncStatList();

    @Query("SELECT * FROM syncstat WHERE cancel_by_user=0")
    List<SyncStat> getAllItems();

    @Query("DELETE FROM syncstat WHERE project_id=:projectId")
    void deleteById(String projectId);

    @Query("UPDATE syncstat set cancel_by_user=1 WHERE project_id=:projectId")
    void setSyncCancelled(String projectId);

    @Query("DELETE FROM syncstat WHERE cancel_by_user=1")
    void removeCancelledSync();
}
