package org.odk.collect.naxa.project.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.naxa.common.FieldSightDatabase;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.site.db.SiteDao;
import org.odk.collect.naxa.site.db.SiteRepository;

import java.util.List;

public class ProjectRepository {
    private ProjectDao projectDao;

    public ProjectRepository(Application application) {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(application);
        this.projectDao = database.getProjectDAO();
    }

    public LiveData<List<Project>> getAllProjects() {
        return projectDao.getProjects();
    }

    public void insert(Project...projects) {
        new ProjectRepository.insertAsyncTask(projectDao).execute(projects);
    }

    private static class insertAsyncTask extends AsyncTask<Project, Void, Void> {

        private ProjectDao mAsyncTaskDao;

        insertAsyncTask(ProjectDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Project... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


}
