package org.bcss.collect.naxa.common.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import org.bcss.collect.naxa.login.model.Site;

import java.util.ArrayList;
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
