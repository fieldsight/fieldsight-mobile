package org.bcss.collect.naxa.login.model;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.reflect.TypeToken;

import org.bcss.collect.naxa.common.GSONInstance;

import java.lang.reflect.Type;
import java.util.List;

public class SiteMetaAttributesTypeConverter {

    @TypeConverter
    public static List<SiteMetaAttribute> toSiteMetaAttribute(String json) {
        Type type = new TypeToken<List<SiteMetaAttribute>>() {
        }.getType();
        return GSONInstance.getInstance().fromJson(json, type);
    }

    @TypeConverter
    public static String toString(List<SiteMetaAttribute> siteMetaAttribute) {
        return GSONInstance.getInstance().toJson(siteMetaAttribute);
    }
}
