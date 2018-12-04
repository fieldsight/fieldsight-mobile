package org.bcss.collect.naxa.common.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class SiteUploadHistoryDAO implements BaseDaoFieldSight<SiteUploadHistory> {

    @Query("SELECT * from site_upload_history")
    public abstract LiveData<List<SiteUploadHistory>> getAll();

    @Query("SELECT * from site_upload_history WHERE oldSiteId=:siteId")
    public abstract String getBySiteId(String siteId);

}
