package org.bcss.collect.naxa.project;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.android.application.ForceUpdateChecker;
import org.bcss.collect.naxa.BackupActivity;
import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.common.InternetUtils;
import org.bcss.collect.naxa.common.RecyclerViewEmptySupport;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.common.event.DataSyncEvent;
import org.bcss.collect.naxa.common.utilities.SnackBarUtils;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.notificationslist.NotificationListActivity;
import org.bcss.collect.naxa.preferences.SettingsActivity;
import org.bcss.collect.naxa.project.adapter.MyProjectsAdapter;
import org.bcss.collect.naxa.project.data.ProjectViewModel;
import org.bcss.collect.naxa.report.ReportActivity;
import org.bcss.collect.naxa.site.ProjectDashboardActivity;
import org.bcss.collect.naxa.sync.ContentDownloadActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ProjectListActivity extends CollectAbstractActivity implements MyProjectsAdapter.OnItemClickListener, ForceUpdateChecker.OnUpdateNeededListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar_general)
    AppBarLayout appbarGeneral;
    @BindView(R.id.my_projects_list)
    RecyclerViewEmptySupport rvProjects;
    @BindView(R.id.coordinatorLayout_project_listing)
    CoordinatorLayout coordinatorLayoutProjectListing;


    private MyProjectsAdapter projectlistAdapter;
    private ProjectViewModel viewModel;


    public static void start(Context context) {
        Intent intent = new Intent(context, ProjectListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        setupWindowTransition();
        ButterKnife.bind(this);
        setupToolbar();
        setupProjectList();

        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(ProjectViewModel.class);
        viewModel
                .getAll(false)
                .observe(ProjectListActivity.this, projects -> {
                    if (projectlistAdapter.getItemCount() == 0) {
                        projectlistAdapter.updateList(projects);
                        runLayoutAnimation(rvProjects);
                    } else {
                        projectlistAdapter.updateList(projects);
                    }

                });
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {

        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        getSupportActionBar().setTitle(R.string.projects);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(BuildConfig.BUILD_TYPE.equals("release")) {
            menu.findItem(R.id.action_server_url).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
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
                ContentDownloadActivity.start(this);
                break;
            case R.id.action_notificaiton:
                NotificationListActivity.start(this);
                break;
            case R.id.action_logout:
                showProgress();
                InternetUtils.checkInterConnectivity(new InternetUtils.OnConnectivityListener() {
                    @Override
                    public void onConnectionSuccess() {
                        FieldSightUserSession.showLogoutDialog(ProjectListActivity.this);
                    }

                    @Override
                    public void onConnectionFailure() {
                        FieldSightUserSession.stopLogoutDialog(ProjectListActivity.this);
                    }

                    @Override
                    public void onCheckComplete() {
                        hideProgress();
                    }
                });

                break;

            case R.id.action_server_url:
                startActivity(new Intent(this, org.bcss.collect.naxa.common.SettingsActivity.class));
                return true;

            case R.id.action_backup:
                startActivity(new Intent(this, BackupActivity.class));
                return true;
            case R.id.action_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_submit_report:
                startActivity(new Intent(this, ReportActivity.class));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    private void setupProjectList() {
        projectlistAdapter = new MyProjectsAdapter(new ArrayList<>(0), this);
        RecyclerView.LayoutManager myProjectLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvProjects.setLayoutManager(myProjectLayoutManager);
        rvProjects.setEmptyView(findViewById(R.id.root_layout_empty_layout),
                "Once you are assigned to a site, you'll see projects listed here",
                () -> {
                    viewModel.getAll(true);
                });
        rvProjects.setProgressView(findViewById(R.id.progress_layout));
        rvProjects.setItemAnimator(new DefaultItemAnimator());
        rvProjects.setAdapter(projectlistAdapter);


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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DataSyncEvent event) {

        String syncItem = "projects and sites";

        Timber.i(event.toString());
        switch (event.getEvent()) {
            case DataSyncEvent.EventStatus.EVENT_START:
                SnackBarUtils.showFlashbar(this, getString(R.string.download_update_start_message, syncItem), true);
                break;
            case DataSyncEvent.EventStatus.EVENT_END:
                SnackBarUtils.showFlashbar(this, getString(R.string.download_update_end_message, syncItem), false);
                break;
            case DataSyncEvent.EventStatus.EVENT_ERROR:
                SnackBarUtils.showFlashbar(this, getString(R.string.download_update_error_message, syncItem), false);
                break;
        }
    }

    @Override
    public void onItemClick(Project project) {


        Pair<View, String> p1 = Pair.create(appbarGeneral, ViewCompat.getTransitionName(appbarGeneral));
        //inspection
        ProjectDashboardActivity.start(this, project, p1);

    }


    private void setupWindowTransition() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.slide_top);
            Transition exit = TransitionInflater.from(this).inflateTransition(R.transition.slide_bottom);

            Fade fade = new Fade();
            fade.setDuration(1000);

//            getWindow().setEnterTransition(fade);
//            getWindow().setExitTransition(fade);

        }
    }


    @Override
    public void onUpdateNeeded(String updateUrl) {
        startActivity(new Intent(this, AppUpdateActivity.class));

    }
}
