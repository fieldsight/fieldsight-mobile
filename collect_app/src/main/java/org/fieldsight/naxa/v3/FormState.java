package org.fieldsight.naxa.v3;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormState {

    @SerializedName("pk")
    @Expose
    private Integer pk;
    @SerializedName("project_fxf")
    @Expose
    private Integer projectFxf;
    @SerializedName("site_fxf")
    @Expose
    private Integer siteFxf;
    @SerializedName("project")
    @Expose
    private Integer project;
    @SerializedName("site")
    @Expose
    private Integer site;
    @SerializedName("form_status")
    @Expose
    private Integer formStatus;
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

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public Integer getProjectFxf() {
        return projectFxf;
    }

    public void setProjectFxf(Integer projectFxf) {
        this.projectFxf = projectFxf;
    }

    public Integer getSiteFxf() {
        return siteFxf;
    }

    public void setSiteFxf(Integer siteFxf) {
        this.siteFxf = siteFxf;
    }

    public Integer getProject() {
        return project;
    }

    public void setProject(Integer project) {
        this.project = project;
    }

    public Integer getSite() {
        return site;
    }

    public void setSite(Integer site) {
        this.site = site;
    }

    public Integer getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(Integer formStatus) {
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

}