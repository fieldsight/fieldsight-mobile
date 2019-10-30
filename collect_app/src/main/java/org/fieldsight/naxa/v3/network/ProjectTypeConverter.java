package org.fieldsight.naxa.v3.network;


import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ProjectTypeConverter {

    private ProjectTypeConverter(){

    }

    @TypeConverter
    public static List<ProjectTypes> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<ProjectTypes>>() {}.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String regionToString(List<ProjectTypes> someObjects) {
        return new Gson().toJson(someObjects);
    }
}