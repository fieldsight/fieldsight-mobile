package org.odk.collect.naxa.onboarding;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.common.base.Objects;


@Entity(tableName = "sync")
public class SyncableItems {

    @PrimaryKey
    private int uid;
    private int downloadingStatus;
    private String lastSyncDateTime;
    private String title;
    private String detail;
    private boolean checked;
    private boolean progressStatus;

    @Ignore
    private boolean isSelected;

    public SyncableItems() {

    }

    @Ignore
    public SyncableItems(int uid, int downloadingStatus, String lastSyncDateTime, String title, String detail) {
        this.uid = uid;
        this.downloadingStatus = downloadingStatus;
        this.lastSyncDateTime = lastSyncDateTime;
        this.title = title;
        this.detail = detail;
        this.isSelected = false;
        this.checked = true;
        this.progressStatus = false;
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
        if (!(o instanceof SyncableItems)) return false;
        SyncableItems that = (SyncableItems) o;
        return Objects.equal(getUid(), that.getUid()) &&
                Objects.equal(getDownloadingStatus(), that.getDownloadingStatus()) &&
                Objects.equal(getLastSyncDateTime(), that.getLastSyncDateTime()) &&
                Objects.equal(getTitle(), that.getTitle()) &&
                Objects.equal(getDetail(), that.getDetail());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUid(), getDownloadingStatus(), getLastSyncDateTime(), getTitle(), getDetail());
    }

    public static void init() {

    }


}
