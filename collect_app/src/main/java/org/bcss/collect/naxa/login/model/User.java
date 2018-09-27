package org.bcss.collect.naxa.login.model;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Susan on 11/21/2016.
 */
public class User {

    @SerializedName("my_sites")
    private List<MySites> mySitesModel = new ArrayList<MySites>();
    @SerializedName("username")
    private String user_name;
    @SerializedName("profile_pic")
    private String profilepic;
    @SerializedName("server_time")
    private String serverTime;

    private String full_name;
    private String email;
    private String phone;
    @SerializedName("address")
    private String address;
    private String gender;

    private String skype;

    private String primaryNumber;
    private String secondaryNumber;
    private String officeNumber;
    private String viber;
    private String whatsApp;
    private String wechat;
    private String line;
    private String tango;
    private String hike;
    private String qq;
    private String googleTalk;
    private String twitter;
    private String organization;
    private String project;
    @SerializedName("is_supervisor")
    private Boolean isSupervisor;

    @SerializedName("last_login")
    private String lastLogin;
    @SerializedName("organization_url")
    private String organizationUrl;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public User() {
//        this.mySitesModel = new ArrayList<>();
//        this.additionalProperties = new HashMap<>();
//        this.user_name = "";
//        this.profilepic = "";
//        this.serverTime = "";
//        this.full_name = "";
//        this.email = "";
//        this.phone = "";
//        this.address = "";
//        this.gender = "";
//        this.skype = "";
//        this.primaryNumber = "";
//        this.secondaryNumber = "";
//        this.officeNumber = "";
//        this.viber = "";
//        this.whatsApp = "";
//        this.wechat = "";
//        this.line = "";
//        this.tango = "";
//        this.hike = "";
//        this.qq = "";
//        this.googleTalk = "";
//        this.twitter = "";
//        this.organization = "";
//        this.project = "";
//        this.organizationUrl = "";
//        this.lastLogin = "";
//        this.isSupervisor = false;
    }

    /**
     * @return The mySitesModel
     */
    public List<MySites> getMySitesModel() {
        return mySitesModel;
    }

    /**
     * @return The isSupervisor
     */
    public Boolean getIsSupervisor() {
        return isSupervisor;
    }


    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }


    public String getUser_name() {
        return user_name;
    }


    public String getFull_name() {
        return full_name;
    }

    public void setMySitesModel(List<MySites> mySitesModel) {
        this.mySitesModel = mySitesModel;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getPrimaryNumber() {
        return primaryNumber;
    }

    public void setPrimaryNumber(String primaryNumber) {
        this.primaryNumber = primaryNumber;
    }

    public String getSecondaryNumber() {
        return secondaryNumber;
    }

    public void setSecondaryNumber(String secondaryNumber) {
        this.secondaryNumber = secondaryNumber;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    public String getViber() {
        return viber;
    }

    public void setViber(String viber) {
        this.viber = viber;
    }

    public String getWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(String whatsApp) {
        this.whatsApp = whatsApp;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getTango() {
        return tango;
    }

    public void setTango(String tango) {
        this.tango = tango;
    }

    public String getHike() {
        return hike;
    }

    public void setHike(String hike) {
        this.hike = hike;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getGoogleTalk() {
        return googleTalk;
    }

    public void setGoogleTalk(String googleTalk) {
        this.googleTalk = googleTalk;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Boolean getSupervisor() {
        return isSupervisor;
    }

    public void setSupervisor(Boolean supervisor) {
        isSupervisor = supervisor;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getOrganizationUrl() {
        return organizationUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setOrganizationUrl(String organizationUrl) {
        this.organizationUrl = organizationUrl;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equal(mySitesModel, user.mySitesModel) &&
                Objects.equal(user_name, user.user_name) &&
                Objects.equal(profilepic, user.profilepic) &&
                Objects.equal(serverTime, user.serverTime) &&
                Objects.equal(full_name, user.full_name) &&
                Objects.equal(email, user.email) &&
                Objects.equal(address, user.address) &&
                Objects.equal(skype, user.skype) &&
                Objects.equal(primaryNumber, user.primaryNumber) &&
                Objects.equal(secondaryNumber, user.secondaryNumber) &&
                Objects.equal(officeNumber, user.officeNumber) &&
                Objects.equal(viber, user.viber) &&
                Objects.equal(whatsApp, user.whatsApp) &&
                Objects.equal(wechat, user.wechat) &&
                Objects.equal(line, user.line) &&
                Objects.equal(tango, user.tango) &&
                Objects.equal(hike, user.hike) &&
                Objects.equal(qq, user.qq) &&
                Objects.equal(googleTalk, user.googleTalk) &&
                Objects.equal(twitter, user.twitter) &&
                Objects.equal(organization, user.organization) &&
                Objects.equal(project, user.project) &&
                Objects.equal(isSupervisor, user.isSupervisor) &&
                Objects.equal(lastLogin, user.lastLogin) &&
                Objects.equal(organizationUrl, user.organizationUrl) &&
                Objects.equal(additionalProperties, user.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mySitesModel, user_name, profilepic, serverTime, full_name, email, address, skype, primaryNumber, secondaryNumber, officeNumber, viber, whatsApp, wechat, line, tango, hike, qq, googleTalk, twitter, organization, project, isSupervisor, lastLogin, organizationUrl, additionalProperties);
    }


}
