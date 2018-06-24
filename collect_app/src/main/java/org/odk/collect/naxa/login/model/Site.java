package org.odk.collect.naxa.login.model;


import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Site implements Serializable {


    private String lSiteDesc;
    private String lLongitude;
    private String progress;
    private String projectName;
    private String projectId;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    private String add_desc;
    private String bluePrintString;
    private int isSiteVerified;
    private boolean isSiteSelected;


    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("lat")
    @Expose
    private String latitude;
    @SerializedName("lon")
    @Expose
    private String longitude;
    @SerializedName("identifier")
    @Expose
    private String identifier;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type_id")
    @Expose
    private Integer typeId;
    @SerializedName("type_label")
    @Expose
    private String typeLabel;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("public_desc")
    @Expose
    private String publicDesc;
    @SerializedName("additional_desc")
    @Expose
    private Object additionalDesc;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("is_survey")
    @Expose
    private Boolean isSurvey;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;
    @SerializedName("project")
    @Expose
    private Integer project;

    @SerializedName("non_field_errors")
    @Expose
    private String nonFieldError;

    @SerializedName("type")
    @Expose
    private String siteTypeError;

    @NonNull
    public String getNonFieldError() {
        return nonFieldError;
    }

    @NonNull
    public String getSiteTypeError() {
        return siteTypeError;
    }


    public boolean isSiteSelected() {
        return isSiteSelected;
    }

    public void setSiteSelected(boolean siteSelected) {
        isSiteSelected = siteSelected;
    }

    @SerializedName("blueprints")
    private ArrayList<String> bluePrintArrayList = new ArrayList<String>();

    public int getIsSiteVerified() {
        return isSiteVerified;
    }

    public void setIsSiteVerified(int isSiteVerified) {
        this.isSiteVerified = isSiteVerified;
    }


    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getlSiteDesc() {
        return lSiteDesc;
    }

    public void setlSiteDesc(String lSiteDesc) {
        this.lSiteDesc = lSiteDesc;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAdd_desc() {
        return add_desc;
    }

    public void setAdd_desc(String add_desc) {
        this.add_desc = add_desc;
    }

    public List<String> getBluePrintArrayList() {
        return bluePrintArrayList;
    }

    public void setBluePrintArrayList(ArrayList<String> bluePrintArrayList) {
        this.bluePrintArrayList = bluePrintArrayList;
    }

    public String getBluePrintString() {
        return bluePrintString;
    }

    public void setBluePrintString(String bluePrintString) {
        this.bluePrintString = bluePrintString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getPublicDesc() {
        return publicDesc;
    }

    public void setPublicDesc(String publicDesc) {
        this.publicDesc = publicDesc;
    }

    public Object getAdditionalDesc() {
        return additionalDesc;
    }

    public void setAdditionalDesc(Object additionalDesc) {
        this.additionalDesc = additionalDesc;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getIsSurvey() {
        return isSurvey;
    }

    public void setIsSurvey(Boolean isSurvey) {
        this.isSurvey = isSurvey;
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
}
