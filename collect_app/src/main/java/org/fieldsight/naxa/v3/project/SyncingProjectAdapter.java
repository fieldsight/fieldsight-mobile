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

import java.util.List;

public class SyncingProjectAdapter extends RecyclerView.Adapter<ProjectSyncViewholder> {
    List<Project> projectList;

    public SyncingProjectAdapter(List<Project> projectList) {
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ProjectSyncViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_expand_sync, parent, false);
        return new ProjectSyncViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectSyncViewholder holder, int position) {
        Project project = projectList.get(position);
        holder.bindView(project, false);
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }
}


