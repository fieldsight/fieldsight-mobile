package org.bcss.collect.naxa.login.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.v3.network.Region;
import org.bcss.collect.naxa.v3.network.RegionConverter;

import java.util.List;


/**
 * Created by Susan on 11/24/2016.
 */
@Entity(tableName = "project")
public class Project implements Parcelable {

    @PrimaryKey
    @NonNull
    private String id;

    @Expose
    private String name;

    @Expose
    private String description;

    @Expose
    private String address;

    @Expose
    private String lat;

    @Expose
    private String lon;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @SerializedName("url")
    public String url;

    @Expose
    private String siteClusters;

    @SerializedName("organization_name")
    private String organizationName;

    @SerializedName("organization_url")
    private String organizationlogourl;

    @SerializedName("has_site_role")
    private Boolean hasClusteredSites;

    @Expose
    private Integer typeId;

    @Expose
    private String typeLabel;

    @Expose
    private String phone;

    private boolean isSyncedWithRemote;

    @Expose
    @Ignore
    boolean checked = false;

    @Expose
    @Ignore
    boolean isSynced = false;

    public void setSynced(boolean isSynced) {
        this.isSynced = isSynced;
    }

    public boolean isSynced() {
        return this.isSynced;
    }

    public List<Region> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<Region> regionList) {
        this.regionList = regionList;
    }

    @SerializedName("project_region")
    @TypeConverters(RegionConverter.class)
    List<Region> regionList;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @SerializedName("site_meta_attributes")
    private List<SiteMetaAttribute> siteMetaAttributes ;

    public Project() {

    }

    @Ignore
    public Project(@NonNull String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Boolean getHasClusteredSites() {
        return hasClusteredSites;
    }

    public void setHasClusteredSites(Boolean hasClusteredSites) {
        this.hasClusteredSites = hasClusteredSites;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationlogourl() {
        return organizationlogourl;
    }

    public void setOrganizationlogourl(String organizationlogourl) {
        this.organizationlogourl = organizationlogourl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSiteMetaAttributes(List<SiteMetaAttribute> siteMetaAttributes) {
        this.siteMetaAttributes = siteMetaAttributes;
    }

    public List<SiteMetaAttribute>  getSiteMetaAttributes() {
        return siteMetaAttributes;
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
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

    public String getSiteClusters() {
        return siteClusters;
    }

    public void setSiteClusters(String siteClusters) {
        this.siteClusters = siteClusters;
    }

    public boolean isSyncedWithRemote() {
        return isSyncedWithRemote;
    }

    public void setSyncedWithRemote(boolean syncedWithRemote) {
        isSyncedWithRemote = syncedWithRemote;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.address);
        dest.writeString(this.lat);
        dest.writeString(this.lon);
        dest.writeString(this.siteClusters);
        dest.writeString(this.organizationName);
        dest.writeString(this.organizationlogourl);
        dest.writeValue(this.hasClusteredSites);
        dest.writeValue(this.typeId);
        dest.writeString(this.typeLabel);
        dest.writeString(this.phone);
        dest.writeByte(this.isSyncedWithRemote ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.siteMetaAttributes);
        dest.writeTypedList(this.regionList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return isSyncedWithRemote == project.isSyncedWithRemote &&
                Objects.equal(id, project.id) &&
                Objects.equal(name, project.name) &&
                Objects.equal(description, project.description) &&
                Objects.equal(address, project.address) &&
                Objects.equal(lat, project.lat) &&
                Objects.equal(lon, project.lon) &&
                Objects.equal(siteClusters, project.siteClusters) &&
                Objects.equal(organizationName, project.organizationName) &&
                Objects.equal(organizationlogourl, project.organizationlogourl) &&
                Objects.equal(hasClusteredSites, project.hasClusteredSites) &&
                Objects.equal(typeId, project.typeId) &&
                Objects.equal(typeLabel, project.typeLabel) &&
                Objects.equal(phone, project.phone) &&
                Objects.equal(siteMetaAttributes, project.siteMetaAttributes) &
                Objects.equal(regionList, project.regionList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, description, address, lat, lon, siteClusters, organizationName, organizationlogourl, hasClusteredSites, typeId, typeLabel, phone, isSyncedWithRemote, siteMetaAttributes);
    }

    protected Project(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.address = in.readString();
        this.lat = in.readString();
        this.lon = in.readString();
        this.siteClusters = in.readString();
        this.organizationName = in.readString();
        this.organizationlogourl = in.readString();
        this.hasClusteredSites = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.typeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.typeLabel = in.readString();
        this.phone = in.readString();
        this.isSyncedWithRemote = in.readByte() != 0;
        this.siteMetaAttributes = in.createTypedArrayList(SiteMetaAttribute.CREATOR);
        this.regionList = in.createTypedArrayList(Region.CREATOR);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel source) {
            return new Project(source);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public Project(@NonNull String id, String name, String description, String address, String lat, String lon, String siteClusters, String organizationName, String organizationlogourl, Boolean hasClusteredSites, Integer typeId, String typeLabel, String phone, boolean isSyncedWithRemote,List<SiteMetaAttribute> metaAttributes, String url) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.siteClusters = siteClusters;
        this.organizationName = organizationName;
        this.organizationlogourl = organizationlogourl;
        this.hasClusteredSites = hasClusteredSites;
        this.typeId = typeId;
        this.typeLabel = typeLabel;
        this.phone = phone;
        this.isSyncedWithRemote = isSyncedWithRemote;
        this.siteMetaAttributes = metaAttributes;
        this.url = url;

    }

}
