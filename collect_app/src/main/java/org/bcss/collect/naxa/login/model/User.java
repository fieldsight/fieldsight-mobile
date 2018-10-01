package org.bcss.collect.naxa.login.model;

import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
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
    @SerializedName("server_time")
    private String serverTime;

    @SerializedName("profile_pic")
    @Expose
    private String profilepic;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;

    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    private String address;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("skype")
    @Expose
    private String skype;
    @SerializedName("primary_number")
    @Expose
    private String primaryNumber;
    @SerializedName("secondary_number")
    @Expose
    private String secondaryNumber;
    @SerializedName("office_number")
    @Expose
    private String officeNumber;
    @SerializedName("viber")
    @Expose
    private String viber;
    @SerializedName("whatsapp")
    @Expose
    private String whatsApp;
    @SerializedName("wechat")
    @Expose
    private String wechat;
    @SerializedName("line")
    @Expose
    private String line;
    @SerializedName("tango")
    @Expose
    private String tango;
    @SerializedName("hike")
    @Expose
    private String hike;
    @SerializedName("qq")
    @Expose
    private String qq;
    @SerializedName("google_talk")
    @Expose
    private String googleTalk;
    @SerializedName("twitter")
    @Expose
    private String twitter;
    @SerializedName("organization")
    @Expose
    private String organization;
    private String project;

    @SerializedName("is_supervisor")
    private Boolean isSupervisor;
    @SerializedName("last_login")
    private String lastLogin;
    @SerializedName("organization_url")
    private String organizationUrl;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private boolean sync;


    public User() {
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
        if (firstName == null) firstName = "Full";
        if (lastName == null) lastName = "Name";
        return String.format("%s %s", firstName, lastName);
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
        String[] name = full_name.split(" ");
        firstName = name[0];
        if (name.length > 1) {
            lastName = name[1];
        } else {
            lastName = "";
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
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
                Objects.equal(firstName, user.firstName) &&
                Objects.equal(lastName, user.lastName) &&
                Objects.equal(email, user.email) &&
                Objects.equal(phone, user.phone) &&
                Objects.equal(address, user.address) &&
                Objects.equal(gender, user.gender) &&
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
        return Objects.hashCode(mySitesModel, user_name, profilepic, serverTime, firstName, lastName, email, phone, address, gender, skype, primaryNumber, secondaryNumber, officeNumber, viber, whatsApp, wechat, line, tango, hike, qq, googleTalk, twitter, organization, project, isSupervisor, lastLogin, organizationUrl, additionalProperties);
    }
}
