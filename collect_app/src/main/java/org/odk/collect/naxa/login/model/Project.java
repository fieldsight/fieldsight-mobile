package org.odk.collect.naxa.login.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Susan on 11/24/2016.
 */
@Entity(tableName = "project")
public class Project implements Parcelable {

    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String description;
    private String address;
    private String lat;
    private String lon;

    @SerializedName("organization_name")
    private String organizationName;
    @SerializedName("organization_url")
    private String organizationlogourl;

    @SerializedName("cluster_sites")
    private Boolean hasClusteredSites;




    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeString(lat);
        dest.writeString(lon);
        dest.writeString(organizationName);
        dest.writeString(organizationlogourl);
//        dest.writeByte((byte) (hasClusteredSites == null ? 0 : hasClusteredSites ? 1 : 2));
        if (typeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(typeId);
        }
        dest.writeString(typeLabel);
        dest.writeString(phone);
        dest.writeTypedList(siteMetaAttributes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public Boolean getHasClusteredSites() {
        return hasClusteredSites;
    }

    public void setHasClusteredSites(Boolean hasClusteredSites) {
        this.hasClusteredSites = hasClusteredSites;
    }

    protected Project(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        address = in.readString();
        lat = in.readString();
        lon = in.readString();
        organizationName = in.readString();
        organizationlogourl = in.readString();
        if (in.readByte() == 0) {
            typeId = null;
        } else {
            typeId = in.readInt();
        }
        typeLabel = in.readString();
        phone = in.readString();
        siteMetaAttributes = in.createTypedArrayList(SiteMetaAttribute.CREATOR);
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

    @SerializedName("type_id")
    @Expose
    private Integer typeId;
    @SerializedName("type_label")
    @Expose
    private String typeLabel;
    @SerializedName("phone")
    @Expose
    private String phone;

    @Ignore
    @SerializedName("site_meta_attributes")
    @Expose
    private List<SiteMetaAttribute> siteMetaAttributes = null;

    public List<SiteMetaAttribute> getSiteMetaAttributes() {
        return siteMetaAttributes;
    }

    @Ignore
    public Project(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setSiteMetaAttributes(List<SiteMetaAttribute> siteMetaAttributes) {
        this.siteMetaAttributes = siteMetaAttributes;
    }

    public Project() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


}
