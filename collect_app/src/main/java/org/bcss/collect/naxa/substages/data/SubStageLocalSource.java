package org.bcss.collect.naxa.substages.data;

import android.arch.lifecycle.LiveData;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.stages.data.SubStage;

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
    public LiveData<List<SubStage>> getAll( ) {
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
