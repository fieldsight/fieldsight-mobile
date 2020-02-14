package org.fieldsight.naxa.v3.network;

import org.fieldsight.naxa.login.model.Project;

import java.util.List;

public interface LoadProjectCallback {
    void onProjectLoaded(List<Project> projects, boolean fromOnline);

    void onDataNotAvailable();
}
