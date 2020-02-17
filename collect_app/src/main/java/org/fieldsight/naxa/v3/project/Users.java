package org.fieldsight.naxa.v3.project;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class Users {
    public String profilePicture, role, id, fullName, gender, googleTalk, line,
            officeNumber, phone, primaryNumber, secondaryNumber, skype, tango,
            twitter, viber, weChat, whatsApp, address, hike, qq;


    private Users(JSONObject jsonObject) {
        this.profilePicture = jsonObject.optString("profile_picture");
        this.role = jsonObject.optString("role");
        this.id = jsonObject.optString("id");
        this.googleTalk = jsonObject.optString("google_talk");
        this.officeNumber = jsonObject.optString("office_number");
        this.primaryNumber = jsonObject.optString("primary_number");
        this.phone = jsonObject.optString("phone");
        this.secondaryNumber = jsonObject.optString("secondary_number");
        this.skype = jsonObject.optString("skype");
        this.tango = jsonObject.optString("tango");
        this.twitter = jsonObject.optString("twitter");
        this.viber = jsonObject.optString("viber");
        this.weChat = jsonObject.optString("wechat");
        this.whatsApp = jsonObject.optString("whatsapp");
        this.address = jsonObject.optString("address");
        this.hike = jsonObject.optString("hike");
        this.qq = jsonObject.optString("qq");
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
}
