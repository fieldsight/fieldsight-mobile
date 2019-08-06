package org.bcss.collect.naxa.site.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.bcss.collect.naxa.login.model.Site;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface SiteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Site... sites);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Site> sites);

    @Query("SELECT * FROM sites")
    LiveData<List<Site>> getSites();

    @Query("SELECT * FROM sites WHERE (name LIKE '%' || :searchQuery || " +
            "'%' OR phone LIKE '%' || :searchQuery || '%' OR identifier LIKE '%' || " +
            ":searchQuery || '%' OR address LIKE '%' || :searchQuery || '%') and project=:projectId")
    List<Site> searchSites(String searchQuery, String projectId);

    @Query("SELECT * from sites WHERE project =  :projectID ORDER BY isSiteVerified ASC,name ")
    LiveData<List<Site>> getSiteByProjectId(String projectID);

    @Query("SELECT * from sites WHERE project = :projectID")
    Single<List<Site>> getSiteByProjectIdAsSingle(String projectID);

    @Query("SELECT * from sites WHERE id= :siteId")
    LiveData<Site> getSiteById(String siteId);

    @Query("SELECT * from sites WHERE id= :siteId")
    Single<Site> getSiteByIdAsSingle(String siteId);

    @Query("UPDATE sites SET isSiteVerified =:siteStatus WHERE id=:siteId")
    int updateSiteStatus(String siteId, int siteStatus);

    @Query("UPDATE sites SET id =:newSiteId WHERE id=:oldSiteId")
    int updateSiteId(String oldSiteId, String newSiteId);

    @Query("UPDATE sites SET generalFormDeployedFrom = :deployedFrom WHERE id = :siteId ")
    void updateGeneralFormDeployedFrom(String siteId, String deployedFrom);


    @Query("UPDATE sites SET stagedFormDeployedFrom = :deployedFrom WHERE id = :siteId ")
    void updateStagedFormDeployedFrom(String siteId, String deployedFrom);

    @Query("SELECT DISTINCT project from sites")
    LiveData<List<String>> getAllDistictProject();

    @Query("UPDATE sites SET scheduleFormDeployedForm = :deployedFrom WHERE id = :siteId ")
    void updateScheduleFormDeployedFrom(String siteId, String deployedFrom);

    @Delete
    int delete(Site site);

    @Query("SELECT * from sites where isSiteVerified =:status and project =:projectId AND site IS NULL")
    LiveData<List<Site>> getByIdOfflineSites(String projectId, int status);

    @Query("SELECT * from sites where project =:projectId and regionId=:cluster ")
    LiveData<List<Site>> getSiteFromFilter(String projectId, String cluster);

    @Query("SELECT * from sites where site =:parentId")
    List<Site> getSiteByParentId(String parentId);

    @Query("DELETE from sites WHERE isSiteVerified =:id ")
    void deleteSyncedSites(int id);

    @Query("SELECT * from sites WHERE isSiteVerified =:siteStatus")
    Single<List<Site>> getAllByStatus(int siteStatus);

    @Query("SELECT * from sites WHERE site IS NULL AND project=:projectId")
    LiveData<List<Site>> getParentSite(String projectId);
}
