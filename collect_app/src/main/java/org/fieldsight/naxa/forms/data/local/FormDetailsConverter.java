package org.fieldsight.naxa.forms.data.local;

import androidx.room.TypeConverter;

import org.json.JSONObject;
import org.odk.collect.android.logic.FormDetails;

import java.io.Serializable;

public class FormDetailsConverter implements Serializable {

    @TypeConverter
    public String fromFormDetail(FormDetails formDetails) {
        String jsonData = "{";
        try{
           jsonData += "\"downloadUrl\": \""+formDetails.getDownloadUrl()+"\",\n" +
                   "            \"manifestUrl\": \""+formDetails.getManifestUrl()+"\",\n" +
                   "            \"name\": \""+formDetails.getFormName()+"\",\n" +
                   "            \"formID\": \""+formDetails.getFormID()+"\",\n" +
                   "            \"version\": \""+formDetails.getFormVersion()+"\",\n" +
                   "            \"hash\": \""+formDetails.getHash()+"\"";
        }catch (Exception e){e.printStackTrace();}
        return jsonData += "}";
    }

    @TypeConverter
    public FormDetails fromString(String value) {
        try {
            JSONObject jsonObject = new JSONObject(value);
            return FieldsightFormDetailsv3.formDetailsfromJSON(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
