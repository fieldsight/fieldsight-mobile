package org.odk.collect.naxa.project.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.site.db.SiteRepository;

import java.util.List;

public class ProjectViewModel extends AndroidViewModel {

    private ProjectRepository projectRepository;
    private List<Project> allProjects;

    public ProjectViewModel(@NonNull Application application) {
        super(application);
        this.projectRepository = new ProjectRepository(application);

    }

    public LiveData<List<Project>> getAllProjects() {
        return projectRepository.getAllProjects();
    }

    public void insert(Project project) {
        projectRepository.insert(project);
    }
}
