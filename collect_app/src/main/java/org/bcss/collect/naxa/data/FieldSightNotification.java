package org.bcss.collect.naxa.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity
public class FieldSightNotification implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
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
    private String details_url;
    private String comment;
    private String formType;
    private boolean isRead;
    private String formSubmissionId;
    private String formVersion;

    public String getFormSubmissionId() {
        return formSubmissionId;
    }

    public void setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }

    public FieldSightNotification(@NonNull int id, String notificationType, String notifiedDate, String notifiedTime, String idString,
                                  String fsFormId, String fsFormIdProject, String formName, String siteId, String siteName, String projectId,
                                  String projectName, String formStatus, String role, String isFormDeployed, String details_url, String comment,
                                  String formType, boolean isRead, String formSubmissionId, String formVersion) {
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
        this.isRead = isRead;
        this.formVersion = formVersion;
        this.formSubmissionId = formSubmissionId;
    }

    public String getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(String formVerion) {
        this.formVersion = formVerion;
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
    public int getId() {
        return id;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setId(@NonNull int id) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.notificationType);
        dest.writeString(this.notifiedDate);
        dest.writeString(this.notifiedTime);
        dest.writeString(this.idString);
        dest.writeString(this.fsFormId);
        dest.writeString(this.fsFormIdProject);
        dest.writeString(this.formName);
        dest.writeString(this.siteId);
        dest.writeString(this.siteName);
        dest.writeString(this.projectId);
        dest.writeString(this.projectName);
        dest.writeString(this.formStatus);
        dest.writeString(this.role);
        dest.writeString(this.isFormDeployed);
        dest.writeString(this.details_url);
        dest.writeString(this.comment);
        dest.writeString(this.formType);
        dest.writeString(this.formSubmissionId);
        dest.writeString(this.formVersion);
        dest.writeByte(this.isRead ? (byte) 1 : (byte) 0);
    }

    protected FieldSightNotification(Parcel in) {
        this.id = in.readInt();
        this.notificationType = in.readString();
        this.notifiedDate = in.readString();
        this.notifiedTime = in.readString();
        this.idString = in.readString();
        this.fsFormId = in.readString();
        this.fsFormIdProject = in.readString();
        this.formName = in.readString();
        this.siteId = in.readString();
        this.siteName = in.readString();
        this.projectId = in.readString();
        this.projectName = in.readString();
        this.formStatus = in.readString();
        this.role = in.readString();
        this.isFormDeployed = in.readString();
        this.details_url = in.readString();
        this.comment = in.readString();
        this.formType = in.readString();
        this.formSubmissionId = in.readString();
        this.formVersion = in.readString();
        this.isRead = in.readByte() != 0;
    }

    public static final Creator<FieldSightNotification> CREATOR = new Creator<FieldSightNotification>() {
        @Override
        public FieldSightNotification createFromParcel(Parcel source) {
            return new FieldSightNotification(source);
        }

        @Override
        public FieldSightNotification[] newArray(int size) {
            return new FieldSightNotification[size];
        }
    };
}
