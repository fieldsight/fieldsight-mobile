package org.bcss.collect.naxa.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class FieldSightNotification {

    @PrimaryKey
    @NonNull
    private String id;
    private String notificationType;
    private String notifiedDate;
    private String notifiedTime;
    private String idString;
    private String fsFormId;
    private String fsFormIdProject;
    private String formName;
    private String siteId;
    private String siteName;
    private String projectId;
    private String projectName;
    private String formStatus;
    private String role;
    private String isFormDeployed;
    private String details_url;
    private String comment;
    private String formType;




    public FieldSightNotification(@NonNull String id, String notificationType, String notifiedDate, String notifiedTime, String idString, String fsFormId, String fsFormIdProject, String formName, String siteId, String siteName, String projectId, String projectName, String formStatus, String role, String isFormDeployed, String details_url, String comment, String formType) {
        this.id = id;
        this.notificationType = notificationType;
        this.notifiedDate = notifiedDate;
        this.notifiedTime = notifiedTime;
        this.idString = idString;
        this.fsFormId = fsFormId;
        this.fsFormIdProject = fsFormIdProject;
        this.formName = formName;
        this.siteId = siteId;
        this.siteName = siteName;
        this.projectId = projectId;
        this.projectName = projectName;
        this.formStatus = formStatus;
        this.role = role;
        this.isFormDeployed = isFormDeployed;
        this.details_url = details_url;
        this.comment = comment;
        this.formType = formType;
    }

    public String getNotifiedTime() {
        return notifiedTime;
    }

    public void setNotifiedTime(String notifiedTime) {
        this.notifiedTime = notifiedTime;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotifiedDate() {
        return notifiedDate;
    }

    public void setNotifiedDate(String notifiedDate) {
        this.notifiedDate = notifiedDate;
    }

    public String getFsFormId() {
        return fsFormId;
    }

    public void setFsFormId(String fsFormId) {
        this.fsFormId = fsFormId;
    }

    public String getFsFormIdProject() {
        return fsFormIdProject;
    }

    public void setFsFormIdProject(String fsFormIdProject) {
        this.fsFormIdProject = fsFormIdProject;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(String formStatus) {
        this.formStatus = formStatus;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIsFormDeployed() {
        return isFormDeployed;
    }

    public void setIsFormDeployed(String isFormDeployed) {
        this.isFormDeployed = isFormDeployed;
    }

    public String getDetails_url() {
        return details_url;
    }

    public void setDetails_url(String details_url) {
        this.details_url = details_url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
