package org.odk.collect.naxa.site;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.odk.collect.naxa.common.database.BaseDaoFieldSight;
import org.odk.collect.naxa.survey.SurveyForm;

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
}
