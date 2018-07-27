package org.odk.collect.naxa.scheduled.data;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.common.FieldSightDatabase;

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
    public LiveData<List<ScheduleForm>> getById(boolean forceUpdate, String id) {
        return dao.getBySiteId(id);
    }

    @Override
    public LiveData<List<ScheduleForm>> getAll( ) {
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
}
