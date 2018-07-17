package org.odk.collect.naxa.database.project;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class ProjectViewModel extends AndroidViewModel {
    private ProjectRepository mProjectRepository;
    private List<ProjectModel> mAllProjects;

    public ProjectViewModel(@NonNull Application application) {
        super(application);
        this.mProjectRepository = new ProjectRepository(application);
        this.mAllProjects = mProjectRepository.getAllProjects();
    }

    public List<ProjectModel> getmAllProjects() {
        return mAllProjects;
    }

    public void insert(ProjectModel projectModel) {
        mProjectRepository.insert(projectModel);
    }
}
