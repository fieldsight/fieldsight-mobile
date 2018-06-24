package org.odk.collect.naxa.login.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MeResponse {

    private String code;
    private User_All_Data_Model data;

    @SerializedName("fieldsight_model")

    /**
     *
     * @return
     *     The code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     *     The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    public User_All_Data_Model getData() {
        return data;
    }

    public void setData(User_All_Data_Model data) {
        this.data = data;
    }
}
