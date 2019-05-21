package org.bcss.collect.naxa.v3.network;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.sync.ContentDownloadAdapter;
import org.bcss.collect.naxa.sync.DownloadViewModel;
import org.bcss.collect.naxa.v3.adapter.SyncAdapterv3;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.EXTRA_MESSAGE;

public class SyncActivity extends CollectAbstractActivity implements SyncAdapterCallback {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_download_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.toggle_button)
    Button toggleButton;

    @BindView(R.id.download_button)
    Button downloadButton;


    @BindView(R.id.layout_network_connectivity)
    RelativeLayout layoutNetworkConnectivity;

    private ContentDownloadAdapter adapter;
    private DownloadViewModel viewModel;
    private DisposableObserver<Boolean> connectivityDisposable;
    boolean isNetworkConnected = true;
    SyncAdapterv3 adapterv3;
    boolean auto = true;
    HashMap<String, List<Syncable>> syncableMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /// getting the selected project list from the projectlist activity
        Bundle bundle = getIntent().getBundleExtra("params");
        ArrayList<Project> projectList = bundle.getParcelableArrayList("projects");
        auto = bundle.getBoolean("auto", true);

        if (projectList == null || projectList.size() == 0) {
            return;
        }

        setTitle(String.format(Locale.getDefault(), "Projects (%d)", projectList.size()));

        /// create the map of the syncing
        createSyncableList(projectList);
        adapterv3 = new SyncAdapterv3(auto, projectList, syncableMap);
        adapterv3.setAdapterCallback(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterv3);

        findViewById(R.id.download_button).setOnClickListener(v -> {
            ToastUtils.showShortToast("Download starts");
//            send the list of project with syncable details
            Intent syncIntent = new Intent(getApplicationContext(), SyncServiceV3.class);
            syncIntent.putParcelableArrayListExtra("projects", projectList);
            syncIntent.putExtra("selection", syncableMap);
            startService(syncIntent);
        });
        toggleButton.setVisibility(View.GONE);

/// hiding the toggle selection button
//        findViewById(R.id.toggle_button).setOnClickListener(v -> {
//            adapterv3.toggleAllSelection();
//        });

    }

    // this class will manage the sync list to determine which should be synced
    ArrayList<Syncable> createList() {
        ArrayList<Syncable> list = new ArrayList<Syncable>() {{
            add(0, new Syncable("Regions and sites", auto, false, false));
            add(1, new Syncable("Forms", auto, false, false));
            add(2, new Syncable("Materials", auto, false, false));
        }};
        return list;
    }

    void createSyncableList(List<Project> selectedProjectList) {
        for (Project project : selectedProjectList) {
            syncableMap.put(project.getId(), createList());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestInterrupt(int pos, Project project) {
        DialogFactory.createActionDialog(this, getString(R.string.app_name), "Are you sure you want to remove " + project.getName() + "from download queue ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    syncableMap.remove(project.getId());
                    adapterv3.removeAndNotify(pos);
                    if (adapterv3.getItemCount() > 0)
                        setTitle("Projects (" + adapterv3.getItemCount() + ")");
                    else
                        setTitle("Projects");
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void childDownloadListSelectionChange(Project project, List<Syncable> list) {
//    add this request in download queue
        Timber.i("SyncActivity data = " + readaableSyncParams(project.getName(), list));
        syncableMap.put(project.getId(), list);
    }

    private String readaableSyncParams(String projectName, List<Syncable> list) {
        String logString = "";
        for (Syncable syncable : list) {
            logString += "\n title = " + syncable.getTitle() + ", sync = " + syncable.getSync();
        }
        return String.format("%s \n params = %s", projectName, logString);
    }
}
