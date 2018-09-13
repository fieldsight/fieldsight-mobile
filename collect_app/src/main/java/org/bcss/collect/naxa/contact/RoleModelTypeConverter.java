package org.bcss.collect.naxa.contact;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.reflect.TypeToken;

import org.bcss.collect.naxa.common.GSONInstance;
import org.bcss.collect.naxa.site.data.SiteRegion;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RoleModelTypeConverter {

    @TypeConverter
    public static String roleModelToString(RoleModel roleModel) {
        return GSONInstance.getInstance().toJson(roleModel);
    }

    @TypeConverter
    public static ArrayList<RoleModel> stringToRoleModel(String roleModel) {
        Type type = new TypeToken<ArrayList<RoleModel>>() {}.getType();
        return GSONInstance.getInstance().fromJson(roleModel,type);
    }
}
