package org.odk.collect.naxa.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.odk.collect.android.application.Collect;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author nishon.tan
 */

public class FormSessionManager {

    private String KEY_URL = "url";
    private String KEY_FORM_TYPE = "form_type";
    private String KEY_PROJECT_ID = "project_id";
    private String KEY_FORM_DEPLOYED_FROM = "form_deployed_from";
    private String KEY_SITE_ID = "form_deployed_from";


    public FormSessionManager(Context context) {

    }

    public void setFormSession(@NonNull String fsFormId, @NonNull String siteId, @NonNull String projectId, @NonNull String formDeployedFrom) {
        SharedPreferences pref = Collect.getInstance().getSharedPreferences(SharedPreferenceUtils.PREF_KEY.FORM_SESSION, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_URL, fsFormId + "/" + siteId);
        editor.putString(KEY_PROJECT_ID, projectId);
        editor.putString(KEY_SITE_ID, siteId);
        editor.putString(KEY_FORM_DEPLOYED_FROM, formDeployedFrom);
        editor.apply();
    }


    public String getFormDeployedFrom() {

        SharedPreferences pref = Collect.getInstance().getSharedPreferences(SharedPreferenceUtils.PREF_KEY.FORM_SESSION, MODE_PRIVATE);
        return pref.getString(KEY_FORM_DEPLOYED_FROM, "");

    }

    public String getFsFormId() {
        SharedPreferences pref = Collect.getInstance().getSharedPreferences(SharedPreferenceUtils.PREF_KEY.FORM_SESSION, MODE_PRIVATE);
        String url = pref.getString(KEY_URL, "");
        String[] parts = url.split("/");
        String fsForId = parts[0];
        return fsForId.trim();

    }


    public String getSiteId() {

        SharedPreferences pref = Collect.getInstance().getSharedPreferences(SharedPreferenceUtils.PREF_KEY.FORM_SESSION, MODE_PRIVATE);
        String url = pref.getString(KEY_URL, "");
        String[] parts = url.split("/");

        String siteId = parts[1];

        return siteId.trim();
    }

    public String getFormType() {

        String formType = null;

        formType = pref.getString(KEY_FORM_TYPE, "");
        return formType.trim();
    }


    public String getFormUploadURL() {
        return pref.getString("url", "");
    }

    SharedPreferences pref = Collect.getInstance().getSharedPreferences(SharedPreferenceUtils.PREF_KEY.FORM_SESSION, MODE_PRIVATE);

    //clear data if it has data
    //then return true if the data was cleared
    public Boolean clearFormSession() {
        Boolean hasUrl = pref.getBoolean(KEY_FORM_TYPE, false);

        return false;

    }

    public String getProjectId() {
        return pref.getString(KEY_PROJECT_ID, "").trim();
    }
}
