
package org.fieldsight.naxa.v3;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormState {

    @SerializedName("pk")
    @Expose
    private String fsFormId;
    @SerializedName("project_fxf")
    @Expose
    private String projectFxf;
    @SerializedName("site_fxf")
    @Expose
    private String siteFxf;
    @SerializedName("project")
    @Expose
    private String project;
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("form_status")
    @Expose
    private String formStatus;
    @SerializedName("form_name")
    @Expose
    private String formName;
    @SerializedName("site_name")
    @Expose
    private String siteName;
    @SerializedName("project_name")
    @Expose
    private String projectName;
    @SerializedName("status_display")
    @Expose
    private String statusDisplay;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("id_string")
    @Expose
    private String idString;

    public String getFsFormId() {
        return fsFormId;
    }

    public void setFsFormId(String fsFormId) {
        this.fsFormId = fsFormId;
    }

    public String getProjectFxf() {
        return projectFxf;
    }

    public void setProjectFxf(String projectFxf) {
        this.projectFxf = projectFxf;
    }

    public String getSiteFxf() {
        return siteFxf;
    }

    public void setSiteFxf(String siteFxf) {
        this.siteFxf = siteFxf;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(String formStatus) {
        this.formStatus = formStatus;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

}
