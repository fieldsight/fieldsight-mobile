package org.fieldsight.naxa.firebase;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Susan on 12/30/2016.
 */
public class FCMParameter {
    @SerializedName("dev_id")
    private
    String devId;
    @SerializedName("reg_id")
    private
    String regId;
    @SerializedName("name")
    private
    String name;
    @SerializedName("is_active")
    private
    String isActive;

    @Override
    public String toString() {
        return "FCMParameter{" +
                "devId='" + devId + '\'' +
                ", regId='" + regId + '\'' +
                ", name='" + name + '\'' +
                ", isActive='" + isActive + '\'' +
                '}';
    }

    public FCMParameter() {
    }

    public FCMParameter(String deviceId, String deviceToken, String deviceName, String deviceStatus) {
        this.devId = deviceId;
        this.regId = deviceToken;
        this.name = deviceName;
        this.isActive = deviceStatus;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
