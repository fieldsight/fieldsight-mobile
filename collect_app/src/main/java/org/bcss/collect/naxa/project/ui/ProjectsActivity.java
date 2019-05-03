package org.bcss.collect.naxa.project.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.BaseRecyclerViewAdapter;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.project.adapter.ProjectViewHolder;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.project.data.ProjectRepository;
import org.bcss.collect.naxa.project.data.model.SiteResponse;
import org.bcss.collect.naxa.project.viewmodel.ProjectViewModel;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

public class ProjectsActivity extends CollectAbstractActivity {

    private RecyclerView recyclerView;
    private boolean isDown = false;
    private BaseRecyclerViewAdapter<Project, ProjectViewHolder> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_projects);
        bindUI();
        populateData();

    }


    private void bindUI() {
        recyclerView = findViewById(R.id.recycler_view_projects_activity);
    }

    private void populateData() {
        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        ProjectViewModel projectViewModel = ViewModelProviders.of(this, factory).get(ProjectViewModel.class);

        ProjectLocalSource.getInstance().deleteAll();
        projectViewModel.getAllProjects(new ProjectRepository.LoadProjectCallback() {
            @Override
            public void onProjectLoaded(List<Project> projects) {
                Timber.d("Loaded %d projects", projects.size());
                setupRecyclerView(projects);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    private void setupRecyclerView(List<Project> projects) {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BaseRecyclerViewAdapter<Project, ProjectViewHolder>(projects, R.layout.project_list_item) {
            @Override
            public void viewBinded(ProjectViewHolder projectViewHolder, Project project) {
                projectViewHolder.bindView(project);
            }

            @Override
            public ProjectViewHolder attachViewHolder(View view) {
                return new ProjectViewHolder(view);
            }

            @Override
            public void onSyncButtonTap(Project l) {
                super.onSyncButtonTap(l);
                requestSiteDataFromRemote(l.getId());
            }

            private void requestSiteDataFromRemote(String id) {
                SiteRemoteSource.getInstance()
                        .getSitesByProjectId(id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<SiteResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(SiteResponse siteResponse) {
                                Timber.d("%d sites found", siteResponse.getCount());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e);
                            }
                        });
            }
        };
        recyclerView.setAdapter(adapter);
    }

}
