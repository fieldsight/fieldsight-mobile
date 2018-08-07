package org.bcss.collect.naxa.common.event;

import org.bcss.collect.naxa.onboarding.DownloadProgress;

import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_UPDATE;

/**
 * Created by nishon on 2/8/18.
 */

public class DataSyncEvent {

    private String event;
    private int uid;
    private DownloadProgress downloadProgress;


    public DataSyncEvent(int uid, String event) {
        this.uid = uid;
        this.event = event;
    }



    public DataSyncEvent(int uid, DownloadProgress downloadProgress) {
        this.event = EVENT_UPDATE;
        this.uid = uid;
        this.downloadProgress = downloadProgress;
    }

    public DownloadProgress getDownloadProgress() {
        return downloadProgress;
    }

    public String getEvent() {
        return event;
    }


    public int getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return "DataSyncEvent{" +
                "event='" + event + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

    public static final class EventStatus {
        public static final String EVENT_START = "start";
        public static final String EVENT_END = "end";
        public static final String EVENT_ERROR = "error";
        public static final String EVENT_UPDATE = "update";
    }

}
