package org.bcss.collect.naxa.project.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.project.data.ProjectRepository;
import org.bcss.collect.naxa.project.viewmodel.ProjectViewModel;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.List;

import timber.log.Timber;

public class ProjectsActivity extends CollectAbstractActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_projects);
        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        ProjectViewModel projectViewModel = ViewModelProviders.of(this, factory).get(ProjectViewModel.class);

        projectViewModel.getAllProjects(new ProjectRepository.LoadProjectCallback() {
            @Override
            public void onProjectLoaded(List<Project> projects) {
                Timber.d("Loaded %d projects", projects.size());
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }
}
