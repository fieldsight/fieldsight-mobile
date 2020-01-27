package org.fieldsight.naxa.v3.project;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.BackupActivity;
import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.helpers.FSInstancesDao;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.NetworkUtils;
import org.fieldsight.naxa.notificationslist.NotificationListActivity;
import org.fieldsight.naxa.preferences.SettingsActivity;
import org.fieldsight.naxa.project.data.ProjectRepository;
import org.fieldsight.naxa.report.ReportActivity;
import org.fieldsight.naxa.v3.adapter.ProjectListAdapter;
import org.fieldsight.naxa.v3.network.LoadProjectCallback;
import org.fieldsight.naxa.v3.network.ProjectNameTuple;
import org.fieldsight.naxa.v3.network.SyncActivity;
import org.fieldsight.naxa.v3.network.SyncLocalSource3;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.dto.Instance;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.PROJECT;

public class ProjectListActivityV3 extends CollectAbstractActivity implements SyncingProjectAdapter.Callback {
    @BindView(R.id.rv_projectlist)
    RecyclerView rvProjectlist;

    @BindView(R.id.ll_nodata)
    LinearLayout llNodata;

    @BindView(R.id.tv_nodata)
    TextView tvNodata;

    @BindView(R.id.prgbar)
    ProgressBar prgbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_sync_project)
    TextView tvSyncProject;

    @BindView(R.id.cv_resync)
    CardView cvResync;

    @BindView(R.id.rv_projectlist_syncing)
    RecyclerView rvSyncing;

//    @BindView(R.id.tv_sync)
//    TextView tvSync;

    @BindView(R.id.tv_unsync)
    TextView tvUnsync;

    ProjectListAdapter adapter;
    List<Project> projectList = new ArrayList<>();

    RecyclerView.AdapterDataObserver observer;

//    @BindView(R.id.swipe_container)
//    SwipeRefreshLayout swipeRefreshLayout;

    boolean allSelected;
    LiveData<List<ProjectNameTuple>> projectIds;
    Observer<List<ProjectNameTuple>> projectObserver;
    boolean showSyncMenu = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_simple_recycler_with_nodata);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Projects");
        adapter = new ProjectListAdapter(projectList, allSelected);

        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                int selectedNum;
                for (selectedNum = 0; selectedNum < projectList.size(); selectedNum++) {
                    if (projectList.get(selectedNum).isChecked()) {
                        break;
                    }
                }
                Timber.d("project list counter is %d", selectedNum);
                if (selectedNum == projectList.size()) {
                    tvSyncProject.setVisibility(View.GONE);
                    allSelected = false;
//                    tvSync.setVisibility(View.GONE);
                    tvUnsync.setVisibility(View.GONE);
//                    invalidateOptionsMenu();
                } else {
                    tvSyncProject.setVisibility(View.VISIBLE);
                    tvSyncProject.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
                    tvSyncProject.setText("Sync Now");
                }
            }
        };

        adapter.registerAdapterDataObserver(observer);
        rvProjectlist.setLayoutManager(new LinearLayoutManager(this));
        rvProjectlist.setAdapter(adapter);

        getDataFromServer();
        manageNodata(true);

//        tvSyncProject.setOnClickListener(v -> openDownloadAActivity());
        projectObserver = projectNameList -> {
            Timber.i("list live data = %d", projectNameList.size());
            adapter.notifyProjectisSynced(projectNameList);
            showSyncMenu = projectNameList.size() == 0 || projectNameList.size() < adapter.getItemCount();
            invalidateOptionsMenu();
        };

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                getDataFromServer();
//
//            }
//        });

        try {
            fixNullUrl();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @OnClick(R.id.tv_sync_project)
    void addInSyncList() {
        ArrayList<Project> syncProjectList = manageSyncList();
        Timber.i("=======>>>>> syncproject length = %d", syncProjectList.size());
        SyncingProjectAdapter syncAdapter = new SyncingProjectAdapter(syncProjectList, this);
        rvSyncing.setLayoutManager(new LinearLayoutManager(this));
        rvSyncing.setAdapter(syncAdapter);
//        tvSync.setVisibility(View.VISIBLE);
        tvUnsync.setVisibility(View.VISIBLE);
    }


    private void fixNullUrl() throws Exception {
        FSInstancesDao instancesDao = new FSInstancesDao();
        List<Instance> instances = instancesDao.getBySiteId("");
        for (Instance instance : instances) {
            instance.setFieldSightSiteId("0");
            String[] path = instance.getSubmissionUri().split("/");
            String lastItem = path[path.length - 1];
            String fsFormId = path[path.length - 2];
            if (TextUtils.equals("null", lastItem)) {
                String where = InstanceProviderAPI.InstanceColumns.SUBMISSION_URI + "=?";

                String[] whereArgs = {instance.getSubmissionUri()};

                String fixedUrl = FSInstancesDao.generateSubmissionUrl(PROJECT, "0", fsFormId);

                ContentValues contentValues = new ContentValues();
                contentValues.put(InstanceProviderAPI.InstanceColumns.FS_SITE_ID, "0");
                contentValues.put(InstanceProviderAPI.InstanceColumns.SUBMISSION_URI, fixedUrl);
                instancesDao.updateInstance(contentValues, where, whereArgs);
                Timber.e("Fixed %s to %s", instance.getSubmissionUri(), fixedUrl);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("ProjectListActivityv3 :: anyProject checked = " + adapter.anyProjectSelectedForSync());
        if (tvSyncProject.getVisibility() == View.VISIBLE && !adapter.anyProjectSelectedForSync()) {
            tvSyncProject.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.cv_resync)
    void resyncProject() {
        if (NetworkUtils.isNetworkConnected()) {
            getDataFromServer();
            manageNodata(true);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet_body), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (observer != null) {
            adapter.unregisterAdapterDataObserver(observer);
        }
        if (projectIds != null && projectIds.hasObservers() && projectObserver != null) {
            projectIds.removeObserver(projectObserver);
        }
    }

    void manageNodata(boolean loading) {
        if (adapter.getItemCount() == 0) {
            llNodata.setVisibility(View.VISIBLE);
            cvResync.setVisibility(loading ? View.GONE : View.VISIBLE);
        } else {
            llNodata.setVisibility(View.GONE);
        }
        prgbar.setVisibility(loading ? View.VISIBLE : View.GONE);
        tvNodata.setText(loading ? "Loading data ... " : "Error in syncing the project");
    }

    void refreshSyncStatus() {
        projectIds = SyncLocalSource3.getInstance().getAllSiteSyncingProject();
        projectIds.observe(ProjectListActivityV3.this, projectObserver);
    }

    void getDataFromServer() {
        ProjectRepository.getInstance().getAll(new LoadProjectCallback() {
            @Override
            public void onProjectLoaded(List<Project> projects) {
                adapter.clearAndUpdate(projects);
                manageNodata(false);
                Timber.e("data found with %d size", projects.size());
//                swipeRefreshLayout.setRefreshing(false);
                refreshSyncStatus();
            }

            @Override
            public void onDataNotAvailable() {
                Timber.d("data not available");
                manageNodata(false);
//                swipeRefreshLayout.setRefreshing(false);
                refreshSyncStatus();
            }
        });


    }

    //    Clear the sync PROJECT list and add the selected projects
    ArrayList<Project> manageSyncList() {
        ArrayList<Project> syncProjectList = new ArrayList<>();
        ArrayList<Project> filteredList = new ArrayList<>();
        for (int i = 0; i < projectList.size(); i++) {
            Project project = projectList.get(i);
            if (project.isChecked()) {
                syncProjectList.add(project);
            } else {
                filteredList.add(project);
            }
        }
        adapter.clearAndUpdate(filteredList);
        return syncProjectList;
    }

//    void openDownloadAActivity() {
    // changing the list as syncing and unsyncing

//        ArrayList<Project> syncProjectList = manageSyncList();
//        if (syncProjectList.size() > 0) {
//            Intent intent = new Intent(this, SyncActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("projects", syncProjectList);
//            bundle.putBoolean("auto", true);
//            intent.putExtra("params", bundle);
//            startActivity(intent);
//        } else {
//            ToastUtils.showShortToastInMiddle("Please select at least one projects");
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_fieldsight, menu);
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
////        menu.findItem(R.id.action_refresh).setVisible(showSyncMenu);
//        if (showSyncMenu) {
//            menu.findItem(R.id.action_refresh).setIcon(allSelected ?
//                    R.drawable.ic_cancel_white_24dp :
//                    R.drawable.ic_action_sync
//            );
//            menu.findItem(R.id.action_refresh).setTitle(allSelected ? "Cancel" : "sync");
//        }
//
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_backup:
                startActivity(new Intent(this, BackupActivity.class));
                return true;
            case R.id.action_refresh:
//                check all the PROJECT and make auto true
//                allSelected = !allSelected;
//                for (Project project : projectList) {
////                    if (!PROJECT.isSynced()) {
//                    project.setChecked(allSelected);
////                    } else {
////                        PROJECT.setChecked(false);
////                    }
//                }
//                adapter.toggleAllSelected(allSelected);
//                adapter.notifyDataSetChanged();
//                invalidateOptionsMenu();
                break;
            case R.id.action_notificaiton:
                NotificationListActivity.start(this);
                break;
            case R.id.action_logout:
                FieldSightUserSession.showLogoutDialog(this);
                break;
            case R.id.action_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_submit_report:
                startActivity(new Intent(this, ReportActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean exit;

    @Override
    public void finish() {
        if (allSelected) {
            allSelected = false;
            for (Project project : projectList) {
                project.setChecked(allSelected);
            }
            adapter.toggleAllSelected(allSelected);
            adapter.notifyDataSetChanged();
            invalidateOptionsMenu();
        } else {
            // exit the app in double back pressed
            if (exit) {
                super.finish();
            } else {
                Toast.makeText(getApplicationContext(), "Please double tap to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 2000);
                exit = true;
            }
        }
    }

    @Override
    public void onCancelClicked(int pos) {
        Timber.i("cancel clicked");
        Project project = ((SyncingProjectAdapter)rvSyncing.getAdapter()).popItem(pos);
        project.setChecked(false);
        adapter.push(project, pos);
    }
}


