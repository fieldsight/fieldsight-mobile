package org.bcss.collect.naxa.scheduled.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class ScheduledFormDAO implements BaseDaoFieldSight<ScheduleForm> {

    @Query("SELECT * FROM scheduled_form")
    public abstract LiveData<List<ScheduleForm>> getAll();

    @Query("SELECT * FROM scheduled_form WHERE siteId =:id OR project =:projectId")
    public abstract LiveData<List<ScheduleForm>> getBySiteId(String id, String projectId);


    @Query("DELETE FROM scheduled_form")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<ScheduleForm> items) {
        deleteAll();
        insert(items);
    }

    @Query("SELECT * FROM scheduled_form WHERE project =:projectId")
    public abstract LiveData<List<ScheduleForm>> getByProjectId(String projectId);

    @Query("SELECT * FROM scheduled_form WHERE fsFormId =:fsFormId")
    public abstract LiveData<List<ScheduleForm>> getById(String fsFormId);
}
