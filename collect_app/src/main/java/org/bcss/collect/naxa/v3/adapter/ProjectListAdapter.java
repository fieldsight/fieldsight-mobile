package org.bcss.collect.naxa.v3.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.login.model.Project;

import java.util.List;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectViewHolder> {
   List<Project> projectList;

    public ProjectListAdapter(List<Project> projectList) {
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProjectViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.project_expand, viewGroup, false)) {
            @Override
            void checkBoxChanged(int index, boolean isChecked) {
                super.checkBoxChanged(index, isChecked);
                 projectList.get(index).setChecked(isChecked);
            }

            @Override
            void itemClicked(int index) {
                super.itemClicked(index);
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder projectViewHolder, int i) {
        projectViewHolder.bindView(projectList.get(i));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }
}
