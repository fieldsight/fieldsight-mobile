package org.bcss.collect.naxa.stages;

import android.arch.persistence.room.TypeConverter;

import com.google.common.reflect.TypeToken;

import org.bcss.collect.naxa.common.GSONInstance;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class StringListTypeConvertor {
    @TypeConverter
    public static List<String> stringToList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<String>>() {}.getType();

        return GSONInstance.getInstance().fromJson(data, listType);
    }

    @TypeConverter
    public static String ListToString(List<String> someObjects) {
        return GSONInstance.getInstance().toJson(someObjects);
    }
}
