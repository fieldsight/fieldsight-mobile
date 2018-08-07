
package org.bcss.collect.naxa.stages.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StageForms {

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


}
