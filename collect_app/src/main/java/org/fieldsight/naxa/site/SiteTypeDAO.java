package org.fieldsight.naxa.site;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import org.fieldsight.naxa.common.database.BaseDaoFieldSight;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class SiteTypeDAO implements BaseDaoFieldSight<SiteType> {

    @Query("DELETE FROM site_types")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<SiteType> items) {
        deleteAll();
        insert(items);
    }

    @Query("SELECT * FROM site_types WHERE projectId= :projectId")
    public abstract LiveData<List<SiteType>> getByProjectId(String projectId);

    @Query("SELECT * FROM site_types WHERE projectId= :projectId")
    public abstract List<SiteType> getAllByProjectId(String projectId);
}
