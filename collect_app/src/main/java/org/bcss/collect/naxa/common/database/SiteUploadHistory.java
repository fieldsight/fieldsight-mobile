package org.bcss.collect.naxa.common.database;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "site_upload_history")
public class SiteUploadHistory {
    @PrimaryKey
    @NonNull
    private String newSiteId;
    private String oldSiteId;


    public SiteUploadHistory() {

    }

    @Ignore
    public SiteUploadHistory(@NonNull String newSiteId, String oldSiteId) {
        this.newSiteId = newSiteId;
        this.oldSiteId = oldSiteId;
    }

    @NonNull
    public String getOldSiteId() {
        return oldSiteId;
    }

    public void setOldSiteId(@NonNull String oldSiteId) {
        this.oldSiteId = oldSiteId;
    }

    public String getNewSiteId() {
        return newSiteId;
    }

    public void setNewSiteId(String newSiteId) {
        this.newSiteId = newSiteId;
    }
}
