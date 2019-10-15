package org.fieldsight.naxa.generalforms;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;

import org.fieldsight.naxa.common.GSONInstance;
import org.fieldsight.naxa.generalforms.data.EmImage;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class EmImageTypeConverter {

    private EmImageTypeConverter(){

    }

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
