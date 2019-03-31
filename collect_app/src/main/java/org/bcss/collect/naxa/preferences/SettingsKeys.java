package org.bcss.collect.naxa.preferences;

import java.util.HashMap;

public final class SettingsKeys {

    public static final String KEY_NOTIFICATION_TIME_DAILY = "notification_time_daily";
    public static final String KEY_NOTIFICATION_TIME_WEEKLY = "notification_time_weekly";
    public static final String KEY_NOTIFICATION_TIME_MONTHLY = "notification_time_monthly";
    public static final String KEY_NOTIFICATION_SAMPLE = "notification_sample";

    public final static HashMap<String, String> defaultvalues = new HashMap<>();

    static {
        defaultvalues.put(KEY_NOTIFICATION_TIME_DAILY, "8:00");
        defaultvalues.put(KEY_NOTIFICATION_TIME_WEEKLY, "0");
        defaultvalues.put(KEY_NOTIFICATION_TIME_MONTHLY, "1");
    }
}
