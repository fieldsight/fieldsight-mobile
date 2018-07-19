package org.odk.collect.naxa.project.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import org.odk.collect.naxa.login.model.Project;

import java.util.List;

import io.reactivex.Maybe;

public class ProjectViewModel extends AndroidViewModel {

    private ProjectRepository projectRepository;
    private List<Project> allProjects;

    public ProjectViewModel(@NonNull Application application) {
        super(application);
        this.projectRepository = new ProjectRepository(application);

    }

    public Maybe<List<Project>> getAllProjects() {
        return projectRepository.getAllProjects();
    }

    public void insert(Project project) {
        projectRepository.insert(project);
    }
}
