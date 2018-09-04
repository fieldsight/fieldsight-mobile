package org.bcss.collect.naxa.login.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.common.Constant;

@Entity(tableName = "sites")
public class Site implements Parcelable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @SerializedName("lat")
    @ColumnInfo(name = "latitude")
    private String latitude;

    @SerializedName("lon")
    @ColumnInfo(name = "longitude")
    private String longitude;

    @SerializedName("identifier")
    @ColumnInfo(name = "identifier")
    private String identifier;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("type_id")
    @ColumnInfo(name = "typeId")
    private String typeId;

    @SerializedName("type_label")
    @ColumnInfo(name = "typeLabel")
    private String typeLabel;

    @SerializedName("phone")
    @ColumnInfo(name = "phone")
    private String phone;

    @SerializedName("address")
    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "publicDesc")
    private String publicDesc;

    @SerializedName("add_desc")
    @ColumnInfo(name = "additionalDesc")
    private String additionalDesc;

    @ColumnInfo(name = "logo")
    private String logo;

    @ColumnInfo(name = "isActive")
    private Boolean isActive;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "isSurvey")
    private Boolean isSurvey;

    @ColumnInfo(name = "dateCreated")
    private String dateCreated;

    @ColumnInfo(name = "project")
    private String project;

    @ColumnInfo(name = "isSiteVerified")
    private int isSiteVerified;

    @SerializedName("type")
    @Expose
    private String siteTypeError;


    private String metaAttributes;
    private String region;

    //default values for  table
    private String generalFormDeployedFrom = Constant.FormDeploymentFrom.PROJECT;
    private String stagedFormDeployedFrom = Constant.FormDeploymentFrom.PROJECT;
    private String scheduleFormDeployedForm = Constant.FormDeploymentFrom.PROJECT;


    public Site() {

    }

    @Ignore
    public Site(@NonNull String id, String latitude, String longitude, String identifier, String name, String typeId, String typeLabel, String phone, String address, String publicDesc, String additionalDesc, String logo, Boolean isActive, String location, Boolean isSurvey, String dateCreated, String project, int isSiteVerified, String siteTypeError, String metaAttributes, String region, String generalFormDeployedFrom, String stagedFormDeployedFrom, String scheduleFormDeployedForm) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.identifier = identifier;
        this.name = name;
        this.typeId = typeId;
        this.typeLabel = typeLabel;
        this.phone = phone;
        this.address = address;
        this.publicDesc = publicDesc;
        this.additionalDesc = additionalDesc;
        this.logo = logo;
        this.isActive = isActive;
        this.location = location;
        this.isSurvey = isSurvey;
        this.dateCreated = dateCreated;
        this.project = project;
        this.isSiteVerified = isSiteVerified;
        this.siteTypeError = siteTypeError;
        this.metaAttributes = metaAttributes;
        this.region = region;
        this.generalFormDeployedFrom = generalFormDeployedFrom;
        this.stagedFormDeployedFrom = stagedFormDeployedFrom;
        this.scheduleFormDeployedForm = scheduleFormDeployedForm;
    }

    public String getMetaAttributes() {
        return metaAttributes == null ? "" : metaAttributes;
    }

    public void setMetaAttributes(String metaAttributes) {
        this.metaAttributes = metaAttributes;
    }


    public void setSiteTypeError(String siteTypeError) {
        this.siteTypeError = siteTypeError;
    }

    public String getGeneralFormDeployedFrom() {
        return generalFormDeployedFrom;
    }

    public void setGeneralFormDeployedFrom(String generalFormDeployedFrom) {
        this.generalFormDeployedFrom = generalFormDeployedFrom;
    }

    public String getStagedFormDeployedFrom() {
        return stagedFormDeployedFrom;
    }

    public void setStagedFormDeployedFrom(String stagedFormDeployedFrom) {
        this.stagedFormDeployedFrom = stagedFormDeployedFrom;
    }

    public String getScheduleFormDeployedForm() {
        return scheduleFormDeployedForm;
    }

    public void setScheduleFormDeployedForm(String scheduleFormDeployedForm) {
        this.scheduleFormDeployedForm = scheduleFormDeployedForm;
    }


    public int getIsSiteVerified() {
        return isSiteVerified;
    }

    public void setIsSiteVerified(int isSiteVerified) {
        this.isSiteVerified = isSiteVerified;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude == null ? "" : latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude == null ? "" : longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIdentifier() {
        return identifier == null ? "" : identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeId() {
        return typeId == null ? "" : typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }


    public String getRegion() {
        return region == null ? "" : region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }

    public String getPhone() {
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address == null ? "" : address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPublicDesc() {
        return publicDesc == null ? "" : publicDesc;
    }

    public void setPublicDesc(String publicDesc) {
        this.publicDesc = publicDesc;
    }

    public String getAdditionalDesc() {
        return additionalDesc;
    }

    public void setAdditionalDesc(String additionalDesc) {
        this.additionalDesc = additionalDesc;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getSurvey() {
        return isSurvey;
    }

    public void setSurvey(Boolean survey) {
        isSurvey = survey;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getProject() {
        return project == null ? "" : project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @NonNull
    public String getSiteTypeError() {
        return siteTypeError == null ? "" : siteTypeError;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.identifier);
        dest.writeString(this.name);
        dest.writeString(this.typeId);
        dest.writeString(this.typeLabel);
        dest.writeString(this.phone);
        dest.writeString(this.address);
        dest.writeString(this.publicDesc);
        dest.writeString(this.additionalDesc);
        dest.writeString(this.logo);
        dest.writeValue(this.isActive);
        dest.writeString(this.location);
        dest.writeValue(this.isSurvey);
        dest.writeString(this.dateCreated);
        dest.writeString(this.project);
        dest.writeInt(this.isSiteVerified);
        dest.writeString(this.siteTypeError);
        dest.writeString(this.metaAttributes);
        dest.writeString(this.region);
        dest.writeString(this.generalFormDeployedFrom);
        dest.writeString(this.stagedFormDeployedFrom);
        dest.writeString(this.scheduleFormDeployedForm);
    }

    protected Site(Parcel in) {
        this.id = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.identifier = in.readString();
        this.name = in.readString();
        this.typeId = in.readString();
        this.typeLabel = in.readString();
        this.phone = in.readString();
        this.address = in.readString();
        this.publicDesc = in.readString();
        this.additionalDesc = in.readString();
        this.logo = in.readString();
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.location = in.readString();
        this.isSurvey = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.dateCreated = in.readString();
        this.project = in.readString();
        this.isSiteVerified = in.readInt();
        this.siteTypeError = in.readString();
        this.metaAttributes = in.readString();
        this.region = in.readString();
        this.generalFormDeployedFrom = in.readString();
        this.stagedFormDeployedFrom = in.readString();
        this.scheduleFormDeployedForm = in.readString();
    }

    public static final Creator<Site> CREATOR = new Creator<Site>() {
        @Override
        public Site createFromParcel(Parcel source) {
            return new Site(source);
        }

        @Override
        public Site[] newArray(int size) {
            return new Site[size];
        }
    };
}

