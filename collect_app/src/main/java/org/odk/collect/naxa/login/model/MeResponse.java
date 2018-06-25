package org.odk.collect.naxa.login.model;


import com.google.gson.annotations.SerializedName;


public class MeResponse {

    private String code;
    private User data;

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

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
