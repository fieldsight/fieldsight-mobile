package org.fieldsight.naxa.common.database;

import androidx.lifecycle.LiveData;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseLocalDataSource;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;

public class ProjectFilterLocalSource implements BaseLocalDataSource<ProjectFilter> {

    private static ProjectFilterLocalSource INSTANCE;
    private ProjectFilterDAO dao;


    private ProjectFilterLocalSource() {
        FieldSightConfigDatabase database = FieldSightConfigDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getProjectFilterDAO();

    }

    public static ProjectFilterLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProjectFilterLocalSource();
        }
        return INSTANCE;
    }

    public Maybe<ProjectFilter> getById(String projectId){

        return dao.getOnceById(projectId);
    }

    @Override
    public LiveData<List<ProjectFilter>> getAll() {
        return null;
    }

    @Override
    public void save(ProjectFilter... items) {

    }

    @Override
    public void save(ArrayList<ProjectFilter> items) {

    }

    @Override
    public void updateAll(ArrayList<ProjectFilter> items) {

    }
}
