package org.fieldsight.naxa.v3.network;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.fieldsight.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.DialogFactory;
import org.fieldsight.naxa.common.DisposableManager;
import org.fieldsight.naxa.forms.FieldSightFormDownloadList;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.NetworkUtils;
import org.fieldsight.naxa.v3.adapter.SyncAdapterv3;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ApplicationConstants;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SyncActivity extends CollectAbstractActivity implements SyncAdapterCallback {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_download_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.download_button)
    Button downloadButton;

    @BindView(R.id.toolbar_message)
    TextView toolbarMessage;

    SyncAdapterv3 adapterv3;
    boolean auto = true;
    HashMap<String, List<Syncable>> syncableMap;

    LiveData<List<SyncStat>> syncdata;
    Observer<List<SyncStat>> syncObserver;
    boolean syncing ;
    ArrayList<Project> projectList;
    LiveData<Integer> runningLiveData;
    Observer<Integer> runningLiveDataObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /// getting the selected PROJECT list from the projectlist activity
        Timber.i("SyncActivity, alreadySyncing:: " + (Collect.selectedProjectList != null && Collect.selectedProjectList.size() > 0));
        if (Collect.selectedProjectList != null && Collect.selectedProjectList.size() > 0) {
            syncing = true;
            projectList = Collect.selectedProjectList;
            syncableMap = Collect.syncableMap;
        } else {
            Bundle bundle = getIntent().getBundleExtra("params");
            projectList = bundle.getParcelableArrayList("projects");
            auto = bundle.getBoolean("auto", true);
        }

        if (projectList == null || projectList.size() == 0) {
            return;
        }

        Timber.i("SyncActivity, isSyncing = " + syncing);
        // clear the sync stat table if it is not syncing when opened
        if (!syncing) {
            SyncLocalSource3.getInstance().delete();
        }

        setTitle(String.format(Locale.getDefault(), "Projects (%d)", projectList.size()));
        /// create the map of the syncing
        if (syncableMap == null) {
            createSyncableList(projectList);
        }

        adapterv3 = new SyncAdapterv3(auto, projectList, syncableMap);
        adapterv3.setAdapterCallback(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterv3);

        findViewById(R.id.download_button).setOnClickListener(v -> {
            if (NetworkUtils.isNetworkConnected()) {
                ToastUtils.showShortToast("Download starts");
                Intent syncIntent = new Intent(getApplicationContext(), SyncServiceV3.class);
                syncIntent.putParcelableArrayListExtra("projects", projectList);
                syncIntent.putExtra("selection", syncableMap);
                startService(syncIntent);

                Collect.selectedProjectList = projectList;
                Collect.syncableMap = syncableMap;
                enableDisableAdapter(true);
            } else {
                Toast.makeText(this, getString(R.string.no_internet_body), Toast.LENGTH_SHORT).show();
            }
        });

        syncObserver = syncStats -> {
            Timber.i("sync stats size = %d", syncStats.size());
            adapterv3.notifyBySyncStat(syncStats);
        };

        syncdata = SyncLocalSource3.getInstance().getAll();
        syncdata.observe(this, syncObserver);

        runningLiveDataObserver = count -> {
            Timber.i("SyncActivity, syncing = " + syncing + " count = %d", count);
            if (count == 0) {
                Timber.i("SyncActivity, enable called");
                enableDisableAdapter(false);
            }
        };
        runningLiveData = SyncLocalSource3.getInstance().getCountByStatus(Constant.DownloadStatus.RUNNING, Constant.DownloadStatus.QUEUED);
        runningLiveData.observe(this, runningLiveDataObserver);
        if (syncing) {
            enableDisableAdapter(syncing);
        }

    }

    // this class will manage the sync list to determine which should be synced
    ArrayList<Syncable> createList() {
        // -1 refers here as never started
        return new ArrayList<Syncable>() {{
            add(0, new Syncable("Regions and sites", auto, -1));
            add(1, new Syncable("Forms", auto, -1));
            add(2, new Syncable("Materials", auto, -1));
        }};
    }

    void createSyncableList(List<Project> selectedProjectList) {
        syncableMap = new HashMap<>();
        for (Project project : selectedProjectList) {
            syncableMap.put(project.getId(), createList());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        } else if (item.getItemId() == R.id.menu_item_cancel) {
            // finish sync service
            DisposableManager.dispose();
            syncing = false;
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onRequestInterrupt(int pos, Project project) {
        DialogFactory.createActionDialog(this, getString(R.string.app_name), "Are you sure you want to remove " + project.getName() + "from download queue ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    syncableMap.remove(project.getId());
                    adapterv3.removeAndNotify(pos);
                    if (adapterv3.getItemCount() > 0) {
                        setTitle("Projects (" + adapterv3.getItemCount() + ")");
                    } else {
                        setTitle("Projects");
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void childDownloadListSelectionChange(Project project, List<Syncable> list) {
//    add this request in download queue
        Timber.i("SyncActivity data = %s", readaableSyncParams(project.getName(), list));
        syncableMap.put(project.getId(), list);
    }

    @Override
    public void onRetryButtonClicked(Project project, String[] failedUrls) {

        FieldSightFormDownloadList.startForResult(this, project, failedUrls, Constant.RequestCode.DOWNLOAD_FORMS);
    }

    private void enableDisableAdapter(boolean isSyncing) {
        if (isSyncing) {
            adapterv3.disableItemClick();
        } else {
            adapterv3.enableItemClick();
        }
        downloadButton.setEnabled(!isSyncing);
//        downloadButton.setBackgroundColor(isSyncing ? getResources().getColor(R.color.disabled_gray) :
//                getResources().getColor(R.color.primaryColor));
//        downloadButton.setTextColor(getResources().getColor(R.color.white));
        this.syncing = isSyncing;
    }

    private String readaableSyncParams(String projectName, List<Syncable> list) {
        String logString = "";
        for (Syncable syncable : list) {
            logString += "\n title = " + syncable.getTitle() + ", sync = " + syncable.isSync();
        }
        return String.format("%s \n params = %s", projectName, logString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (syncdata != null && syncdata.hasObservers()) {
            syncdata.removeObserver(syncObserver);
        }
        if (runningLiveData != null && runningLiveData.hasObservers()) {
            runningLiveData.removeObserver(runningLiveDataObserver);
        }
        Timber.i("OnDestroy, isSyncing : %s", syncing);
        if (syncing) {
            Collect.syncableMap = syncableMap;
            Collect.selectedProjectList = projectList;
        } else {
            Collect.syncableMap = null;
            Collect.selectedProjectList = null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.RequestCode.DOWNLOAD_FORMS) {
            String projectId;
            HashMap<String, Boolean> statusAndForms = (HashMap<String, Boolean>) data.getSerializableExtra(ApplicationConstants.BundleKeys.FORM_IDS);

            ArrayList<String> failedUrls = new ArrayList<>();

            for (String key : statusAndForms.keySet()) {
                boolean failedToDownload = !statusAndForms.get(key);
                if (failedToDownload) {
                    failedUrls.add(key);
                }
            }

            projectId = data.getStringExtra(Constant.EXTRA_ID);

            if (failedUrls.size() > 0) {
                SyncLocalSource3.getInstance().markAsFailed(projectId, 1, failedUrls.toString());
            } else {
                SyncLocalSource3.getInstance().markAsCompleted(projectId, 1);
            }
        }
    }
}
