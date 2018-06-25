package org.odk.collect.naxa.database.project;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "project_table")
public class ProjectModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "latitude")
    private String latitude;

    @ColumnInfo(name = "longitude")
    private String longitude;

    @ColumnInfo(name = "organizationName")
    private String organizationName;

    @ColumnInfo(name = "organizationLogoUrl")
    private String organizationLogoUrl;

    @ColumnInfo(name = "hasClusteredSites")
    private Boolean hasClusteredSites;

    @ColumnInfo(name = "typeId")
    private Integer typeId;

    @ColumnInfo(name = "typeLabel")
    private String typeLabel;

    @ColumnInfo(name = "phone")
    private String phone;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationLogoUrl() {
        return organizationLogoUrl;
    }

    public void setOrganizationLogoUrl(String organizationLogoUrl) {
        this.organizationLogoUrl = organizationLogoUrl;
    }

    public Boolean getHasClusteredSites() {
        return hasClusteredSites;
    }

    public void setHasClusteredSites(Boolean hasClusteredSites) {
        this.hasClusteredSites = hasClusteredSites;
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
}
