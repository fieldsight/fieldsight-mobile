package org.fieldsight.naxa.project;

import org.fieldsight.naxa.login.model.Project;

import java.util.List;

public interface ProjectView {

    void showProgress(boolean show);

    void showEmpty(boolean show);

    void showContent(boolean show, List<Project> projectList);
}
