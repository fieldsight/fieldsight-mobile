package org.odk.collect.naxa.project;

import android.support.v7.util.DiffUtil;

import org.odk.collect.naxa.generalforms.data.GeneralForm;
import org.odk.collect.naxa.login.model.Project;

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
}
