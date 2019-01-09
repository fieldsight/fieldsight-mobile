package org.bcss.collect.naxa.contact;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

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
    private String google_talk;

    @SerializedName("profile_picture")
    private String profilePicture;
    @SerializedName("viber")
    private String viber;
    @SerializedName("whatsapp")
    private String whatsapp;
    @SerializedName("wechat")
    private String wechat;
    @SerializedName("full_name")
    private String full_name;

    @Ignore
    @TypeConverters(RoleModelTypeConverter.class)
    @SerializedName("role")
    private ArrayList<RoleModel> role;

    @SerializedName("primary_number")
    private String primary_number;
    @SerializedName("secondary_number")
    private String secondary_number;
    @SerializedName("office_number")
    private String office_number;

    private String roleString;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public FieldSightContactModel() {
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

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
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

    public String getGoogle_talk() {
        return google_talk;
    }

    public void setGoogle_talk(String google_talk) {
        this.google_talk = google_talk;
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

    public String getPrimary_number() {
        return primary_number;
    }

    public void setPrimary_number(String primary_number) {
        this.primary_number = primary_number;
    }

    public String getSecondary_number() {
        return secondary_number;
    }

    public void setSecondary_number(String secondary_number) {
        this.secondary_number = secondary_number;
    }

    public String getOffice_number() {
        return office_number;
    }

    public void setOffice_number(String office_number) {
        this.office_number = office_number;
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