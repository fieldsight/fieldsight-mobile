package org.bcss.collect.naxa.onboarding;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.common.base.Objects;

@Deprecated
@Entity(tableName = "sync")
public class SyncableItem {

    @PrimaryKey
    private int uid;
    private int downloadingStatus;
    private String lastSyncDateTime;
    private String title;
    private String detail;
    private boolean checked;
    private boolean progressStatus;

    @Ignore
    private boolean isOutOfSync;

    @Ignore
    private boolean isSelected;

    public SyncableItem() {

    }

    @Ignore
    public SyncableItem(int uid, int downloadingStatus, String lastSyncDateTime, String title, String detail) {
        this.uid = uid;
        this.downloadingStatus = downloadingStatus;
        this.lastSyncDateTime = lastSyncDateTime;
        this.title = title;
        this.detail = detail;
        this.isSelected = false;
        this.checked = true;
        this.progressStatus = false;

    }

    @Ignore
    public SyncableItem(int uid, int downloadingStatus, String title, String detail) {
        this.uid = uid;
        this.downloadingStatus = downloadingStatus;

        this.title = title;
        this.detail = detail;
        this.isSelected = false;
        this.checked = true;
        this.progressStatus = false;

    }

    public boolean isOutOfSync() {
        return isOutOfSync;
    }

    public void setOutOfSync(boolean outOfSync) {
        isOutOfSync = outOfSync;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setProgressStatus(boolean progressStatus) {
        this.progressStatus = progressStatus;
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

    public boolean isProgressStatus() {
        return progressStatus;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyncableItem that = (SyncableItem) o;
        return uid == that.uid &&
                downloadingStatus == that.downloadingStatus &&
                checked == that.checked &&
                progressStatus == that.progressStatus &&
                isOutOfSync == that.isOutOfSync &&
                isSelected == that.isSelected &&
                Objects.equal(lastSyncDateTime, that.lastSyncDateTime) &&
                Objects.equal(title, that.title) &&
                Objects.equal(detail, that.detail);
    }

    @Override
    public String toString() {
        return "SyncableItem{" +
                "uid=" + uid +
                ", downloadingStatus=" + downloadingStatus +
                ", lastSyncDateTime='" + lastSyncDateTime + '\'' +
                ", title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", checked=" + checked +
                ", progressStatus=" + progressStatus +
                ", isOutOfSync=" + isOutOfSync +
                ", isSelected=" + isSelected +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid, downloadingStatus, lastSyncDateTime, title, detail, checked, progressStatus, isOutOfSync, isSelected);
    }

    public static void init() {

    }


}
