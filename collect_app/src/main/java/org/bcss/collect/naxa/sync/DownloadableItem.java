package org.bcss.collect.naxa.sync;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;


@Entity(tableName = "sync")
public class DownloadableItem {

    @PrimaryKey
    private int uid;
    private int downloadingStatus;
    private String title;
    private String detail;
    private boolean checked;
    private String lastSyncDateTime;
    private int syncProgress;
    private int syncTotal;
    private String errorMessage;

    @Ignore
    private boolean isOutOfSync;

    @Ignore
    private boolean isSelected;

    public DownloadableItem() {

    }

    @Ignore
    public DownloadableItem(int uid, int downloadingStatus, String lastSyncDateTime, String title, String detail) {
        this.uid = uid;
        this.downloadingStatus = downloadingStatus;
        this.lastSyncDateTime = lastSyncDateTime;
        this.title = title;
        this.detail = detail;
        this.isSelected = false;
        this.checked = true;
    }

    @Ignore
    public DownloadableItem(int uid, int downloadingStatus, String title, String detail) {
        this.uid = uid;
        this.downloadingStatus = downloadingStatus;
        this.title = title;
        this.detail = detail;
        this.isSelected = false;
        this.checked = false;
    }


    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    boolean isOutOfSync() {
        return isOutOfSync;
    }

    void setOutOfSync(boolean outOfSync) {
        isOutOfSync = outOfSync;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    public boolean getIsSelected() {
        return isSelected;
    }

    public void toggleSelected() {
        isSelected = !isSelected;
    }

    public boolean isChecked() {
        return checked;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getDownloadingStatus() {
        return downloadingStatus;
    }

    public void setDownloadingStatus(int downloadingStatus) {
        this.downloadingStatus = downloadingStatus;
    }

    public String getLastSyncDateTime() {
        return lastSyncDateTime;
    }

    public void setLastSyncDateTime(String lastSyncDateTime) {
        this.lastSyncDateTime = lastSyncDateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getSyncProgress() {
        return syncProgress;
    }

    public void setSyncProgress(int syncProgress) {
        this.syncProgress = syncProgress;
    }

    public int getSyncTotal() {
        return syncTotal;
    }

    public void setSyncTotal(int syncTotal) {
        this.syncTotal = syncTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadableItem that = (DownloadableItem) o;
        return uid == that.uid &&
                downloadingStatus == that.downloadingStatus &&
                checked == that.checked &&

                isOutOfSync == that.isOutOfSync &&
                isSelected == that.isSelected &&
                Objects.equal(lastSyncDateTime, that.lastSyncDateTime) &&
                Objects.equal(title, that.title) &&
                Objects.equal(detail, that.detail);
    }

    @NonNull
    @Override
    public String toString() {
        return "SyncableItem{" +
                "uid=" + uid +
                ", downloadingStatus=" + downloadingStatus +
                ", lastSyncDateTime='" + lastSyncDateTime + '\'' +
                ", title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", checked=" + checked +
                ", isOutOfSync=" + isOutOfSync +
                ", isSelected=" + isSelected +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid, downloadingStatus, lastSyncDateTime, title, detail, checked, isOutOfSync, isSelected);
    }

}

