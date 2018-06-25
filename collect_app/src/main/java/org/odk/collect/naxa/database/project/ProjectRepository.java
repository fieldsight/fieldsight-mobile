package org.odk.collect.naxa.database.project;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.naxa.database.FieldSightRoomDatabase;

import java.util.List;

public class ProjectRepository {
    private ProjectDao mProjectDao;
    private LiveData<List<ProjectModel>> mAllProjectModel;

    public ProjectRepository(Application application) {
        FieldSightRoomDatabase database = FieldSightRoomDatabase.getDatabase(application);
        this.mProjectDao = database.projectDao();
        this.mAllProjectModel = mProjectDao.getAllProjects();
    }

    LiveData<List<ProjectModel>> getAllProjects() {
        return mAllProjectModel;
    }

    public void insert(ProjectModel projectModel) {
        new insertAsyncTask(mProjectDao).execute(projectModel);
    }

    private static class insertAsyncTask extends AsyncTask<ProjectModel, Void, Void> {

        private ProjectDao mAsyncTaskDao;

        insertAsyncTask(ProjectDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final ProjectModel... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
