package org.odk.collect.naxa.project.data;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.common.FieldSightDatabase;
import org.odk.collect.naxa.login.model.Project;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;

public class ProjectLocalSource implements BaseLocalDataSource<Project> {

    private static ProjectLocalSource INSTANCE;
    private ProjectDao dao;


    private ProjectLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getProjectDAO();
    }


    public static ProjectLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProjectLocalSource();
        }
        return INSTANCE;
    }

    @Override
    public LiveData<List<Project>> getById(boolean forceUpdate, String id) {
        return dao.getProjectsLive();
    }

    @Override
    public LiveData<List<Project>> getAll( ) {
        return dao.getProjectsLive();
    }

    @Override
    public void save(Project... items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<Project> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<Project> items) {
        AsyncTask.execute(() -> dao.updateAll(items));
    }

    public Maybe<List<Project>> getProjectsMaybe() {
        return dao.getProjectsMaybe();
    }
}
