package org.bcss.collect.naxa.v3.network;

import org.bcss.collect.naxa.login.model.Project;

import java.util.List;

public interface LoadProjectCallback {
    void onProjectLoaded(List<Project> projects);

    void onDataNotAvailable();
}
