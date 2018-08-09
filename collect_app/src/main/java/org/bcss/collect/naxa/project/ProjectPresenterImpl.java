package org.bcss.collect.naxa.project;

import org.bcss.collect.naxa.login.model.Project;

import java.util.List;

public class ProjectPresenterImpl implements ProjectPresenter {

    private ProjectView projectView;
    private ProjectModel projectModel;

    public ProjectPresenterImpl(ProjectView projectView) {
        this.projectView = projectView;
        this.projectModel = new ProjectModelImpl();
    }

    @Override
    public void initiateDownload() {
        projectView.showProgress(true);
        projectModel.downloadUserInformation();
    }

    public void showContent(List<Project> projectList){
        projectView.showProgress(false);
        projectView.showEmpty(false);
        projectView.showContent(true,projectList);
    }
}
