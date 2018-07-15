package org.odk.collect.naxa.onboarding;

import org.odk.collect.naxa.common.Constant;

import java.util.HashMap;
import java.util.Hashtable;

import com.google.common.base.Objects;

public class DownloadableItem {

    private String uid;
    private String downloadingStatus;
    private String lastSyncDateTime;


    private String title;
    private String detail;

    public DownloadableItem(String uid, String downloadingStatus, String lastSyncDateTime, String title, String detail) {
        this.uid = uid;
        this.downloadingStatus = downloadingStatus;
        this.lastSyncDateTime = lastSyncDateTime;
        this.title = title;
        this.detail = detail;
    }

    public String getDownloadingStatus() {
        return downloadingStatus;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return downloadingStatus;
    }

    public String getLastSyncDateTime() {
        return lastSyncDateTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DownloadableItem)) return false;
        DownloadableItem that = (DownloadableItem) o;
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
