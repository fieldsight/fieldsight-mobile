package org.fieldsight.naxa.v3.project;

import org.fieldsight.naxa.login.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Users {
    String profilePicture, role, id, fullName;
    public Users(JSONObject jsonObject) {
        this.profilePicture = jsonObject.optString("profile_picture");
        this.role = jsonObject.optString("role");
        this.id = jsonObject.optString("id");
        this.fullName = jsonObject.optString("full_name");
    }


    public static List<Users> toList(String users) {
        List<Users> usersList = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(users);
            for(int i = 0; i < jsonArray.length(); i++) {
                usersList.add(new Users(jsonArray.optJSONObject(i)));
            }
        }catch (Exception e){e.printStackTrace();}

        return usersList;
    }
}
