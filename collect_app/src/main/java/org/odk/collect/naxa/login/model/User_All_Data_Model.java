package org.odk.collect.naxa.login.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Susan on 11/21/2016.
 */
public class User_All_Data_Model {

    @SerializedName("my_sites")
    private List<My_Sites_Model> mySitesModel = new ArrayList<My_Sites_Model>();
    @SerializedName("username")
    private String user_name;
    @SerializedName("profile_pic")
    private String profilepic;
    @SerializedName("server_time")
    private String serverTime;
    @SerializedName("address")
    private String userAddress;
    private String userPhone;
    private String full_name;
    private String userSkype;
    @SerializedName("is_supervisor")
    private Boolean isSupervisor;
    @SerializedName("last_login")
    private String lastLogin;
    @SerializedName("organization_url")
    private String organizationUrl;
    private String organization;
    private String email;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     *     The mySitesModel
     */
    public List<My_Sites_Model> getMySitesModel() {
        return mySitesModel;
    }

    /**
     *
     * @param mySitesModel
     *     The fieldsight_info
     */
    public void setMySitesModel(List<My_Sites_Model> mySitesModel) {
        this.mySitesModel = mySitesModel;
    }

    /**
     *
     * @return
     *     The isSupervisor
     */
    public Boolean getIsSupervisor() {
        return isSupervisor;
    }

    /**
     *
     * @param isSupervisor
     *     The is_supervisor
     */
    public void setIsSupervisor(Boolean isSupervisor) {
        this.isSupervisor = isSupervisor;
    }

    /**
     *
     * @return
     *     The lastLogin
     */
    public String getLastLogin() {
        return lastLogin;
    }

    /**
     *
     * @param lastLogin
     *     The last_login
     */
    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     *
     * @return
     *     The fullName
     */
    public String getFullName() {
        return full_name;
    }

    /**
     *
     * @param fullName
     *     The full_name
     */
    public void setFullName(String fullName) {
        this.full_name = fullName;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String userName) {
        this.user_name = userName;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    /**
     *
     * @return
     *     The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     *     The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Boolean getSupervisor() {
        return isSupervisor;
    }

    public void setSupervisor(Boolean supervisor) {
        isSupervisor = supervisor;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserSkype() {
        return userSkype;
    }

    public void setUserSkype(String userSkype) {
        this.userSkype = userSkype;
    }

    public String getOrganizationUrl() {
        return organizationUrl;
    }

    public void setOrganizationUrl(String organizationUrl) {
        this.organizationUrl = organizationUrl;
    }
}
