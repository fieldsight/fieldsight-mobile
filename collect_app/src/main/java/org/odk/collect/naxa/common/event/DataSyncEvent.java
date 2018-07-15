package org.odk.collect.naxa.common.event;

/**
 * Created by nishon on 2/8/18.
 */

public class DataSyncEvent {

    public static final class EventType {
        public static final String GENERAL_FORM_DEPLOYED = "general_form_deployed";
        public static final String SCHEDULE_FORM_DEPLOYED = "schedule_form_deployed";
        public static final String STAGED_FORM_DEPLOYED = "staged_form_deployed";
        public static final String ME_API_HIT = "me_api_hit";

        public static final String ANY_DATA_SYNC = "any_api_hit";


    }

    @Override
    public String toString() {
        return "DataSyncEvent{" +
                "event='" + event + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public static final class EventStatus {
        public static final String EVENT_START = "start";
        public static final String EVENT_END = "end";
        public static final String EVENT_ERROR = "error";
    }

    private String event;
    private String status;

    public String getStatus() {
        return status;
    }

    public DataSyncEvent(String event, String status) {
        this.event = event;
        this.status = status;
    }

    public String getEvent() {
        return event;
    }
}
