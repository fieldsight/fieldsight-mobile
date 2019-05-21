package org.bcss.collect.naxa.v3.network;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "syncstat")
public class SyncStat {
    @PrimaryKey(autoGenerate = true)
     public int uid;

    @ColumnInfo(name = "project_id")
    String projectId;
    @ColumnInfo(name = "type")
    String type;
    @ColumnInfo(name = "failed_url")
    String failedUrl;
    @ColumnInfo(name = "started")
    boolean started;
    @ColumnInfo(name = "status")
    int status;

    public SyncStat() {}

    /*
            @params projectId
            @params type
            @params failedUrl
            @params started
     */
    public SyncStat(String projectId, String type, String failedUrl, boolean started, int status) {
        this.projectId = projectId;
        this.type = type;
        this.failedUrl = failedUrl;
        this.started = started;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFailedUrl() {
        return failedUrl;
    }

    public void setFailedUrl(String failedUrl) {
        this.failedUrl = failedUrl;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
