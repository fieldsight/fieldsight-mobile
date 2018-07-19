package org.odk.collect.naxa.project;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.onboarding.DownloadActivity;
import org.odk.collect.naxa.project.adapter.MyProjectsAdapter;
import org.odk.collect.naxa.project.db.ProjectViewModel;
import org.odk.collect.naxa.project.event.ErrorEvent;
import org.odk.collect.naxa.project.event.PayloadEvent;
import org.odk.collect.naxa.project.event.ProgressEvent;
import org.odk.collect.naxa.site.db.SiteViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ProjectListActivity extends AppCompatActivity implements ProjectView {

    @BindView(R.id.toolbar_general)
    Toolbar toolbar;
    @BindView(R.id.tv_toolbar_message)
    TextView tvToolbarMessage;
    @BindView(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;
    @BindView(R.id.appbar_general)
    AppBarLayout appbarGeneral;
    @BindView(R.id.my_projects_list)
    RecyclerView rvProjects;
    @BindView(R.id.coordinatorLayout_project_listing)
    CoordinatorLayout coordinatorLayoutProjectListing;


    private List<Project> projectList = new ArrayList<>();
    private ProjectPresenterImpl projectPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        ButterKnife.bind(this);

        setupToolbar();
        setupProjectlist();

        projectPresenter = new ProjectPresenterImpl(this);


        ViewModelProviders.of(this)
                .get(ProjectViewModel.class)
                .getAllProjectsLive()
                .observe(this, projects -> projectPresenter.showContent(projects));

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.projects);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_fieldsight, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:

                DownloadActivity.start(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupProjectlist() {
        MyProjectsAdapter projectlistAdapter = new MyProjectsAdapter(projectList);
        RecyclerView.LayoutManager myProjectLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvProjects.setLayoutManager(myProjectLayoutManager);
        rvProjects.setItemAnimator(new DefaultItemAnimator());
        rvProjects.setAdapter(projectlistAdapter);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent errorEvent) {
        showProgress(false);
        showContent(false, null);
        showEmpty(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onContentEvent(PayloadEvent payloadEvent) {
        showProgress(false);
        showContent(true, payloadEvent.getPayload());
        showEmpty(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProgressEvent(ProgressEvent progressEvent) {
        showProgress(true);
        showContent(false, null);
        showEmpty(false);
    }

    @Override
    public void showProgress(boolean show) {
        Timber.i("Showing progress %s", show);
    }

    @Override
    public void showEmpty(boolean show) {
        Timber.i("Showing empty %s", show);
    }

    @Override
    public void showContent(boolean show, List<Project> projectList) {
        this.projectList = projectList;
        Timber.i("Showing content %s", show);
        if (show && projectList != null) {
            rvProjects.swapAdapter(new MyProjectsAdapter(projectList), true);
        }
    }


}
