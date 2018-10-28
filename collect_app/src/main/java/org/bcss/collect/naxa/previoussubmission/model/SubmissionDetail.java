
package org.bcss.collect.naxa.previoussubmission.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.generalforms.data.FormType;

@Entity(tableName = "submission_detail")
public class SubmissionDetail {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private Integer uid;

    @SerializedName("site")
    @Expose
    private String site;

    @SerializedName("project")
    @Expose
    private String project;

    @SerializedName("site_fxf")
    @Expose
    private String siteFsFormId;

    @SerializedName("project_fxf")
    @Expose
    private String projectFsFormId;

    @SerializedName("date")
    @Expose
    private String submissionDateTime;

    @SerializedName("submitted_by")
    @Expose
    private String submittedBy;

    @Ignore
    @SerializedName("form_type")
    @Expose
    private FormType formType;

    @SerializedName("status_display")
    @Expose
    private String statusDisplay;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getSiteFsFormId() {
        return siteFsFormId;
    }

    public void setSiteFsFormId(String siteFsFormId) {
        this.siteFsFormId = siteFsFormId;
    }

    public String getProjectFsFormId() {
        return projectFsFormId;
    }

    public void setProjectFsFormId(String projectFsFormId) {
        this.projectFsFormId = projectFsFormId;
    }

    public Integer getUid() {
        return uid;
    }

    public String getSubmissionDateTime() {
        return submissionDateTime;
    }

    public void setSubmissionDateTime(String submissionDateTime) {
        this.submissionDateTime = submissionDateTime;
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

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissionDetail that = (SubmissionDetail) o;
        return
                Objects.equal(site, that.site) &&
                Objects.equal(project, that.project) &&
                Objects.equal(siteFsFormId, that.siteFsFormId) &&
                Objects.equal(projectFsFormId, that.projectFsFormId) &&
                Objects.equal(submissionDateTime, that.submissionDateTime) &&
                Objects.equal(submittedBy, that.submittedBy) &&
                Objects.equal(formType, that.formType) &&
                Objects.equal(statusDisplay, that.statusDisplay);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid, site, project, siteFsFormId, projectFsFormId, submissionDateTime, submittedBy, formType, statusDisplay);
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

}
