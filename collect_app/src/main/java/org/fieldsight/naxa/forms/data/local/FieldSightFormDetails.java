package org.fieldsight.naxa.forms.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


import com.google.gson.annotations.SerializedName;

import org.fieldsight.naxa.forms.data.local.FormSchedule;
import org.fieldsight.naxa.stages.data.SubStage;
import org.json.JSONObject;
import org.odk.collect.android.logic.FormDetails;

import java.util.ArrayList;

@Entity(tableName = "fieldsight_forms")
public class FieldSightFormDetails extends FormDetails {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String fieldSightFormId;

    @SerializedName("site")
    private String formDeployedSiteId;

    @SerializedName("PROJECT")
    private String formDeployedProjectId;

    @SerializedName("site_project_id")
    private Integer projectId;

    @SerializedName("name")
    private String odkFormName;

    @SerializedName("descriptionText")
    private String formDescriptionText;


    @SerializedName("version")
    private String odkFormVersion;


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

    private int totalFormsInProject;


    public FieldSightFormDetails(Integer projectId, String formName, String downloadUrl, String manifestUrl, String formID, String formVersion, String hash, String manifestFileHash, boolean isNewerFormVersionAvailable, boolean areNewerMediaFilesAvailable) {
        super(formName, downloadUrl, manifestUrl, formID, formVersion, hash, manifestFileHash, isNewerFormVersionAvailable, areNewerMediaFilesAvailable);
        this.projectId = projectId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setTotalFormsInProject(int totalFormsInProject) {
        this.totalFormsInProject = totalFormsInProject;
    }

    public int getTotalFormsInProject() {
        return totalFormsInProject;
    }

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


    public String getOdkFormName() {
        return odkFormName;
    }


    public String getFormDescriptionText() {
        return formDescriptionText;
    }


    public String getOdkFormVersion() {
        return odkFormVersion;
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

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public void setOdkFormName(String odkFormName) {
        this.odkFormName = odkFormName;
    }

    void setFormDescriptionText(String formDescriptionText) {
        this.formDescriptionText = formDescriptionText;
    }


    void setOdkFormVersion(String odkFormVersion) {
        this.odkFormVersion = odkFormVersion;
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
        return "FieldSightFormDetails{" +
                ", formName=" + getFormName() +
                ", downloadUrl=" + getDownloadUrl() +
                ", manifestUrl=" + getManifestUrl() +
                '}';
    }
}
