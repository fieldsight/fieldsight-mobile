package org.odk.collect.naxa.project;

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
}
