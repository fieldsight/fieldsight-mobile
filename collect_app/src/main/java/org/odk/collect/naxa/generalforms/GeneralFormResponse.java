package org.odk.collect.naxa.generalforms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.android.naxa.project.Em;

import java.util.List;

public class GeneralFormResponse {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("em")
    @Expose
    private Em em;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id_string")
    @Expose
    private String idString;
    @SerializedName("responses_count")
    @Expose
    private Integer responsesCount;
    @SerializedName("is_staged")
    @Expose
    private Boolean isStaged;
    @SerializedName("is_scheduled")
    @Expose
    private Boolean isScheduled;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;
    @SerializedName("date_modified")
    @Expose
    private String dateModified;
    @SerializedName("shared_level")
    @Expose
    private Integer sharedLevel;
    @SerializedName("form_status")
    @Expose
    private Integer formStatus;
    @SerializedName("is_deployed")
    @Expose
    private Boolean isDeployed;
    @SerializedName("is_deleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("is_survey")
    @Expose
    private Boolean isSurvey;
    @SerializedName("from_project")
    @Expose
    private Boolean fromProject;
    @SerializedName("default_submission_status")
    @Expose
    private Integer defaultSubmissionStatus;
    @SerializedName("xf")
    @Expose
    private Integer xf;

    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("project")
    @Expose
    private String project;

    @SerializedName("schedule")
    @Expose
    private Object schedule;

    @SerializedName("downloadUrl")
    private String downloadUrl;

    @SerializedName("manifestUrl")
    private String manifestUrl;


    @SerializedName("stage")
    @Expose
    private Object stage;
    @SerializedName("fsform")
    @Expose
    private String fsform;

    //    @SerializedName("latest_submission")
    @Expose
    private List<FormResponse> latestSubmission = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Em getEm() {
        return em;
    }

    public void setEm(Em em) {
        this.em = em;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public Integer getResponsesCount() {
        return responsesCount;
    }

    public void setResponsesCount(Integer responsesCount) {
        this.responsesCount = responsesCount;
    }

    public Boolean getIsStaged() {
        return isStaged;
    }

    public void setIsStaged(Boolean isStaged) {
        this.isStaged = isStaged;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public Integer getSharedLevel() {
        return sharedLevel;
    }

    public void setSharedLevel(Integer sharedLevel) {
        this.sharedLevel = sharedLevel;
    }

    public Integer getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(Integer formStatus) {
        this.formStatus = formStatus;
    }

    public Boolean getDeployed() {
        return isDeployed;
    }

    public void setIsDeployed(Boolean isDeployed) {
        this.isDeployed = isDeployed;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getIsSurvey() {
        return isSurvey;
    }

    public void setIsSurvey(Boolean isSurvey) {
        this.isSurvey = isSurvey;
    }

    public Boolean getFromProject() {
        return fromProject;
    }

    public void setFromProject(Boolean fromProject) {
        this.fromProject = fromProject;
    }

    public Integer getDefaultSubmissionStatus() {
        return defaultSubmissionStatus;
    }

    public void setDefaultSubmissionStatus(Integer defaultSubmissionStatus) {
        this.defaultSubmissionStatus = defaultSubmissionStatus;
    }

    public List<FormResponse> getLatestSubmission() {
        return latestSubmission;
    }

    public void setLatestSubmission(List<FormResponse> latestSubmission) {
        this.latestSubmission = latestSubmission;
    }

    public Integer getXf() {
        return xf;
    }

    public void setXf(Integer xf) {
        this.xf = xf;
    }

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

    public Object getSchedule() {
        return schedule;
    }

    public void setSchedule(Object schedule) {
        this.schedule = schedule;
    }

    public Object getStage() {
        return stage;
    }

    public void setStage(Object stage) {
        this.stage = stage;
    }

    public String getFsform() {
        return fsform;
    }

    public void setFsform(String fsform) {
        this.fsform = fsform;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getManifestUrl() {
        return manifestUrl;
    }

    public void setManifestUrl(String manifestUrl) {
        this.manifestUrl = manifestUrl;
    }
}