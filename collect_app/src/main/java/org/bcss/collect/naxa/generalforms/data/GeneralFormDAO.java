package org.bcss.collect.naxa.generalforms.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class GeneralFormDAO implements BaseDaoFieldSight<GeneralForm> {

    @Query("SELECT * FROM general_forms WHERE project =:projectId")
    public abstract LiveData<List<GeneralForm>> getProjectGeneralForms(String projectId);

    @Query("SELECT * FROM general_forms WHERE site =:siteId")
    public abstract LiveData<List<GeneralForm>> getSiteGeneralForms(String siteId);


    @Query("DELETE FROM general_forms")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<GeneralForm> items) {
        deleteAll();
        insert(items);
    }


    @Query("SELECT * FROM general_forms")
    public abstract LiveData<List<GeneralForm>> getAll();
}
