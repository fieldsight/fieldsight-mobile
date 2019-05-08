package org.bcss.collect.naxa.project.ui;

import org.bcss.collect.naxa.login.model.Project;

import java.util.List;

class ProjectExpandableItem implements ParentListItem {

    private List<Movies> downloadItems;
    private org.bcss.collect.naxa.login.model.Project project;


    public ProjectExpandableItem(Project project, List<Movies> downloadItems) {
        this.downloadItems = downloadItems;
        this.project = project;
    }

    @Override
    public List<?> getChildItemList() {
        return downloadItems;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
