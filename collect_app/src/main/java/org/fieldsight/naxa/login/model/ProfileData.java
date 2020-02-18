
package org.fieldsight.naxa.login.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("phone")
    @Expose
    private String phone;
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
    private String whatsapp;
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
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    @SerializedName("notification_seen_date")
    @Expose
    private String notificationSeenDate;
    @SerializedName("task_last_view_date")
    @Expose
    private String taskLastViewDate;
    @SerializedName("organization")
    @Expose
    private Integer organization;
    @SerializedName("timezone")
    @Expose
    private Integer timezone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getNotificationSeenDate() {
        return notificationSeenDate;
    }

    public void setNotificationSeenDate(String notificationSeenDate) {
        this.notificationSeenDate = notificationSeenDate;
    }

    public String getTaskLastViewDate() {
        return taskLastViewDate;
    }

    public void setTaskLastViewDate(String taskLastViewDate) {
        this.taskLastViewDate = taskLastViewDate;
    }

    public Integer getOrganization() {
        return organization;
    }

    public void setOrganization(Integer organization) {
        this.organization = organization;
    }

    public Integer getTimezone() {
        return timezone;
    }

    public void setTimezone(Integer timezone) {
        this.timezone = timezone;
    }

}
