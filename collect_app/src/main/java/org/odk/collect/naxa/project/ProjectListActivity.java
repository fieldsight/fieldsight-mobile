package org.odk.collect.naxa.project;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.naxa.common.FieldSightUserSession;
import org.odk.collect.naxa.common.RecyclerViewEmptySupport;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.common.utilities.FlashBarUtils;
import org.odk.collect.naxa.common.ViewModelFactory;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.onboarding.DownloadActivity;
import org.odk.collect.naxa.project.adapter.MyProjectsAdapter;
import org.odk.collect.naxa.project.data.ProjectViewModel;
import org.odk.collect.naxa.site.ProjectDashboardActivity;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ProjectListActivity extends CollectAbstractActivity implements MyProjectsAdapter.OnItemClickListener {

    @BindView(R.id.toolbar_general)
    Toolbar toolbar;
    @BindView(R.id.tv_toolbar_message)
    TextView tvToolbarMessage;
    @BindView(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;
    @BindView(R.id.appbar_general)
    AppBarLayout appbarGeneral;
    @BindView(R.id.my_projects_list)
    RecyclerViewEmptySupport rvProjects;
    @BindView(R.id.coordinatorLayout_project_listing)
    CoordinatorLayout coordinatorLayoutProjectListing;


    private MyProjectsAdapter projectlistAdapter;
    private ProjectViewModel viewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        setupWindowTransition();
        ButterKnife.bind(this);
        setupToolbar();
        setupProjectlist();

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(ProjectViewModel.class);


        viewModel
                .getAll(false)
                .observe(ProjectListActivity.this, projects -> {
                    Timber.i("Projects data changing %s", projects.size());
                    projectlistAdapter.updateList(projects);
                });
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
            case R.id.action_logout:
                ReactiveNetwork.checkInternetConnectivity()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSingleObserver<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                if (aBoolean) {
                                    FieldSightUserSession.createLogoutDialog(ProjectListActivity.this);
                                } else {
                                    FieldSightUserSession.stopLogoutDialog(ProjectListActivity.this);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupProjectlist() {
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
                FlashBarUtils.showFlashbar(this, getString(R.string.download_update_start_message, syncItem), true);
                break;
            case DataSyncEvent.EventStatus.EVENT_END:
                FlashBarUtils.showFlashbar(this, getString(R.string.download_update_end_message, syncItem), false);
                break;
            case DataSyncEvent.EventStatus.EVENT_ERROR:
                FlashBarUtils.showFlashbar(this, getString(R.string.download_update_error_message, syncItem), false);
                break;
        }
    }

    @Override
    public void onItemClick(Project project) {


        Pair<View, String> p1 = Pair.create(appbarGeneral, ViewCompat.getTransitionName(appbarGeneral));
        //inspection
        ProjectDashboardActivity.start(this,project,p1);

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
}
