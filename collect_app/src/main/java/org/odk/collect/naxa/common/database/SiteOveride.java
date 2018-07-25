package org.odk.collect.naxa.common.database;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "site_overide_ids")
public class SiteOveride {
    @PrimaryKey
    @NonNull
    private String projectId;
    private String generalFormIds;
    private String scheduleFormIds;
    private String stagedFormIds;

    public SiteOveride() {
    }

    @Ignore
    public SiteOveride(@NonNull String projectId, String generalFormIds, String scheduleFormIds, String stagedFormIds) {
        this.projectId = projectId;
        this.generalFormIds = generalFormIds;
        this.scheduleFormIds = scheduleFormIds;
        this.stagedFormIds = stagedFormIds;
    }

    @NonNull
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(@NonNull String projectId) {
        this.projectId = projectId;
    }

    public String getGeneralFormIds() {
        return generalFormIds;
    }

    public void setGeneralFormIds(String generalFormIds) {
        this.generalFormIds = generalFormIds;
    }

    public String getScheduleFormIds() {
        return scheduleFormIds;
    }

    public void setScheduleFormIds(String scheduleFormIds) {
        this.scheduleFormIds = scheduleFormIds;
    }

    public String getStagedFormIds() {
        return stagedFormIds;
    }

    public void setStagedFormIds(String stagedFormIds) {
        this.stagedFormIds = stagedFormIds;
    }
}
