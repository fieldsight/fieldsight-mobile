package org.odk.collect.naxa.stages.data;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.common.FieldSightDatabase;

import java.util.ArrayList;
import java.util.List;

public class StageLocalSource implements BaseLocalDataSource<Stage> {

    private static StageLocalSource INSTANCE;
    private StageFormDAO dao;

    private StageLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getStageDAO();
    }


    public static StageLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StageLocalSource();
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<Stage>> getAll( ) {
        return dao.getAllStages();
    }

    @Override
    public void save(Stage... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<Stage> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<Stage> items) {
        AsyncTask.execute(() -> dao.updateAll(items));
    }

    public LiveData<List<Stage>> getBySiteId(String siteId, String formDeployedForm) {
        return dao.getBySiteId(siteId,formDeployedForm);
    }
}
