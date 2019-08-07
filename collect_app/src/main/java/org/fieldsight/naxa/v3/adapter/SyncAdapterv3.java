package org.fieldsight.naxa.v3.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.v3.network.SyncAdapterCallback;
import org.fieldsight.naxa.v3.network.SyncStat;
import org.fieldsight.naxa.v3.network.Syncable;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * @author: yubaraj poudel
 * @Since 2019/05/10
 * <p>
 * Manages the sync of all the project related contents. It is the controller that allows the user to select
 * which content of the projects to download
 **/

public class SyncAdapterv3 extends RecyclerView.Adapter<SyncViewHolder> {
    List<Project> selectedProjectList;
    HashMap<String, List<Syncable>> syncableMap;
    boolean auto;
    SyncAdapterCallback callback = null;
    boolean disableItemClick = false;
    HashMap<String, Integer> progressMap = new HashMap<>();

    public SyncAdapterv3(boolean auto, List<Project> selectedProjectList, HashMap<String, List<Syncable>> syncableMap) {
        this.auto = auto;
        this.selectedProjectList = selectedProjectList;
        this.syncableMap = syncableMap;
        initProgressMap();
        checkIfSyncing();
    }

    private void checkIfSyncing() {

    }

    private void initProgressMap() {
        for (Project project : selectedProjectList) {
            progressMap.put(project.getId(), 0);
        }
    }

    public void toggleAllSelection(HashMap<String, List<Syncable>> syncableMap) {
        syncableMap.clear();
        this.auto = !auto;
        this.syncableMap = syncableMap;
        notifyDataSetChanged();
    }

    public void setAdapterCallback(SyncAdapterCallback callback) {
        this.callback = callback;
    }

    public void disableItemClick() {
        this.disableItemClick = true;
        notifyDataSetChanged();
    }

    public void enableItemClick() {
        this.disableItemClick = false;
        notifyDataSetChanged();
    }

    public void notifyBySyncStat(List<SyncStat> syncStatList) {
        if (syncStatList != null && syncStatList.size() > 0) {

            for (SyncStat syncStat : syncStatList) {
                if (syncableMap.containsKey(syncStat.getProjectId())) {
                    List<Syncable> list = syncableMap.get(syncStat.getProjectId());
                    int syncType = Integer.parseInt(syncStat.getType());
                    boolean isValidList = syncStat.getFailedUrl().contains("[") ;
                    if (syncType > -1) {
                        Syncable syncable = list.get(syncType);
                        syncable.setStatus(syncStat.getStatus());
                        syncable.getFailedUrl().clear();
                        if (isValidList) {
                            String[] urlList = syncStat.getFailedUrl()
                                    .replace("[", "")
                                    .replace("]", "")
                                    .split(",");
                            syncable.addFailedUrl(urlList);
                        }

                    }
                    syncableMap.put(syncStat.getProjectId(), list);
                }
            }
            Timber.i("SyncAdapterV3 syncedMessage = %s", syncableMap.toString());
            notifyProgressBar();
            notifyDataSetChanged();
        }
    }

    private void notifyProgressBar() {
        for (String key : syncableMap.keySet()) {
            List<Syncable> syncableList = syncableMap.get(key);
            int totalSynced = 0;
            int totalSize = 0;
            for (Syncable syncable : syncableList) {
                if (!syncable.getSync())
                    continue;
                if (syncable.getStatus() == Constant.DownloadStatus.COMPLETED) {
                    totalSynced++;
                }
                totalSize++;
            }
            progressMap.put(key, totalSynced * 100 / totalSize);
        }
    }

    @NonNull
    @Override
    public SyncViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SyncViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_sync_all, viewGroup, false)) {
            @Override
            public void downloadListItemClicked(int parentPos, int pos) {
                Timber.i("Syncadapterv3, project details clicked");
                if (callback != null) {
                    Project p = selectedProjectList.get(parentPos);
                    syncableMap.get(p.getId()).get(pos).toggleSync();
                    notifyDataSetChanged();
                    callback.childDownloadListSelectionChange(p, syncableMap.get(p.getId()));
                }
            }

            @Override
            public void retryButtonClicked(Project project, String[] failedUrls) {
                if(callback != null){
                    callback.onRetryButtonClicked(project,failedUrls);
                }
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull SyncViewHolder syncViewHolder, int i) {
        Project project = selectedProjectList.get(i);
        syncViewHolder.bindView(project, progressMap, disableItemClick);
        List<Syncable> syncables = syncableMap.get(project.getId());
        syncViewHolder.manageChildView(syncables, disableItemClick);
        syncViewHolder.iv_cancel.setOnClickListener(v -> {
            if (callback != null) {
                callback.onRequestInterrupt(i, project);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedProjectList.size();
    }

    public void removeAndNotify(int pos) {
        String projectId = selectedProjectList.get(pos).getId();
        if (syncableMap.keySet().contains(projectId)) {
            syncableMap.remove(selectedProjectList.get(pos).getId());
        }
        progressMap.remove(projectId);
        selectedProjectList.remove(pos);
        notifyDataSetChanged();
    }
}


