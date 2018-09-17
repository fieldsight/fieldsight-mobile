
package org.bcss.collect.naxa.previoussubmission;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("submission_data")
    @Expose
    private List<SubmissionDatum> submissionData = null;
    @SerializedName("site")
    @Expose
    private Integer site;
    @SerializedName("project")
    @Expose
    private Integer project;
    @SerializedName("site_fxf")
    @Expose
    private Integer siteFxf;
    @SerializedName("project_fxf")
    @Expose
    private Integer projectFxf;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("submitted_by")
    @Expose
    private String submittedBy;
    @SerializedName("form_type")
    @Expose
    private FormType formType;

    public List<SubmissionDatum> getSubmissionData() {
        return submissionData;
    }

    public void setSubmissionData(List<SubmissionDatum> submissionData) {
        this.submissionData = submissionData;
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

    public Integer getSiteFxf() {
        return siteFxf;
    }

    public void setSiteFxf(Integer siteFxf) {
        this.siteFxf = siteFxf;
    }

    public Integer getProjectFxf() {
        return projectFxf;
    }

    public void setProjectFxf(Integer projectFxf) {
        this.projectFxf = projectFxf;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public FormType getFormType() {
        return formType;
    }

    public void setFormType(FormType formType) {
        this.formType = formType;
    }

}
