package org.bcss.collect.naxa.survey;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "survey_forms")
public class SurveyForm {

    @NonNull
    @PrimaryKey
    private String fsFormId;
    private String projectId;
    private String idString;
    private String name;

    public SurveyForm(){

    }

    @Ignore
    public SurveyForm(String fsFormId, String projectId, String idString, String name) {
        this.fsFormId = fsFormId;
        this.projectId = projectId;
        this.idString = idString;
        this.name = name;
    }

    public String getFsFormId() {
        return fsFormId;
    }

    public void setFsFormId(String fsFormId) {
        this.fsFormId = fsFormId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
