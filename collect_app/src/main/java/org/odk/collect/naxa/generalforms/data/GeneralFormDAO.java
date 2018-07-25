package org.odk.collect.naxa.generalforms.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.odk.collect.naxa.common.database.BaseDaoFieldSight;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class GeneralFormDAO implements BaseDaoFieldSight<GeneralForm> {

    @Query("SELECT * FROM general_forms")
    public abstract LiveData<List<GeneralForm>> getProjectGeneralForms();


    @Query("DELETE FROM general_forms")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<GeneralForm> items) {
        deleteAll();
        insert(items);
    }
}
