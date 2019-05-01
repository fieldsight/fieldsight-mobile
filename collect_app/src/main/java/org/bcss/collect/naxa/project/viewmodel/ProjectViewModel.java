package org.bcss.collect.naxa.project.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.project.ProjectListActivity;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.project.data.ProjectRepository;

import java.util.List;

/**
 * View Model {@link ProjectListActivity}
 */
public class ProjectViewModel extends ViewModel {
    private final ProjectLocalSource projectLocalSource;
    private MutableLiveData<List<Project>> allProjects;
    private final MutableLiveData<Boolean> mIsDataAvailable = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mDataLoading = new MutableLiveData<>();

    private ProjectRepository repository;

    public ProjectViewModel() {
        this.projectLocalSource = ProjectLocalSource.getInstance();
        repository = ProjectRepository.getInstance();
    }

    public void getAllProjects(ProjectRepository.LoadProjectCallback loadProjectCallback) {
        repository.getAll(loadProjectCallback);
    }


    public final LiveData<Boolean> completed = Transformations.map(allProjects, new Function<List<Project>, Boolean>() {
        @Override
        public Boolean apply(List<Project> input) {
            return null;
        }
    });


}
