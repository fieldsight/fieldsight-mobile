package org.bcss.collect.naxa.project.data;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.login.model.Project;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

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


    public void deleteAll() {
        AsyncTask.execute(() -> dao.deleteAll());
    }




    @Override
    public LiveData<List<Project>> getAll() {
        return dao.getProjectsLive();
    }

    @Override
    public void save(Project... items) {
        AsyncTask.execute(() -> dao.insertWithIgnore(items));
    }

    @Override
    public void save(ArrayList<Project> items) {
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void updateAll(ArrayList<Project> items) {
        AsyncTask.execute(() -> dao.updateAll(items));
    }

    public Single<List<Project>> getProjectsMaybe() {
        return dao.getProjectsMaybe();
    }

    public void updateSiteClusters(String projectId, String siteClusters) {
        AsyncTask.execute(() -> dao.updateCluster(projectId, siteClusters));
    }

    public LiveData<Project> getProjectById(String id) {
        return dao.getById(id);
    }
}
