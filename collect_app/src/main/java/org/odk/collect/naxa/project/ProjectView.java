package org.odk.collect.naxa.project;

import org.odk.collect.naxa.login.model.Project;

import java.util.List;

public interface ProjectView {

    void showProgress(boolean show);

    void showEmpty(boolean show);

    void showContent(boolean show, List<Project> projectList);
}
