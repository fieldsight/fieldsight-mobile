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
    public interface Callback {
        void onCancelClicked(int pos);
    }

    List<Project> projectList;
    Callback callback;

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
        return new ProjectSyncViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectSyncViewholder holder, int position) {
        Project project = projectList.get(position);
        holder.bindView(project, false);
        holder.itemView.findViewById(R.id.iv_cancel).setOnClickListener(v -> callback.onCancelClicked(position));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }
}


