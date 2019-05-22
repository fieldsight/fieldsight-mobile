package org.bcss.collect.naxa.v3.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.v3.network.SyncAdapterCallback;
import org.bcss.collect.naxa.v3.network.SyncStat;
import org.bcss.collect.naxa.v3.network.Syncable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
    }

    private void initProgressMap() {
        for(Project project : selectedProjectList) {
            progressMap.put(project.getId(), 0);
        }
    }

    public void toggleAllSelection(HashMap<String, List<Syncable>> syncableMap) {
        syncableMap.clear();
        this.auto = ! auto;
        this.syncableMap = syncableMap;
        notifyDataSetChanged();
    }

    public void setAdapterCallback(SyncAdapterCallback callback) {
        this.callback = callback;
    }

    public void disableItemClick(){
        this.disableItemClick = true;
    }

    public void enableItemClick() {
        this.disableItemClick = false;
    }

    public void notifyBySyncStat(List<SyncStat> syncStatList) {
        if(syncStatList != null && syncStatList.size() > 0) {
            for(SyncStat syncStat : syncStatList) {
                List<Syncable> list = syncableMap.get(syncStat.getProjectId());
                int syncType = Integer.parseInt(syncStat.getType());
                if(syncType > -1) {
                    Syncable syncable = list.get(syncType);
                    syncable.setStatus(syncStat.getStatus());
                }
                syncableMap.put(syncStat.getProjectId(), list);
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
            for(Syncable syncable : syncableList) {
                if(syncable.getStatus() == Constant.DownloadStatus.COMPLETED) {
                    totalSynced++;
                }
                totalSize += syncable.getSync() ? 1 : 0;
            }
            progressMap.put(key, totalSynced*100/totalSize);
        }
    }

    @NonNull
    @Override
    public SyncViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SyncViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_sync_all, viewGroup, false)) {
            @Override
            public void downloadListItemClicked(int parentPos, int pos) {
                Timber.i("Syncadapterv3, project details clicked");
                if(callback != null && !disableItemClick) {
                    Project p = selectedProjectList.get(parentPos);
                    syncableMap.get(p.getId()).get(pos).toggleSync();
                    notifyDataSetChanged();
                    callback.childDownloadListSelectionChange(p, syncableMap.get(p.getId()));
                }
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull SyncViewHolder syncViewHolder, int i) {
        Project project = selectedProjectList.get(i);
        syncViewHolder.bindView(project, progressMap);
        List<Syncable> syncables = syncableMap.get(project.getId());
        syncViewHolder.manageChildView(syncables);
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
        if(syncableMap.keySet().contains(selectedProjectList.get(pos).getId())) {
            syncableMap.remove(selectedProjectList.get(pos).getId());
        }
        selectedProjectList.remove(pos);
        notifyDataSetChanged();
    }
}


