package org.bcss.collect.naxa.common.database;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

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
