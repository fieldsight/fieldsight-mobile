package org.fieldsight.naxa.data;

import org.odk.collect.android.utilities.DateTimeUtils;

public class FieldSightNotificationBuilder {
    private int id;
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
    private String detailsUrl;
    private String comment;
    private String formType;
    private boolean isRead;
    private boolean isDeployedFromSite;
    private String formSubmissionId;
    private String formVersion;
    private String siteIdentifier;
    private String scheduleFormsCount;
    private String receivedDateTime;
    private long receivedDateTimeInMillis;

    public FieldSightNotificationBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public FieldSightNotificationBuilder setNotificationType(String notificationType) {
        this.notificationType = notificationType;
        return this;
    }

    public FieldSightNotificationBuilder setSheduleFormsCount(String scheduleFormsCount) {
        this.scheduleFormsCount = scheduleFormsCount;
        return this;
    }


    public FieldSightNotificationBuilder setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
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

    public FieldSightNotificationBuilder setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
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

    public FieldSightNotificationBuilder isRead(boolean isRead) {
        this.isRead = isRead;
        return this;
    }

    public FieldSightNotificationBuilder isDeployedFromSite(boolean isDeployedFromSite) {
        this.isDeployedFromSite = isDeployedFromSite;
        return this;
    }

    public FieldSightNotificationBuilder setFormVersion(String formVersion) {
        this.formVersion = formVersion;
        return this;
    }

    public FieldSightNotification createFieldSightNotification() {
        return new FieldSightNotification(id, notificationType, notifiedDate, notifiedTime,
                idString, fsFormId, fsFormIdProject, formName, siteId, siteName, projectId,
                projectName, formStatus, role, isFormDeployed, detailsUrl, comment,
                formType, isRead, formSubmissionId, formVersion, siteIdentifier, isDeployedFromSite, scheduleFormsCount,receivedDateTime, receivedDateTimeInMillis);
    }

    public FieldSightNotificationBuilder setSiteIdentifier(String siteIdentifier) {
        this.siteIdentifier = siteIdentifier;
        return this;
    }

    public FieldSightNotificationBuilder setReceivedDateTime(String receivedDateTime) {
        this.receivedDateTime = receivedDateTime;
        return this;
    }

    public long getReceivedDateTimeInMillis() {
        return receivedDateTimeInMillis;
    }

    public FieldSightNotificationBuilder setReceivedDateTimeInMillis() {
        this.receivedDateTimeInMillis = DateTimeUtils.tsToSec8601(this.receivedDateTime);
        return this;
    }
}