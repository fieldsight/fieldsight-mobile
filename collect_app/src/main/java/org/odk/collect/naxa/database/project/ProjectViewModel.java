package org.odk.collect.naxa.database.project;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class ProjectViewModel extends AndroidViewModel {
    private ProjectRepository mProjectRepository;
    private LiveData<List<ProjectModel>> mAllProjectModel;

    public ProjectViewModel(@NonNull Application application) {
        super(application);
        this.mProjectRepository = new ProjectRepository(application);
        this.mAllProjectModel = mProjectRepository.getAllProjects();
    }

    public LiveData<List<ProjectModel>> getmAllProjectModel() {
        return mAllProjectModel;
    }

    public void insert(ProjectModel projectModel) {
        mProjectRepository.insert(projectModel);
    }
}
