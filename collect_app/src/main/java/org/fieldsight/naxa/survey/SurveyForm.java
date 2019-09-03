package org.fieldsight.naxa.survey;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "survey_forms")
public class SurveyForm {

    @NonNull
    @PrimaryKey
    @SerializedName("id")
    private String fsFormId;

    @SerializedName("project")
    private String projectId;

    @Ignore
    @SerializedName("downloadUrl")
    private String downloadUrl;

    @Ignore
    @SerializedName("manifestUrl")
    private String manifestUrl;

    @SerializedName("formID")
    private String idString;

    @SerializedName("name")
    private String name;

    @Ignore
    @SerializedName("version")
    private String version;

    @Ignore
    @SerializedName("hash")
    private String hash;


    public SurveyForm() {

    }

    @Ignore
    public SurveyForm(String fsFormId, String projectId, String idString, String name) {
        this.fsFormId = fsFormId;
        this.projectId = projectId;
        this.idString = idString;
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public String getHash() {
        return hash;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getManifestUrl() {
        return manifestUrl;
    }

    public void setManifestUrl(String manifestUrl) {
        this.manifestUrl = manifestUrl;
    }

    public String getFsFormId() {
        return fsFormId;
    }

    public void setFsFormId(String fsFormId) {
        this.fsFormId = fsFormId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
