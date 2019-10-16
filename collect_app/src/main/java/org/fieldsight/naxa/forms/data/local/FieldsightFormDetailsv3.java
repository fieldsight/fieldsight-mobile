package org.fieldsight.naxa.forms.data.local;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.fieldsight.naxa.common.GSONInstance;
import org.fieldsight.naxa.generalforms.data.Em;
import org.json.JSONArray;
import org.json.JSONObject;
import org.odk.collect.android.logic.FormDetails;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

@Entity(tableName = "fieldsight_formv3")
public class FieldsightFormDetailsv3 {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "id")
    String id;

    @ColumnInfo(name = "site")
    String site;

    @ColumnInfo(name = "PROJECT")
    String project;

    @ColumnInfo(name = "site_project_id")
    String siteProjectId;

    @ColumnInfo(name = "type")
    String type;

    @ColumnInfo(name = "em")
    String em;

    @ColumnInfo(name = "description")
    String description;

    @ColumnInfo(name = "settings")
    String settings;

    @TypeConverters(FormDetailsConverter.class) // add here
            FormDetails formDetails;

    String metaAttributes;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getSiteProjectId() {
        return siteProjectId;
    }

    public void setSiteProjectId(String siteProjectId) {
        this.siteProjectId = siteProjectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FormDetails getFormDetails() {
        return formDetails;
    }

    public void setFormDetails(FormDetails formDetails) {
        this.formDetails = formDetails;
    }

    public String getEm() {
        return em;
    }

    public void setEm(String em) {
        this.em = em;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public String getMetaAttributes() {
        return metaAttributes;
    }

    public void setMetaAttributes(String metaAttributes) {
        this.metaAttributes = metaAttributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Ignore
    public static FormDetails formDetailsfromJSON(JSONObject jsonObject) {
        String formName = jsonObject.optString("name");
        String downloadUrl = jsonObject.optString("downloadUrl");
        String manifestUrl = jsonObject.optString("manifestUrl");
        String formID = jsonObject.optString("formID");
        String formVersion = jsonObject.optString("version");
        String hash = jsonObject.optString("hash");
        return new FormDetails(formName, downloadUrl, manifestUrl, formID, formVersion, hash, null, false, false);
    }

    @Ignore
    public static FieldsightFormDetailsv3 parseFromJSON(JSONObject jsonObject, String type) {
        String[] metaDataKey = new String[]{"schedule"};

        FieldsightFormDetailsv3 fieldsightFormDetailsv3 = new FieldsightFormDetailsv3();
        FormDetails formDetails = formDetailsfromJSON(jsonObject);
        fieldsightFormDetailsv3.setId(jsonObject.optString("id"));
        fieldsightFormDetailsv3.setProject(jsonObject.optString("PROJECT"));
        fieldsightFormDetailsv3.setSiteProjectId(jsonObject.optString("siteProjectId"));
        fieldsightFormDetailsv3.setSite(jsonObject.optString("site"));
        fieldsightFormDetailsv3.setFormDetails(formDetailsfromJSON(jsonObject));
        Timber.i("Fieldsightformdetailsv3, type = %s, em = %s", type, jsonObject.optString("em"));
        fieldsightFormDetailsv3.setEm(jsonObject.optString("em"));
        fieldsightFormDetailsv3.setSettings(jsonObject.optString("settings"));
        fieldsightFormDetailsv3.setDescription(jsonObject.optString("descriptionText"));
        fieldsightFormDetailsv3.setFormDetails(formDetails);
        fieldsightFormDetailsv3.setType(type);
        try {
            JSONObject metaJSON = new JSONObject();
            for (String key : metaDataKey) {
                if (jsonObject.has(key)) {
                    metaJSON.put(key, jsonObject.opt(key));
                }
            }
            fieldsightFormDetailsv3.setMetaAttributes(metaJSON.toString());
        } catch (Exception e) {
            Timber.e(e);
        }
        return fieldsightFormDetailsv3;
    }

    @Ignore
    public static void getMetaJSON(String prefix, JSONObject fromJSON, JSONObject atJSON) {
        try {
            atJSON.put(prefix + "_name", fromJSON.optString("name"));
            atJSON.put(prefix + "_description", fromJSON.optString("description"));
            atJSON.put(prefix + "_type", fromJSON.optJSONArray("types"));
            atJSON.put(prefix + "_order", fromJSON.optString("order"));
            atJSON.put(prefix + "_weight", fromJSON.optString("weight"));
            if (fromJSON.has("tags")) {
                atJSON.put(prefix + "_tags", fromJSON.optJSONArray("tags"));
            }
            if (fromJSON.has("regions")) {
                atJSON.put(prefix + "_regions", fromJSON.optJSONArray("regions"));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Ignore
    public static List<FieldsightFormDetailsv3> fieldsightFormDetailsV3FromJSON(JSONArray jsonArray) {
        List<FieldsightFormDetailsv3> fieldsightFormDetailsNewArrayList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject stageFormJSON = jsonArray.optJSONObject(i);
                Timber.i("FieldSightFormDetailsv3, stageFormJSON = %s", stageFormJSON.toString());
                // stage for basic id
                JSONObject metaJSON = new JSONObject();
                getMetaJSON("stage", stageFormJSON, metaJSON);
                JSONArray subStageArray = stageFormJSON.optJSONArray("sub_stages");

                for (int j = 0; j < subStageArray.length(); j++) {
                    JSONObject subStageFormJSON = subStageArray.optJSONObject(j);
                    getMetaJSON("substage", subStageFormJSON, metaJSON);

                    FieldsightFormDetailsv3 fieldsightFormDetails = new FieldsightFormDetailsv3();

                    JSONObject subStageFormDetailJSON = subStageFormJSON.optJSONObject("stage_forms");
                    fieldsightFormDetails.setId(subStageFormDetailJSON.optString("id"));
                    fieldsightFormDetails.setProject(subStageFormDetailJSON.optString("PROJECT"));
                    fieldsightFormDetails.setSiteProjectId(subStageFormDetailJSON.optString("siteProjectId"));
                    fieldsightFormDetails.setSite(subStageFormDetailJSON.optString("site"));

                    fieldsightFormDetails.setFormDetails(formDetailsfromJSON(subStageFormDetailJSON));
                    fieldsightFormDetails.setDescription(subStageFormDetailJSON.optString("descriptionText"));
                    Timber.i("FieldsightFormdetailsv3, em = %s", subStageFormDetailJSON.optString("em"));
                    fieldsightFormDetails.setEm(subStageFormDetailJSON.optString("em"));
                    fieldsightFormDetails.setSettings(subStageFormDetailJSON.optString("settings"));
                    fieldsightFormDetails.setType("stage");
                    fieldsightFormDetails.setMetaAttributes(metaJSON.toString());
                    fieldsightFormDetailsNewArrayList.add(fieldsightFormDetails);
                }

            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return fieldsightFormDetailsNewArrayList;
    }


    //TODO: replace with manual parsing
    public static StageSubStage getStageAndSubstage(String stageAndSubstage) {
        return GSONInstance.getInstance().fromJson(stageAndSubstage, StageSubStage.class);
    }

    //TODO: replace with manual parsing
    public static Em mapStringToEm(String em) {
        return GSONInstance.getInstance().fromJson(em, Em.class);
    }

}
