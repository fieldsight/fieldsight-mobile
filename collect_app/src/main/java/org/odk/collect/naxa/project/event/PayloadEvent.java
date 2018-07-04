package org.odk.collect.naxa.project.event;

import android.support.annotation.NonNull;

import org.odk.collect.naxa.login.model.Project;

import java.util.ArrayList;
import java.util.List;

public class PayloadEvent {

    private final List<Project> projectArrayList;

    public PayloadEvent(List<Project> projectArrayList) {
        this.projectArrayList = projectArrayList;
    }

    @NonNull
    public List<Project> getPayload() {
        if (projectArrayList == null) {
            return new ArrayList<>();
        }
        return projectArrayList;
    }
}
