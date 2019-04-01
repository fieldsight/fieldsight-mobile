package org.bcss.collect.naxa.preferences;

import java.util.HashMap;

public final class SettingsKeys {

    public static final String KEY_NOTIFICATION_TIME_DAILY = "notification_time_daily";
    public static final String KEY_NOTIFICATION_TIME_WEEKLY = "notification_time_weekly";
    public static final String KEY_NOTIFICATION_TIME_MONTHLY = "notification_time_monthly";
    public static final String KEY_NOTIFICATION_SWITCH_DAILY = "switch_notification_daily";
    public static final String KEY_NOTIFICATION_SWITCH_WEEKLY = "switch_notification_weekly";
    public static final String KEY_NOTIFICATION_SWITCH_MONTHLY = "switch_notification_monthly";


    public final static HashMap<String, Object> defaultvalues = new HashMap<>();

    static {
        defaultvalues.put(KEY_NOTIFICATION_TIME_DAILY, "8:00");
        defaultvalues.put(KEY_NOTIFICATION_TIME_WEEKLY, 0);
        defaultvalues.put(KEY_NOTIFICATION_TIME_MONTHLY, 0);
        defaultvalues.put(KEY_NOTIFICATION_SWITCH_DAILY, true);
        defaultvalues.put(KEY_NOTIFICATION_SWITCH_WEEKLY, true);
        defaultvalues.put(KEY_NOTIFICATION_SWITCH_MONTHLY, true);
    }
}
