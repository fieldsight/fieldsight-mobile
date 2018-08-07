package org.odk.collect.naxa.site;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import org.odk.collect.naxa.common.database.BaseDaoFieldSight;
import org.odk.collect.naxa.site.data.SiteCluster;

import java.util.List;

@Dao
public abstract class SiteClusterDAO implements BaseDaoFieldSight<SiteCluster> {
    @Query("SELECT * FROM site_clusters WHERE id =:projectId")
    public abstract LiveData<List<SiteCluster>> getByProjectId(String projectId);
}
