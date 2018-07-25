package org.odk.collect.naxa.substages.data;

import android.arch.lifecycle.LiveData;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.common.FieldSightDatabase;
import org.odk.collect.naxa.stages.data.SubStage;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class SubStageLocalSource implements BaseLocalDataSource<SubStage> {

    private static SubStageLocalSource INSTANCE;
    private SubStageDAO dao;


    private SubStageLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getSubStageDAO();
    }

    public static SubStageLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SubStageLocalSource();
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<SubStage>> getById(boolean forceUpdate, String id) {
        return dao.getByStageId(id);
    }

    @Override
    public LiveData<List<SubStage>> getAll() {
        return dao.getAllSubStages();
    }

    @Override
    public void save(SubStage... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<SubStage> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<SubStage> items) {
        AsyncTask.execute(() -> dao.updateAll(items));
    }
}
