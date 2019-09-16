package org.fieldsight.naxa.common.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class SiteUploadHistoryDAO implements BaseDaoFieldSight<SiteUploadHistory> {

    @Query("SELECT * from site_upload_history")
    public abstract LiveData<List<SiteUploadHistory>> getAll();

    @Query("SELECT * from site_upload_history WHERE oldSiteId=:siteId")
    public abstract SiteUploadHistory getBySiteId(String siteId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long[] insertAndReturnIds(SiteUploadHistory... items);

}
