package org.fieldsight.naxa.contact;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;



import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@Entity(tableName = "contacts")

public class FieldSightContactModel {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("address")
    private String address;
    @SerializedName("gender")
    private String gender;
    @SerializedName("phone")
    private String phone;
    @SerializedName("skype")
    private String skype;
    @SerializedName("twitter")
    private String twitter;
    @SerializedName("tango")
    private String tango;
    @SerializedName("hike")
    private String hike;
    @SerializedName("qq")
    private String qq;
    @SerializedName("google_talk")
    private String googleTalk;

    @SerializedName("profile_picture")
    private String profilePicture;
    @SerializedName("viber")
    private String viber;
    @SerializedName("whatsapp")
    private String whatsapp;
    @SerializedName("wechat")
    private String wechat;
    @SerializedName("full_name")
    private String fullName;

    @Ignore
    @TypeConverters(RoleModelTypeConverter.class)
    @SerializedName("role")
    private ArrayList<RoleModel> role;

    @SerializedName("primary_number")
    private String primaryNumber;
    @SerializedName("secondary_number")
    private String secondaryNumber;
    @SerializedName("office_number")
    private String officeNumber;

    private String roleString;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
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

    public ArrayList<RoleModel> getRole() {
        return role;
    }

    public void setRole(ArrayList<RoleModel> role) {
        this.role = role;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getRoleString() {
        return roleString;
    }

    public void setRoleString(String roleString) {
        this.roleString = roleString;
    }
}