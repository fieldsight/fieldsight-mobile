package org.fieldsight.naxa.v3.forms;

import org.odk.collect.android.logic.FormDetails;

public class FieldSightFormDetails extends FormDetails {
    private int projectId;

    public FieldSightFormDetails(String error) {
        super(error);
    }

    public FieldSightFormDetails(Integer projectId, String formName, String downloadUrl, String manifestUrl, String formID, String formVersion, String hash, String manifestFileHash, boolean isNewerFormVersionAvailable, boolean areNewerMediaFilesAvailable) {
        super(formName, downloadUrl, manifestUrl, formID, formVersion, hash, manifestFileHash, isNewerFormVersionAvailable, areNewerMediaFilesAvailable);
        this.projectId = projectId;
    }

    public int getProjectId() {
        return projectId;
    }


}
