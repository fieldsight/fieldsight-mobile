package org.fieldsight.naxa.v3.project;

import android.text.TextUtils;

import com.google.common.base.Objects;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import timber.log.Timber;

public class Users {
    public String profilePicture, role, id, fullName, gender, googleTalk, line,
            officeNumber, phone, primaryNumber, secondaryNumber, skype, tango,
            twitter, viber, weChat, whatsApp, address, hike, qq, email, username,
            firstName, lastName;


    private Users(JSONObject jsonObject) {
        this.profilePicture = jsonObject.optString("profile_picture");
        if (TextUtils.isEmpty(this.profilePicture)) {
            this.profilePicture = jsonObject.optString("profile_pic");
        }
        this.fullName = jsonObject.optString("full_name");
        this.email = jsonObject.optString("email");
        this.address = jsonObject.optString("address");

        if (jsonObject.has("profile_data")) {
            try {
                JSONObject profileObject = jsonObject.getJSONObject("profile_data");
                parseProfileData(profileObject);
            } catch (JSONException e) {
                Timber.w(e);
            }
        } else {
            parseProfileData(jsonObject);
        }


    }

    private void parseProfileData(JSONObject profileData) {
        this.role = profileData.optString("role");
        this.id = profileData.optString("id");
        this.googleTalk = profileData.optString("google_talk");
        this.officeNumber = profileData.optString("office_number");
        this.primaryNumber = profileData.optString("primary_number");
        this.phone = profileData.optString("phone");
        this.secondaryNumber = profileData.optString("secondary_number");
        this.skype = profileData.optString("skype");
        this.tango = profileData.optString("tango");
        this.twitter = profileData.optString("twitter");
        this.viber = profileData.optString("viber");
        this.weChat = profileData.optString("wechat");
        this.whatsApp = profileData.optString("whatsapp");
        this.hike = profileData.optString("hike");
        this.qq = profileData.optString("qq");
        this.gender = profileData.optString("gender");
        this.firstName = profileData.optString("first_name");
        this.lastName = profileData.optString("last_name");
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(profilePicture, role, id, fullName, gender, googleTalk, line, officeNumber, phone, primaryNumber, secondaryNumber, skype, tango, twitter, viber, weChat, whatsApp, address, hike, qq, email, username);
    }

    public static List<Users> toList(String users) {
        List<Users> usersList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(users);
            for (int i = 0; i < jsonArray.length(); i++) {
                usersList.add(new Users(jsonArray.optJSONObject(i)));
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return usersList;
    }

    @Nullable
    public static Users toUser(@Nonnull String user) {
        Users users = null;
        try {
            JSONObject jsonObject = new JSONObject(user);
            users = new Users(jsonObject);
        } catch (Exception e) {
            Timber.e(e);
        }

        return users;
    }
}
