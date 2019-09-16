package org.fieldsight.naxa.project.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.fieldsight.naxa.login.model.Project;

import java.util.List;

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
