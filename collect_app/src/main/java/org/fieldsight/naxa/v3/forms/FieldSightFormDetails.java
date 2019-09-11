package org.fieldsight.naxa.v3.forms;

import androidx.annotation.NonNull;


import org.odk.collect.android.logic.FormDetails;

public class FieldSightFormDetails extends FormDetails {
    private int projectId;
    private int totalFormsInProject;


    public FieldSightFormDetails(Integer projectId, String formName, String downloadUrl, String manifestUrl, String formID, String formVersion, String hash, String manifestFileHash, boolean isNewerFormVersionAvailable, boolean areNewerMediaFilesAvailable) {
        super(formName, downloadUrl, manifestUrl, formID, formVersion, hash, manifestFileHash, isNewerFormVersionAvailable, areNewerMediaFilesAvailable);
        this.projectId = projectId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setTotalFormsInProject(int totalFormsInProject) {
        this.totalFormsInProject = totalFormsInProject;
    }

    public int getTotalFormsInProject() {
        return totalFormsInProject;
    }

    @NonNull
    @Override
    public String toString() {
        return "FieldSightFormDetails{" +
                "projectId=" + projectId +
                ", formName=" + getFormName() +
                ", downloadUrl=" + getDownloadUrl() +
                '}';
    }
}
