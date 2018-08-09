package org.bcss.collect.naxa.firebase;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Susan on 12/30/2016.
 */
public class FCMParameter {
    @SerializedName("dev_id")
    String dev_id;
    @SerializedName("reg_id")
    String reg_id;
    @SerializedName("name")
    String name;
    @SerializedName("is_active")
    String is_active;

    @Override
    public String toString() {
        return "FCMParameter{" +
                "dev_id='" + dev_id + '\'' +
                ", reg_id='" + reg_id + '\'' +
                ", name='" + name + '\'' +
                ", is_active='" + is_active + '\'' +
                '}';
    }

    public FCMParameter(String deviceId, String deviceToken, String deviceName, String deviceStatus) {
        this.dev_id = deviceId;
        this.reg_id = deviceToken;
        this.name = deviceName;
        this.is_active = deviceStatus;
    }

    public String getDev_id() {
        return dev_id;
    }

    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
    }

    public String getReg_id() {
        return reg_id;
    }

    public void setReg_id(String reg_id) {
        this.reg_id = reg_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }
}
