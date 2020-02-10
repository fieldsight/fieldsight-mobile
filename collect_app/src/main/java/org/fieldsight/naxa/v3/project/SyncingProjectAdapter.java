package org.fieldsight.naxa.v3.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.bcss.collect.android.R;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.v3.adapter.ProjectSyncViewholder;
import org.fieldsight.naxa.v3.adapter.ProjectViewHolder;
import org.fieldsight.naxa.v3.network.Syncable;
import org.odk.collect.android.utilities.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SyncingProjectAdapter extends RecyclerView.Adapter<ProjectSyncViewholder> {

    public void updateAdapter(List<Project> syncProjectList) {
        this.projectList.addAll(0,syncProjectList);
        notifyItemRangeChanged(0, syncProjectList.size());
    }



    public interface Callback {
        void syncedProjectClicked(Project project);
        void onCancelClicked(int pos);
        void retryClicked(int pos);
    }

    List<Project> projectList;
    Callback callback;
    HashMap<String, List<Syncable>> syncableMap = new HashMap<>();

    public SyncingProjectAdapter(List<Project> projectList, Callback callback) {
        this.projectList = projectList;
        this.callback = callback;
    }

    public Project popItem(int index) {
        Project project = projectList.get(index);
        projectList.remove(index);
        notifyDataSetChanged();
        return project;
    }

    @NonNull
    @Override
    public ProjectSyncViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_expand_sync, parent, false);
        return new ProjectSyncViewholder(view) {
            @Override
            public void hasSyncComplete(int index) {
                projectList.get(index).setSynced(true);
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectSyncViewholder holder, int position) {
        Project project = projectList.get(position);
        holder.bindView(project, false, syncableMap.containsKey(project.getId()) ? syncableMap.get(project.getId()) : null);
        holder.itemView.setOnClickListener(v -> {
            if(project.isSynced()) {
                callback.syncedProjectClicked(project);
            }
        });

        holder.itemView.findViewById(R.id.iv_cancel).setOnClickListener(v -> {
//            if(v.getTag()== null || v.getTag().equals("syncing")) {
//                callback.onCancelClicked(position);
//            } else if(v.equals("retry")){
                callback.retryClicked(position);
//            }
        });
    }

    public void updateSyncMap(HashMap<String, List<Syncable>> syncableMap) {
        this.syncableMap = syncableMap;
        this.notifyDataSetChanged();
    }

    public List<Project> getUnsyncedProject() {
        List<Project> unsynced = new ArrayList<>();
        for(int i = 0; i < this.projectList.size(); i ++) {
            if(!this.projectList.get(i).isSynced()) {
                unsynced.add(this.projectList.get(i));
            }
        }
        return unsynced;
    }

    public List<Project> popItemByIds(String... ids) {
        List<Project> newSyncedList = new ArrayList<>();
        List<Project> filteredList = new ArrayList<>();
        for( int i =0; i < this.projectList.size(); i++) {
            boolean found = false;
            int j;
            for(j = 0; j < ids.length; j ++) {
                if (this.projectList.get(i).getId().equals(ids[j])) {
                    found = true;
                    break;
                }
            }
            if(found) {
                this.projectList.get(i).setSynced(false);
                this.projectList.get(i).setChecked(false);
                newSyncedList.add(this.projectList.get(i));
            }else {
                filteredList.add(this.projectList.get(i));
            }

        }


        this.projectList.clear();
        this.projectList.addAll(filteredList);
        notifyDataSetChanged();
        return newSyncedList;
    }

    public void notifyProjectSyncStatusChange(String projectId) {
        // get the index of the selected project id
        int foundIndex = -1;
        for(int i = 0; i < this.projectList.size(); i++) {
            if(projectId.equals(projectList.get(i).getId())) {
                foundIndex = i;
                break;
            }
        }

        if(foundIndex > -1) {
            this.projectList.get(foundIndex).setSynced(true);
            notifyItemChanged(foundIndex);
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }
}


