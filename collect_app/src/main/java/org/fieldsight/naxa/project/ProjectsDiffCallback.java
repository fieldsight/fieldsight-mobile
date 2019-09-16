package org.fieldsight.naxa.project;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import org.fieldsight.naxa.login.model.Project;

import java.util.List;

public class ProjectsDiffCallback extends DiffUtil.Callback {

    private List<Project> oldProjects;
    private List<Project> newProjects;

    public ProjectsDiffCallback(List<Project> newProjects, List<Project> oldProjects) {
        this.newProjects = newProjects;
        this.oldProjects = oldProjects;
    }


    @Override
    public int getOldListSize() {
        return oldProjects.size();
    }

    @Override
    public int getNewListSize() {
        return newProjects.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldProjects.get(oldItemPosition).getId()
                .equals(newProjects.get(newItemPosition).getId());

    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldProjects.get(oldItemPosition).equals(newProjects.get(newItemPosition));

    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
