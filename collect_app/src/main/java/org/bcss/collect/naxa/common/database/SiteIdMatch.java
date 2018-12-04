package org.bcss.collect.naxa.common.database;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "site_id_match")
public class SiteIdMatch {
    @PrimaryKey
    @NonNull
    private String oldSiteId;
    private String newSiteId;


    public SiteIdMatch() {

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
