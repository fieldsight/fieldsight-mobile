package org.odk.collect.naxa.project.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
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

    public LiveData<List<Project>> getAllProjectsLive() {
        return projectRepository.getAllProjectsLive();
    }

    public Maybe<List<Project>> getAllProjectsMaybe() {
        return projectRepository.getAllProjectsMaybe();
    }

    public void insert(Project project) {
        projectRepository.insert(project);
    }
}
