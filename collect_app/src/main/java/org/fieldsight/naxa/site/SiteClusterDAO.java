package org.fieldsight.naxa.site;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import org.fieldsight.naxa.common.database.BaseDaoFieldSight;
import org.fieldsight.naxa.site.data.SiteRegion;

import java.util.List;

@Dao
public abstract class SiteClusterDAO implements BaseDaoFieldSight<SiteRegion> {
    @Query("SELECT * FROM site_region WHERE id =:projectId")
    public abstract LiveData<List<SiteRegion>> getByProjectId(String projectId);
}
