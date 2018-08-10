package org.bcss.collect.naxa.data;

public class FieldSightNotificationBuilder {
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

    public FieldSightNotificationBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public FieldSightNotificationBuilder setNotificationType(String notificationType) {
        this.notificationType = notificationType;
        return this;
    }

    public FieldSightNotificationBuilder setNotifiedDate(String notifiedDate) {
        this.notifiedDate = notifiedDate;
        return this;
    }

    public FieldSightNotificationBuilder setNotifiedTime(String notifiedTime) {
        this.notifiedTime = notifiedTime;
        return this;
    }

    public FieldSightNotificationBuilder setIdString(String idString) {
        this.idString = idString;
        return this;
    }

    public FieldSightNotificationBuilder setFsFormId(String fsFormId) {
        this.fsFormId = fsFormId;
        return this;
    }

    public FieldSightNotificationBuilder setFsFormIdProject(String fsFormIdProject) {
        this.fsFormIdProject = fsFormIdProject;
        return this;
    }

    public FieldSightNotificationBuilder setFormName(String formName) {
        this.formName = formName;
        return this;
    }

    public FieldSightNotificationBuilder setSiteId(String siteId) {
        this.siteId = siteId;
        return this;
    }

    public FieldSightNotificationBuilder setSiteName(String siteName) {
        this.siteName = siteName;
        return this;
    }

    public FieldSightNotificationBuilder setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public FieldSightNotificationBuilder setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public FieldSightNotificationBuilder setFormStatus(String formStatus) {
        this.formStatus = formStatus;
        return this;
    }

    public FieldSightNotificationBuilder setRole(String role) {
        this.role = role;
        return this;
    }

    public FieldSightNotificationBuilder setIsFormDeployed(String isFormDeployed) {
        this.isFormDeployed = isFormDeployed;
        return this;
    }

    public FieldSightNotificationBuilder setDetails_url(String details_url) {
        this.details_url = details_url;
        return this;
    }

    public FieldSightNotificationBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public FieldSightNotificationBuilder setFormType(String formType) {
        this.formType = formType;
        return this;
    }

    public FieldSightNotification createFieldSightNotification() {
        return new FieldSightNotification(id, notificationType, notifiedDate, notifiedTime, idString, fsFormId, fsFormIdProject, formName, siteId, siteName, projectId, projectName, formStatus, role, isFormDeployed, details_url, comment, formType);
    }
}