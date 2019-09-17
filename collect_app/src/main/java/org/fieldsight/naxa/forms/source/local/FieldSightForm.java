package org.fieldsight.naxa.forms.source.local;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

import org.fieldsight.naxa.stages.data.SubStage;

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

    @SerializedName("metadata")
    private String metadata;

    @Ignore
    @SerializedName("schedule")
    private FormSchedule schedules;

    @Ignore
    @SerializedName("sub_stages")
    private ArrayList<SubStage> subStages;

    @SerializedName("order")
    private Integer formOrder;

    private String formType;

    @Ignore
    private String siteId;

    public String getMetadata() {
        return metadata;
    }

    @NonNull
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

    public FormSchedule getSchedules() {
        return schedules;
    }

    public ArrayList<SubStage> getSubStages() {
        return subStages;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String type) {
        this.formType = type;
    }

    void setFieldSightFormId(@NonNull String fieldSightFormId) {
        this.fieldSightFormId = fieldSightFormId;
    }

    void setFormDeployedSiteId(String formDeployedSiteId) {
        this.formDeployedSiteId = formDeployedSiteId;
    }

    void setFormDeployedProjectId(String formDeployedProjectId) {
        this.formDeployedProjectId = formDeployedProjectId;
    }

    public Integer getFormOrder() {
        return formOrder;
    }

    public void setFormOrder(Integer formOrder) {
        this.formOrder = formOrder;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    void setFormDownloadUrl(String formDownloadUrl) {
        this.formDownloadUrl = formDownloadUrl;
    }

    void setManifestDownloadUrl(String manifestDownloadUrl) {
        this.manifestDownloadUrl = manifestDownloadUrl;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    void setFormDescriptionText(String formDescriptionText) {
        this.formDescriptionText = formDescriptionText;
    }

    void setOdkFormID(String odkFormID) {
        this.odkFormID = odkFormID;
    }

    void setOdkFormVersion(String odkFormVersion) {
        this.odkFormVersion = odkFormVersion;
    }

    void setOdkFormHash(String odkFormHash) {
        this.odkFormHash = odkFormHash;
    }

    public void setSchedules(FormSchedule schedules) {
        this.schedules = schedules;
    }

    public void setSubStages(ArrayList<SubStage> subStages) {
        this.subStages = subStages;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    @NonNull
    @Override
    public String toString() {
        return "FieldSightForm{" +
                "fieldSightFormId='" + fieldSightFormId + '\'' +
                ", formDeployedSiteId='" + formDeployedSiteId + '\'' +
                ", formDeployedProjectId='" + formDeployedProjectId + '\'' +
                ", formType='" + formType + '\'' +
                '}';
    }
}
