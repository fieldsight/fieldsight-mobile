package org.bcss.collect.naxa.generalforms;

import android.arch.persistence.room.TypeConverter;

import com.google.common.reflect.TypeToken;

import org.bcss.collect.naxa.common.GSONInstance;
import org.bcss.collect.naxa.generalforms.data.EmImage;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class EmImageTypeConverter {
    @TypeConverter
    public static List<EmImage> stringToEmImage(String data) {

        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<EmImage>>() {
        }.getType();

        return GSONInstance.getInstance().fromJson(data, listType);
    }

    @TypeConverter
    public static String emImageToString(List<EmImage> someObjects) {
        return GSONInstance.getInstance().toJson(someObjects);
    }

}
