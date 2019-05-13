package org.bcss.collect.naxa.v3.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.v3.network.SyncAdapterCallback;

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
    HashMap<String, List<Syncable>> syncableMap = new HashMap<>();
    boolean auto;
    SyncAdapterCallback callback = null;

    // this class will manage the sync list to determine which should be synced
    public class Syncable {
        String title;
        boolean sync;

        public String getTitle(){
            return this.title;
        }
        public boolean getSync() {
            return this.sync;
        }

        /**
         * @param title - title that is show in the list
         * @param sync  - selector to include in the downlod or not. if no need to download {@code sync = false }
         */
        public Syncable(String title, boolean sync) {
            this.title = title;
            this.sync = sync;
        }
    }

    ArrayList<Syncable> createList() {
        ArrayList<Syncable> list = new ArrayList<Syncable>() {{
            add(new Syncable("Regions and sites", auto));
            add(new Syncable("Forms", auto));
            add(new Syncable("Materials", auto));
        }};
        return list;
    }

    void createSyncableList() {
        for (Project project : selectedProjectList) {
            syncableMap.put(project.getId(), createList());
        }
    }


    public SyncAdapterv3(boolean auto, List<Project> selectedProjectList) {
        this.auto = auto;
        this.selectedProjectList = selectedProjectList;
        this.createSyncableList();
    }

    public void toggleAllSelection() {
        syncableMap.clear();
        this.auto = ! auto;
        createSyncableList();
        notifyDataSetChanged();
    }

    public void setAdapterCallback(SyncAdapterCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public SyncViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SyncViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_sync_all, viewGroup, false)) {
            @Override
            public void downloadListItemClicked(int parentPos, int pos) {
                Timber.i("Syncadapterv3, project details clicked");
                if(callback != null) {
                    Project p = selectedProjectList.get(parentPos);
                    syncableMap.get(p.getId()).get(pos).sync = ! syncableMap.get(p.getId()).get(pos).sync;
                    notifyDataSetChanged();
                    callback.childDownloadListSelectionChange(p, syncableMap.get(p.getId()));
                }
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull SyncViewHolder syncViewHolder, int i) {
        Project project = selectedProjectList.get(i);
        syncViewHolder.bindView(project);
        List<Syncable> syncables = syncableMap.get(project.getId());
        syncViewHolder.manageChildView(syncables);
        syncViewHolder.iv_cancel.setOnClickListener(v -> {
            if (callback != null) {
                callback.onRequestInterrupt(project);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedProjectList.size();
    }

}


