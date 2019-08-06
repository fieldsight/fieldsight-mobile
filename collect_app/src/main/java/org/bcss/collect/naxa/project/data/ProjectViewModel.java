package org.bcss.collect.naxa.project.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.bcss.collect.naxa.login.model.Project;

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
