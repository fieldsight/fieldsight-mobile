package org.bcss.collect.naxa.project.data.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.login.model.SiteMetaAttribute;

public class ProjectResponseV3 {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("organization")
    @Expose
    private Organization organization;
    @SerializedName("project_region")
    @Expose
    private List<Object> projectRegion = null;
    @SerializedName("meta_attributes")
    @Expose
    private List<SiteMetaAttribute> metaAttributes = null;
    @SerializedName("has_site_role")
    @Expose
    private Boolean hasSiteRole;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<Object> getProjectRegion() {
        return projectRegion;
    }

    public void setProjectRegion(List<Object> projectRegion) {
        this.projectRegion = projectRegion;
    }

    public List<SiteMetaAttribute> getMetaAttributes() {
        return metaAttributes;
    }

    public void setMetaAttributes(List<SiteMetaAttribute> metaAttributes) {
        this.metaAttributes = metaAttributes;
    }

    public Boolean getHasSiteRole() {
        return hasSiteRole;
    }

    public void setHasSiteRole(Boolean hasSiteRole) {
        this.hasSiteRole = hasSiteRole;
    }
}
