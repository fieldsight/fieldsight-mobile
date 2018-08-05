package org.odk.collect.naxa.scheduled.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.odk.collect.naxa.common.database.BaseDaoFieldSight;
import org.odk.collect.naxa.generalforms.data.GeneralForm;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class ScheduledFormDAO implements BaseDaoFieldSight<ScheduleForm> {

    @Query("SELECT * FROM scheduled_form")
    public abstract LiveData<List<ScheduleForm>> getAll();

    @Query("SELECT * FROM scheduled_form WHERE siteId =:id ")
    public abstract LiveData<List<ScheduleForm>> getBySiteId(String id);


    @Query("DELETE FROM scheduled_form")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<ScheduleForm> items) {
        deleteAll();
        insert(items);
    }

    @Query("SELECT * FROM scheduled_form WHERE project =:projectId")
    public abstract LiveData<List<ScheduleForm>> getByProjectId(String projectId);
}
