
package org.bcss.collect.naxa.notificationslist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Finstance {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("site_fxf")
    @Expose
    private SiteFxf siteFxf;
    @SerializedName("project_fxf")
    @Expose
    private ProjectFxf projectFxf;
    @SerializedName("form_status")
    @Expose
    private Integer formStatus;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("instance")
    @Expose
    private Integer instance;
    @SerializedName("site")
    @Expose
    private Integer site;
    @SerializedName("project")
    @Expose
    private Integer project;
    @SerializedName("submitted_by")
    @Expose
    private Integer submittedBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SiteFxf getSiteFxf() {
        return siteFxf;
    }

    public void setSiteFxf(SiteFxf siteFxf) {
        this.siteFxf = siteFxf;
    }

    public ProjectFxf getProjectFxf() {
        return projectFxf;
    }

    public void setProjectFxf(ProjectFxf projectFxf) {
        this.projectFxf = projectFxf;
    }

    public Integer getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(Integer formStatus) {
        this.formStatus = formStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getInstance() {
        return instance;
    }

    public void setInstance(Integer instance) {
        this.instance = instance;
    }

    public Integer getSite() {
        return site;
    }

    public void setSite(Integer site) {
        this.site = site;
    }

    public Integer getProject() {
        return project;
    }

    public void setProject(Integer project) {
        this.project = project;
    }

    public Integer getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(Integer submittedBy) {
        this.submittedBy = submittedBy;
    }

}
