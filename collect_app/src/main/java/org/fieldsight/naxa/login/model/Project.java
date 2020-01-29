package org.fieldsight.naxa.login.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.fieldsight.naxa.v3.network.Region;
import org.fieldsight.naxa.v3.network.RegionConverter;

import java.util.List;


/**
 * Created by Susan on 11/24/2016.
 */
@SuppressWarnings("PMD")
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

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Expose
    @Ignore
    String statusMessage = "";

    @SerializedName("total_regions")
    @ColumnInfo(name = "total_regions")
    private int totalRegions;

    @SerializedName("total_sites")
    @ColumnInfo(name = "total_sites")
    private int totalSites;

    @SerializedName("total_users")
    @ColumnInfo(name = "total_users")
    private int totalUsers;

    @SerializedName("total_submissions")
    @ColumnInfo(name = "total_submissions")
    private int totalSubmissions;

    public String getTerms_and_labels() {
        return terms_and_labels;
    }

    public void setTerms_and_labels(String terms_and_labels) {
        this.terms_and_labels = terms_and_labels;
    }

    private String terms_and_labels = "";

    public long getSyncedDate() {
        return syncedDate;
    }

    public void setSyncedDate(long syncedDate) {
        this.syncedDate = syncedDate;
    }

    @Expose
    @Ignore
    long syncedDate = 0;

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
    private List<SiteMetaAttribute> siteMetaAttributes;

    public Project() {

    }

    @Ignore
    public Project(@NonNull String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Creator<Project> getCREATOR() {
        return CREATOR;
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

    public int getTotalRegions() {
        return totalRegions;
    }

    public void setTotalRegions(int totalRegions) {
        this.totalRegions = totalRegions;
    }

    public int getTotalSites() {
        return totalSites;
    }

    public void setTotalSites(int totalSites) {
        this.totalSites = totalSites;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(int totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
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

    public List<SiteMetaAttribute> getSiteMetaAttributes() {
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

    @Ignore
    public Project(@NonNull String id, String name, String description, String address, String lat, String lon, String url, String siteClusters, String organizationName, String organizationlogourl, Boolean hasClusteredSites, Integer typeId, String typeLabel, String phone, boolean isSyncedWithRemote, int totalRegions, int totalSites, int totalUsers, int totalSubmissions, String terms_and_labels, List<SiteMetaAttribute> siteMetaAttributes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.url = url;
        this.siteClusters = siteClusters;
        this.organizationName = organizationName;
        this.organizationlogourl = organizationlogourl;
        this.hasClusteredSites = hasClusteredSites;
        this.typeId = typeId;
        this.typeLabel = typeLabel;
        this.phone = phone;
        this.isSyncedWithRemote = isSyncedWithRemote;
        this.totalRegions = totalRegions;
        this.totalSites = totalSites;
        this.totalUsers = totalUsers;
        this.totalSubmissions = totalSubmissions;
        this.terms_and_labels = terms_and_labels;
        this.regionList = regionList;
        this.siteMetaAttributes = siteMetaAttributes;
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
        dest.writeString(this.url);
        dest.writeString(this.siteClusters);
        dest.writeString(this.organizationName);
        dest.writeString(this.organizationlogourl);
        dest.writeValue(this.hasClusteredSites);
        dest.writeValue(this.typeId);
        dest.writeString(this.typeLabel);
        dest.writeString(this.phone);
        dest.writeByte(this.isSyncedWithRemote ? (byte) 1 : (byte) 0);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSynced ? (byte) 1 : (byte) 0);
        dest.writeString(this.statusMessage);
        dest.writeString(this.terms_and_labels);
        dest.writeLong(this.syncedDate);
        dest.writeInt(this.totalRegions);
        dest.writeInt(this.totalSites);
        dest.writeInt(this.totalSubmissions);
        dest.writeInt(this.totalUsers);
        dest.writeTypedList(this.regionList);
        dest.writeTypedList(this.siteMetaAttributes);
    }

    @Ignore
    protected Project(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.address = in.readString();
        this.lat = in.readString();
        this.lon = in.readString();
        this.url = in.readString();
        this.siteClusters = in.readString();
        this.organizationName = in.readString();
        this.organizationlogourl = in.readString();
        this.hasClusteredSites = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.typeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.typeLabel = in.readString();
        this.phone = in.readString();
        this.isSyncedWithRemote = in.readByte() != 0;
        this.checked = in.readByte() != 0;
        this.isSynced = in.readByte() != 0;
        this.statusMessage = in.readString();
        this.terms_and_labels = in.readString();
        this.syncedDate = in.readLong();
        this.regionList = in.createTypedArrayList(Region.CREATOR);
        this.totalRegions = in.readInt();
        this.totalSites = in.readInt();
        this.totalSubmissions = in.readInt();
        this.totalUsers = in.readInt();
        this.siteMetaAttributes = in.createTypedArrayList(SiteMetaAttribute.CREATOR);
    }

    public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel source) {
            return new Project(source);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };
}
