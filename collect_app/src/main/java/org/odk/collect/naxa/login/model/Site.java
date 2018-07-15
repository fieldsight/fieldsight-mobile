package org.odk.collect.naxa.login.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "site")
public class Site implements Parcelable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;
    @ColumnInfo(name = "latitude")
    private String latitude;
    @ColumnInfo(name = "longitude")
    private String longitude;
    @ColumnInfo(name = "identifier")
    private String identifier;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "typeId")
    private Integer typeId;
    @ColumnInfo(name = "typeLabel")
    private String typeLabel;
    @ColumnInfo(name = "phone")
    private String phone;
    @ColumnInfo(name = "address")
    private String address;
    @ColumnInfo(name = "publicDesc")
    private String publicDesc;
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
    private Integer project;

    private int isSiteVerified;

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
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPublicDesc() {
        return publicDesc;
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

    public Integer getProject() {
        return project;
    }

    public void setProject(Integer project) {
        this.project = project;
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
        dest.writeValue(this.typeId);
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
        dest.writeValue(this.project);
        dest.writeInt(this.isSiteVerified);
    }

    public Site() {
    }

    protected Site(Parcel in) {
        this.id = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.identifier = in.readString();
        this.name = in.readString();
        this.typeId = (Integer) in.readValue(Integer.class.getClassLoader());
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
        this.project = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isSiteVerified = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Site> CREATOR = new Parcelable.Creator<Site>() {
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

