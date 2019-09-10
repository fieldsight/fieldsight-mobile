package org.fieldsight.naxa.forms.source.local;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;

@Entity(tableName = "fieldsight_forms",
        primaryKeys = {"fieldSightFormId"})
public class FieldSightForm {

    @NonNull
    @SerializedName("id")
    private String fieldSightFormId;

    @SerializedName("site")
    private String formDeployedSiteId;

    @SerializedName("project")
    private String formDeployedProjectId;

    @SerializedName("site_project_id")
    private String projectId;

    @SerializedName("downloadUrl")
    private String formDownloadUrl;

    @SerializedName("manifestUrl")
    private String manifestDownloadUrl;

    @SerializedName("name")
    private String formName;

    @SerializedName("descriptionText")
    private String formDescriptionText;

    @SerializedName("formID")
    private String odkFormID;

    @SerializedName("version")
    private String odkFormVersion;

    @SerializedName("hash")
    private String odkFormHash;

    @Ignore
    @SerializedName("schedule")
    private JSONObject schedules;

    @Ignore
    @SerializedName("sub_stages")
    private ArrayList<JSONObject> subStages;

    private String formType;

    public String getFieldSightFormId() {
        return fieldSightFormId;
    }

    public String getFormDeployedSiteId() {
        return formDeployedSiteId;
    }

    public String getFormDeployedProjectId() {
        return formDeployedProjectId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getFormDownloadUrl() {
        return formDownloadUrl;
    }

    public String getManifestDownloadUrl() {
        return manifestDownloadUrl;
    }

    public String getOdkFormName() {
        return formName;
    }

    public String getFormDescriptionText() {
        return formDescriptionText;
    }

    public String getOdkFormID() {
        return odkFormID;
    }

    public String getOdkFormVersion() {
        return odkFormVersion;
    }

    public String getOdkFormHash() {
        return odkFormHash;
    }

    public JSONObject getSchedules() {
        return schedules;
    }

    public ArrayList<JSONObject> getSubStages() {
        return subStages;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String type) {
        this.formType = type;

    }

    public void setFieldSightFormId(String fieldSightFormId) {
        this.fieldSightFormId = fieldSightFormId;
    }

    public void setFormDeployedSiteId(String formDeployedSiteId) {
        this.formDeployedSiteId = formDeployedSiteId;
    }

    public void setFormDeployedProjectId(String formDeployedProjectId) {
        this.formDeployedProjectId = formDeployedProjectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setFormDownloadUrl(String formDownloadUrl) {
        this.formDownloadUrl = formDownloadUrl;
    }

    public void setManifestDownloadUrl(String manifestDownloadUrl) {
        this.manifestDownloadUrl = manifestDownloadUrl;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public void setFormDescriptionText(String formDescriptionText) {
        this.formDescriptionText = formDescriptionText;
    }

    public void setOdkFormID(String odkFormID) {
        this.odkFormID = odkFormID;
    }

    public void setOdkFormVersion(String odkFormVersion) {
        this.odkFormVersion = odkFormVersion;
    }

    public void setOdkFormHash(String odkFormHash) {
        this.odkFormHash = odkFormHash;
    }

    public void setSchedules(JSONObject schedules) {
        this.schedules = schedules;
    }

    public void setSubStages(ArrayList<JSONObject> subStages) {
        this.subStages = subStages;
    }
}
