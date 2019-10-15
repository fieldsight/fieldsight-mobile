package org.fieldsight.naxa.login.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("TOKEN")
    private String token;

    @SerializedName("data")
    String message;


    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }


}
