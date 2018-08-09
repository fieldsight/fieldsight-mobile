package org.bcss.collect.naxa.project.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.naxa.login.model.Project;

import java.util.List;

import io.reactivex.Maybe;

public class ProjectViewModel extends ViewModel {

    private ProjectRepository projectRepository;

    public ProjectViewModel(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    public LiveData<List<Project>> getAll(boolean forcedUpdate) {
        return projectRepository.getAll(forcedUpdate);
    }


    public void insert(Project project) {
        projectRepository.save(project);
    }
}
