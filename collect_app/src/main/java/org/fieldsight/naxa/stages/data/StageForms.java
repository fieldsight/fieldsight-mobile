
package org.fieldsight.naxa.stages.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Ignore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StageForms implements Parcelable {

    @SerializedName("xf")
    @Expose
    private Xf xf;
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("downloadUrl")
    private String downloadUrl;

    @SerializedName("manifestUrl")
    private String manifestUrl;

    @Ignore
    @SerializedName("name")
    private String formName;

    @Ignore
    @SerializedName("hash")
    private String hash;

    @Ignore
    @SerializedName("version")
    private String version;

    @Ignore
    @SerializedName("formID")
    private String idString;


    public String getFormName() {
        return formName;
    }

    public String getHash() {
        return hash;
    }

    public String getVersion() {
        return version;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getManifestUrl() {
        return manifestUrl;
    }

    public void setManifestUrl(String manifestUrl) {
        this.manifestUrl = manifestUrl;
    }

    public Xf getXf() {
        return xf;
    }

    public void setXf(Xf xf) {
        this.xf = xf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdString() {
        return idString;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.xf, flags);
        dest.writeString(this.id);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.manifestUrl);
        dest.writeString(this.formName);
        dest.writeString(this.hash);
        dest.writeString(this.version);
        dest.writeString(this.idString);
    }

    public StageForms() {
    }

    protected StageForms(Parcel in) {
        this.xf = in.readParcelable(Xf.class.getClassLoader());
        this.id = in.readString();
        this.downloadUrl = in.readString();
        this.manifestUrl = in.readString();
        this.formName = in.readString();
        this.hash = in.readString();
        this.version = in.readString();
        this.idString = in.readString();
    }

    public static final Parcelable.Creator<StageForms> CREATOR = new Parcelable.Creator<StageForms>() {
        @Override
        public StageForms createFromParcel(Parcel source) {
            return new StageForms(source);
        }

        @Override
        public StageForms[] newArray(int size) {
            return new StageForms[size];
        }
    };
}
