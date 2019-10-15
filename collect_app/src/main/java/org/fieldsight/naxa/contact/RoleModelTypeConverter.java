package org.fieldsight.naxa.contact;

import androidx.room.TypeConverter;

import com.google.gson.reflect.TypeToken;

import org.fieldsight.naxa.common.GSONInstance;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RoleModelTypeConverter {

    private RoleModelTypeConverter() {

    }

    @TypeConverter
    public static String roleModelToString(RoleModel roleModel) {
        return GSONInstance.getInstance().toJson(roleModel);
    }

    @TypeConverter
    public static ArrayList<RoleModel> stringToRoleModel(String roleModel) {
        Type type = new TypeToken<ArrayList<RoleModel>>() {
        }.getType();
        return GSONInstance.getInstance().fromJson(roleModel, type);
    }
}
