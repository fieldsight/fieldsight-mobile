package org.bcss.collect.naxa.project;

import org.bcss.collect.naxa.login.model.Project;

import java.util.List;

public interface ProjectView {

    void showProgress(boolean show);

    void showEmpty(boolean show);

    void showContent(boolean show, List<Project> projectList);
}
