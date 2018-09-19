package org.bcss.collect.naxa.scheduled.data;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.previoussubmission.model.GeneralFormAndSubmission;
import org.bcss.collect.naxa.previoussubmission.model.ScheduledFormAndSubmission;

import java.util.ArrayList;
import java.util.List;

public class ScheduledFormsLocalSource implements BaseLocalDataSource<ScheduleForm> {

    private static ScheduledFormsLocalSource INSTANCE;
    private ScheduledFormDAO dao;

    private ScheduledFormsLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getProjectScheduledFormsDAO();
    }

    public static ScheduledFormsLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScheduledFormsLocalSource();
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<ScheduleForm>> getAll() {
        return dao.getAll();
    }

    @Override
    public void save(ScheduleForm... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<ScheduleForm> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<ScheduleForm> items) {
        AsyncTask.execute(() -> dao.updateAll(items));
    }

    @Deprecated
    public LiveData<List<ScheduleForm>> getBySiteId(String siteId, String projectId) {
        return dao.getBySiteId(siteId, projectId);
    }

    @Deprecated
    public LiveData<List<ScheduleForm>> getByProjectId(String projectId) {
        return dao.getByProjectId(projectId);
    }

    public LiveData<List<ScheduleForm>> getById(String fsFormId) {
        return dao.getById(fsFormId);
    }


    public LiveData<List<ScheduledFormAndSubmission>> getFormsBySiteId(@NonNull String siteId, @NonNull String projectId) {
        return dao.getSiteScheduleFormAndSubmission(siteId, projectId);
    }

    public LiveData<List<ScheduledFormAndSubmission>> getFormsByProjectId(@NonNull String projectId) {
        return dao.getProjectScheduleFormAndSubmission(projectId);
    }

}
