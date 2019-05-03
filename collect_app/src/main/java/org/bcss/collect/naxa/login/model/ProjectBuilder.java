package org.bcss.collect.naxa.login.model;

import android.os.Parcel;

import java.util.List;

public class ProjectBuilder {
    private String id;
    private String name;
    private String description;
    private String address;
    private String lat;
    private String lon;
    private String siteClusters;
    private String organizationName;
    private String organizationlogourl;
    private Boolean hasClusteredSites;
    private Integer typeId;
    private String typeLabel;
    private String phone;
    private boolean isSyncedWithRemote;
    private List<SiteMetaAttribute> metaAttributes;
    private Parcel in;

    public ProjectBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ProjectBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ProjectBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ProjectBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public ProjectBuilder setLat(String lat) {
        this.lat = lat;
        return this;
    }

    public ProjectBuilder setLon(String lon) {
        this.lon = lon;
        return this;
    }

    public ProjectBuilder setSiteClusters(String siteClusters) {
        this.siteClusters = siteClusters;
        return this;
    }

    public ProjectBuilder setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
        return this;
    }

    public ProjectBuilder setOrganizationlogourl(String organizationlogourl) {
        this.organizationlogourl = organizationlogourl;
        return this;
    }

    public ProjectBuilder setHasClusteredSites(Boolean hasClusteredSites) {
        this.hasClusteredSites = hasClusteredSites;
        return this;
    }

    public ProjectBuilder setTypeId(Integer typeId) {
        this.typeId = typeId;
        return this;
    }

    public ProjectBuilder setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
        return this;
    }

    public ProjectBuilder setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public ProjectBuilder setIsSyncedWithRemote(boolean isSyncedWithRemote) {
        this.isSyncedWithRemote = isSyncedWithRemote;
        return this;
    }

    public ProjectBuilder setMetaAttributes(List<SiteMetaAttribute> metaAttributes) {
        this.metaAttributes = metaAttributes;
        return this;
    }

    public ProjectBuilder setIn(Parcel in) {
        this.in = in;
        return this;
    }

    public Project createProject() {
        return new Project(id, name, description, address, lat, lon, siteClusters, organizationName, organizationlogourl, hasClusteredSites, typeId, typeLabel, phone, isSyncedWithRemote, metaAttributes);
    }
}