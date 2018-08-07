package org.bcss.collect.naxa.site.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.bcss.collect.naxa.login.model.Site;

import java.util.List;

@Dao
public interface SiteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Site... sites);

    @Query("SELECT * FROM sites")
    LiveData<List<Site>> getSites();

    @Query("SELECT * FROM sites WHERE name LIKE '%' || :searchQuery || '%' OR phone LIKE '%' || :searchQuery || '%' OR identifier LIKE '%' || :searchQuery || '%' OR address LIKE '%' || :searchQuery || '%'")
    List<Site> searchSites(String searchQuery);

    @Query("SELECT * from sites WHERE project =  :projectID")
    LiveData<List<Site>> getSiteByProjectId(String projectID);

    @Query("UPDATE sites SET isSiteVerified =:siteStatus WHERE id=:siteId")
    long updateSiteStatus(String siteId, int siteStatus);

    @Query("UPDATE sites SET id =:newSiteId WHERE id=:oldSiteId")
    void updateSiteId(String oldSiteId, String newSiteId);

    @Query("UPDATE sites SET generalFormDeployedFrom = :deployedFrom WHERE id = :siteId ")
    void updateGeneralFormDeployedFrom(String siteId, String deployedFrom);


    @Query("UPDATE sites SET stagedFormDeployedFrom = :deployedFrom WHERE id = :siteId ")
    void updateStagedFormDeployedFrom(String siteId, String deployedFrom);


    @Query("UPDATE sites SET scheduleFormDeployedForm = :deployedFrom WHERE id = :siteId ")
    void updateScheduleFormDeployedFrom(String siteId, String deployedFrom);

    @Delete
    int delete(Site site);

}
