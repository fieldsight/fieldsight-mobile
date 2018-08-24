package org.bcss.collect.naxa.login.model;

import android.os.Parcel;

public class SiteBuilder {
    private String id;
    private String latitude;
    private String longitude;
    private String identifier;
    private String name;
    private String typeId;
    private String typeLabel;
    private String phone;
    private String address;
    private String publicDesc;
    private String additionalDesc;
    private String logo;
    private Boolean isActive;
    private String location;
    private Boolean isSurvey;
    private String dateCreated;
    private String project;
    private int isSiteVerified;
    private String siteTypeError;
    private String metaAttributes;
    private String region;
    private String generalFormDeployedFrom;
    private String stagedFormDeployedFrom;
    private String scheduleFormDeployedForm;
    private Parcel in;

    public SiteBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public SiteBuilder setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public SiteBuilder setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public SiteBuilder setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public SiteBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public SiteBuilder setTypeId(String typeId) {
        this.typeId = typeId;
        return this;
    }

    public SiteBuilder setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
        return this;
    }

    public SiteBuilder setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public SiteBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public SiteBuilder setPublicDesc(String publicDesc) {
        this.publicDesc = publicDesc;
        return this;
    }

    public SiteBuilder setAdditionalDesc(String additionalDesc) {
        this.additionalDesc = additionalDesc;
        return this;
    }

    public SiteBuilder setLogo(String logo) {
        this.logo = logo;
        return this;
    }

    public SiteBuilder setIsActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public SiteBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public SiteBuilder setIsSurvey(Boolean isSurvey) {
        this.isSurvey = isSurvey;
        return this;
    }

    public SiteBuilder setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public SiteBuilder setProject(String project) {
        this.project = project;
        return this;
    }

    public SiteBuilder setIsSiteVerified(int isSiteVerified) {
        this.isSiteVerified = isSiteVerified;
        return this;
    }

    public SiteBuilder setSiteTypeError(String siteTypeError) {
        this.siteTypeError = siteTypeError;
        return this;
    }

    public SiteBuilder setMetaAttributes(String metaAttributes) {
        this.metaAttributes = metaAttributes;
        return this;
    }

    public SiteBuilder setRegion(String region) {
        this.region = region;
        return this;
    }

    public SiteBuilder setGeneralFormDeployedFrom(String generalFormDeployedFrom) {
        this.generalFormDeployedFrom = generalFormDeployedFrom;
        return this;
    }

    public SiteBuilder setStagedFormDeployedFrom(String stagedFormDeployedFrom) {
        this.stagedFormDeployedFrom = stagedFormDeployedFrom;
        return this;
    }

    public SiteBuilder setScheduleFormDeployedForm(String scheduleFormDeployedForm) {
        this.scheduleFormDeployedForm = scheduleFormDeployedForm;
        return this;
    }

    public SiteBuilder setIn(Parcel in) {
        this.in = in;
        return this;
    }

    public Site createSite() {
        return new Site(id, latitude, longitude, identifier, name, typeId, typeLabel, phone, address, publicDesc, additionalDesc, logo, isActive, location, isSurvey, dateCreated, project, isSiteVerified, siteTypeError, metaAttributes, region, generalFormDeployedFrom, stagedFormDeployedFrom, scheduleFormDeployedForm);
    }
}