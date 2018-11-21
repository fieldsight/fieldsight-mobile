package org.bcss.collect.naxa.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.login.model.Project;

public class ProjectResponse {

    @SerializedName("project")
    @Expose
    private Project project;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

}
