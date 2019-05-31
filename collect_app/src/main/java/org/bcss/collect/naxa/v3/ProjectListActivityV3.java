package org.bcss.collect.naxa.v3;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.common.InternetUtils;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.notificationslist.NotificationListActivity;
import org.bcss.collect.naxa.project.ProjectListActivity;
import org.bcss.collect.naxa.project.data.ProjectRepository;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.sync.ContentDownloadActivity;
import org.bcss.collect.naxa.v3.adapter.ProjectListAdapter;
import org.bcss.collect.naxa.v3.network.LoadProjectCallback;
import org.bcss.collect.naxa.v3.network.ProjectNameTuple;
import org.bcss.collect.naxa.v3.network.SyncActivity;
import org.bcss.collect.naxa.v3.network.SyncLocalSourcev3;
import org.json.JSONArray;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static org.bcss.collect.android.application.Collect.allowClick;

/**
 * pull the project list from the new api. show the list of the project
 * it has two options - 1. sync all project without selection at a time
 * 2. sync project with the selection
 * add the checkbox in the project to allow the user to select and deselect the project
 *
 * @Since 2019-05-09
 * @Author Yubaraj Poudel
 * <p>
 * steps:
 * 1. Create the list of the project for sync {@code Set<String> syncList }
 * 2. Depending upon the selection remove the items from the list
 * 3. create a flag for controlled sync or automatic sync {@code boolean auto = false }
 * 4. if auto is true
 * 4.1 sync all the selected projects
 * 5. if auto is false
 * 5.1 sync projects but allow the user to select what they want to sync
 **/


public class ProjectListActivityV3 extends CollectAbstractActivity {
    @BindView(R.id.rv_projectlist)
    RecyclerView rv_projectlist;

    @BindView(R.id.ll_nodata)
    LinearLayout ll_nodata;

    @BindView(R.id.tv_nodata)
    TextView tv_nodata;

    @BindView(R.id.prgbar)
    ProgressBar prgbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_sync_project)
    TextView tv_sync_project;

    @BindView(R.id.cv_resync)
    CardView cv_resync;

    ProjectListAdapter adapter = null;
    List<Project> projectList = new ArrayList<>();
    boolean auto = false;
    RecyclerView.AdapterDataObserver observer;
    boolean allSelected = false;
    LiveData<List<ProjectNameTuple>> projectIds;
    Observer<List<ProjectNameTuple>> projectObserver = null;
    boolean showSyncMenu = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_simple_recycler_with_nodata);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("Projects");
        adapter = new ProjectListAdapter(projectList);
        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                int selected = 0;
                for (int i = 0; i < projectList.size(); i++) {
                    if (projectList.get(i).isChecked())
                        selected++;
                }
                Timber.d("project list counter is %d", selected);
                if (selected > 0) {
                    tv_sync_project.setVisibility(View.VISIBLE);
                    tv_sync_project.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
                    tv_sync_project.setText(String.format(Locale.getDefault(), "Sync %d projects", selected));
                } else {
                    tv_sync_project.setVisibility(View.GONE);
                    allSelected = false;
                    invalidateOptionsMenu();
                }

            }
        };

        adapter.registerAdapterDataObserver(observer);
        rv_projectlist.setLayoutManager(new LinearLayoutManager(this));
        rv_projectlist.setAdapter(adapter);
        getDataFromServer();
        manageNodata(true);
        tv_sync_project.setOnClickListener(v -> openDownloadAActivity());
        projectObserver = projectNameList -> {
            Timber.i("list live data = %d", projectNameList.size());
            adapter.notifyProjectisSynced(projectNameList);
            showSyncMenu = projectNameList.size() == 0 || projectNameList.size() < adapter.getItemCount();
            invalidateOptionsMenu();
        };
        projectIds = SyncLocalSourcev3.getInstance().getAllSiteSyncingProject();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(tv_sync_project.getVisibility() == View.VISIBLE) {
            tv_sync_project.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.cv_resync)
    void resyncProject() {
        getDataFromServer();
        manageNodata(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (observer != null)
            adapter.unregisterAdapterDataObserver(observer);
        if (projectIds != null && projectIds.hasObservers() && projectObserver != null)
            projectIds.removeObserver(projectObserver);
    }

    void manageNodata(boolean loading) {
        if (adapter.getItemCount() == 0) {
            ll_nodata.setVisibility(View.VISIBLE);
            cv_resync.setVisibility(loading ? View.GONE : View.VISIBLE);
        } else {
            ll_nodata.setVisibility(View.GONE);
        }
        prgbar.setVisibility(loading ? View.VISIBLE : View.GONE);
        tv_nodata.setText(loading ? "Loading data ... " : "Error in syncing the project");
    }

    void getDataFromServer() {
        ProjectRepository.getInstance().getAll(new LoadProjectCallback() {
            @Override
            public void onProjectLoaded(List<Project> projects) {
                projectList.addAll(projects);
                adapter.notifyDataSetChanged();
                manageNodata(false);
                Timber.e("data found with %d size", projects.size());
                projectIds.observe(ProjectListActivityV3.this, projectObserver);
            }

            @Override
            public void onDataNotAvailable() {
                Timber.d("data not available");
                manageNodata(false);
            }
        });
    }

    //    Clear the sync project list and add the selected projects
    ArrayList<Project> manageSyncList() {
        ArrayList<Project> syncProjectList = new ArrayList<>();
        for (Project project : projectList) {
            if (project.isChecked()) {
                syncProjectList.add(project);
            }
        }
        return syncProjectList;
    }

    void openDownloadAActivity() {
        ArrayList<Project> syncProjectList = manageSyncList();
        if (syncProjectList.size() > 0) {
            Intent intent = new Intent(this, SyncActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("projects", syncProjectList);
            bundle.putBoolean("auto", true);
            intent.putExtra("params", bundle);
            startActivity(intent);
        } else {
            ToastUtils.showShortToastInMiddle("Please select at least one projects");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_fieldsight, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_refresh).setVisible(showSyncMenu);
        if(showSyncMenu) {
            menu.findItem(R.id.action_refresh).setIcon(allSelected ?
                    R.drawable.ic_cancel_white_24dp :
                    R.drawable.ic_action_sync
            );
            menu.findItem(R.id.action_refresh).setTitle(allSelected ? "Cancel" : "sync");
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_search:
//                break;
            case R.id.action_refresh:
//                check all the project and make auto true
                allSelected = !allSelected;
                for (Project project : projectList) {
                    if (!project.isSynced()) {
                        project.setChecked(allSelected);
                    } else {
                        project.setChecked(false);
                    }
                }
                adapter.notifyDataSetChanged();
                invalidateOptionsMenu();
                break;
            case R.id.action_notificaiton:
                NotificationListActivity.start(this);
                break;
            case R.id.action_logout:
                FieldSightUserSession.showLogoutDialog(this);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}


