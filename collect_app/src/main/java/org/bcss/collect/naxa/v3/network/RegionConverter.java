package org.bcss.collect.naxa.v3.network;

import android.arch.persistence.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class RegionConverter {

    @TypeConverter
    public static List<Region> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Region>>() {}.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String regionToString(List<Region> someObjects) {
        return new Gson().toJson(someObjects);
    }
}