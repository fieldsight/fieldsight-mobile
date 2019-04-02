package org.bcss.collect.naxa.scheduled.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.bcss.collect.naxa.common.database.BaseDaoFieldSight;
import org.bcss.collect.naxa.previoussubmission.model.ScheduledFormAndSubmission;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import io.reactivex.Single;

@Dao
public abstract class ScheduledFormDAO implements BaseDaoFieldSight<ScheduleForm> {

    @Query("SELECT * FROM scheduled_form")
    public abstract LiveData<List<ScheduleForm>> getAll();

    @Query("SELECT * FROM scheduled_form")
    public abstract List<ScheduleForm> getDailyForms();

    @Query("SELECT * FROM scheduled_form")
    public abstract List<ScheduleForm> getWeeklyForms();

    @Query("SELECT * FROM scheduled_form")
    public abstract List<ScheduleForm> getMonthlyForms();

    @Deprecated
    @Query("SELECT * FROM scheduled_form WHERE (siteId =:id OR projectId =:projectId) AND isFormDeployed = 1")
    public abstract LiveData<List<ScheduleForm>> getBySiteId(String id, String projectId);


    @Query("DELETE FROM scheduled_form")
    public abstract void deleteAll();

    @Transaction
    public void updateAll(ArrayList<ScheduleForm> items) {
        deleteAll();
        insert(items);
    }

    @Deprecated
    @Query("SELECT * FROM scheduled_form WHERE projectId =:projectId  AND isFormDeployed = 1")
    public abstract LiveData<List<ScheduleForm>> getByProjectId(String projectId);


    @Query("SELECT * FROM scheduled_form WHERE fsFormId =:fsFormId")
    public abstract LiveData<List<ScheduleForm>> getById(String fsFormId);


    @Query("select * from scheduled_form " +
            "left join submission_detail " +
            "on scheduled_form.fsFormId = submission_detail.projectFsFormId" +
            " WHERE scheduled_form.projectId =:projectId")
    public abstract LiveData<List<ScheduledFormAndSubmission>> getProjectScheduleFormAndSubmission(String projectId);

    @Query("select * from scheduled_form " +
            "left join submission_detail " +
            "on scheduled_form.fsFormId = submission_detail.siteFsFormId" +
            " WHERE scheduled_form.siteId =:siteId OR scheduled_form.projectId =:projectId")
    public abstract LiveData<List<ScheduledFormAndSubmission>> getSiteScheduleFormAndSubmission(String siteId, String projectId);
}
