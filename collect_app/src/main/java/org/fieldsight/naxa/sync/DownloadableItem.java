package org.fieldsight.naxa.sync;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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
    private String errorMessage;

    private int syncProgress;
    private int syncTotal;

    @ColumnInfo(name = "sync_message")
    private String syncMessage;

    @ColumnInfo(name = "is_determinate")
    private boolean isDeterminate;

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

    public String getSyncMessage() {
        return syncMessage;
    }

    public void setSyncMessage(String syncMessage) {
        this.syncMessage = syncMessage;
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

    public void setOutOfSync(boolean outOfSync) {
        isOutOfSync = outOfSync;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    public boolean getIsSelected() {
        return isSelected;
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

    public boolean isDeterminate() {
        return isDeterminate;
    }

    public void setDeterminate(boolean determinate) {
        isDeterminate = determinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DownloadableItem that = (DownloadableItem) o;
        return uid == that.uid &&
                downloadingStatus == that.downloadingStatus &&
                checked == that.checked &&
                syncProgress == that.syncProgress &&
                syncTotal == that.syncTotal &&
                isDeterminate == that.isDeterminate &&
                isOutOfSync == that.isOutOfSync &&
                isSelected == that.isSelected &&
                Objects.equal(title, that.title) &&
                Objects.equal(detail, that.detail) &&
                Objects.equal(lastSyncDateTime, that.lastSyncDateTime) &&
                Objects.equal(errorMessage, that.errorMessage) &&
                Objects.equal(syncMessage, that.syncMessage);
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + downloadingStatus;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (detail != null ? detail.hashCode() : 0);
        result = 31 * result + (checked ? 1 : 0);
        result = 31 * result + (lastSyncDateTime != null ? lastSyncDateTime.hashCode() : 0);
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        result = 31 * result + syncProgress;
        result = 31 * result + syncTotal;
        result = 31 * result + (isDeterminate ? 1 : 0);
        result = 31 * result + (isOutOfSync ? 1 : 0);
        result = 31 * result + (isSelected ? 1 : 0);
        return result;
    }
}

