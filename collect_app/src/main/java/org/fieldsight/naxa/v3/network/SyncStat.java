package org.fieldsight.naxa.v3.network;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.jetbrains.annotations.NotNull;
@SuppressWarnings("PMD")
@Entity(tableName = "syncstat", primaryKeys = {"project_id", "type"})

public class SyncStat {
    @NonNull
    @ColumnInfo(name = "project_id")
    String projectId;

    @NonNull
    @ColumnInfo(name = "type")
    String type;

    @ColumnInfo(name = "failed_url")
    private
    String failedUrl;

    @ColumnInfo(name = "started")
    private
    boolean started;

    @ColumnInfo(name = "status")
    int status;

    @ColumnInfo(name = "created_date")
    long created_date;

    private int total;
    private int progress;

    SyncStat() {
    }

    /*
            @params projectId
            @params type
            @params failedUrl
            @params started
     */
    @Ignore
    public SyncStat(@NotNull String projectId, @NotNull String type, String failedUrl, boolean started, int status, long created_date) {
        this.projectId = projectId;
        this.type = type;
        this.failedUrl = failedUrl;
        this.started = started;
        this.status = status;
        this.created_date = created_date;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTotal() {
        return total;
    }

    public int getProgress() {
        return progress;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getCreated_date() {
        return created_date;
    }

    public void setCreated_date(long created_date) {
        this.created_date = created_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @NotNull
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(@NotNull String projectId) {
        this.projectId = projectId;
    }

    @NotNull
    public String getType() {
        return type;
    }

    public void setType(@NotNull String type) {
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

    public boolean isProgressBarEnabled() {
        return getTotal() > 0;
    }

}
