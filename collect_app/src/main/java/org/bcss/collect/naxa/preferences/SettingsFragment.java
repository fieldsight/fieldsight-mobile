package org.bcss.collect.naxa.preferences;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.widget.TimePicker;

import com.evernote.android.job.JobManager;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.naxa.jobs.DailyNotificationJob;
import org.bcss.collect.naxa.jobs.LocalNotificationJob;
import org.bcss.collect.naxa.jobs.MonthlyNotificationJob;
import org.bcss.collect.naxa.jobs.WeeklyNotificationJob;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

import static org.bcss.collect.naxa.preferences.SettingsKeys.KEY_NOTIFICATION_SAMPLE;
import static org.bcss.collect.naxa.preferences.SettingsKeys.KEY_NOTIFICATION_SWITCH_DAILY;
import static org.bcss.collect.naxa.preferences.SettingsKeys.KEY_NOTIFICATION_SWITCH_MONTHLY;
import static org.bcss.collect.naxa.preferences.SettingsKeys.KEY_NOTIFICATION_SWITCH_WEEKLY;
import static org.bcss.collect.naxa.preferences.SettingsKeys.KEY_NOTIFICATION_TIME_DAILY;
import static org.bcss.collect.naxa.preferences.SettingsKeys.KEY_NOTIFICATION_TIME_MONTHLY;
import static org.bcss.collect.naxa.preferences.SettingsKeys.KEY_NOTIFICATION_TIME_WEEKLY;


public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {


    CustomTimePickerDialog customTimePickerDialog = null;
    private SwitchPreference switchPreferenceMonth, switchPreferenceDaily, switchPreferenceWeek;
    String[] weeks = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fieldsight_preferences);
        findPreference(KEY_NOTIFICATION_TIME_DAILY).setOnPreferenceClickListener(this);
        findPreference(KEY_NOTIFICATION_TIME_WEEKLY).setOnPreferenceClickListener(this);
        findPreference(KEY_NOTIFICATION_TIME_MONTHLY).setOnPreferenceClickListener(this);

        findPreference(KEY_NOTIFICATION_SAMPLE).setOnPreferenceClickListener(this);

        setupSharedPreferences();
        setupNotificationToggle();
        setupUpdateButton();


        String[] dailyTime = get(KEY_NOTIFICATION_TIME_DAILY).split(":");
        customTimePickerDialog = new CustomTimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                save(KEY_NOTIFICATION_TIME_DAILY, String.format(Locale.getDefault(), "%d:%d", hourOfDay, minute));
            }
        }, Integer.valueOf(dailyTime[0]), Integer.valueOf(dailyTime[1]));

        getPreferenceScreen().removePreference(findPreference(KEY_NOTIFICATION_TIME_WEEKLY));
        getPreferenceScreen().removePreference(findPreference(KEY_NOTIFICATION_TIME_MONTHLY));
    }

    private void save(String key, String value) {
        SettingsSharedPreferences.getInstance().save(key, value);
    }

    private String get(String key) {
        return (String) SettingsSharedPreferences.getInstance().get(key);
    }

    private void setupUpdateButton() {
        Preference preference = findPreference("app_update");
        preference.setOnPreferenceClickListener(this);
        String title = getString(R.string.app_name).concat(": ").concat(BuildConfig.VERSION_NAME);
        preference.setTitle(title);
        preference.setSummary("Check for update");
    }


    private void setupNotificationToggle() {
        switchPreferenceDaily = (SwitchPreference) findPreference("switch_notification_daily");
        switchPreferenceWeek = (SwitchPreference) findPreference("switch_notification_weekly");
        switchPreferenceMonth = (SwitchPreference) findPreference("switch_notification_monthly");


        switchPreferenceDaily.setSummaryOff(getString(R.string.msg_no_longer_notifcation_receiced));
        switchPreferenceWeek.setSummaryOff(getString(R.string.msg_no_longer_notifcation_receiced));
        switchPreferenceMonth.setSummaryOff(getString(R.string.msg_no_longer_notifcation_receiced));

        switchPreferenceDaily.setSummaryOn(getString(R.string.msg_will_receive_notifications));
        switchPreferenceWeek.setSummaryOn(getString(R.string.msg_will_receive_notifications));
        switchPreferenceMonth.setSummaryOn(getString(R.string.msg_will_receive_notifications));

    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case KEY_NOTIFICATION_TIME_DAILY:
                customTimePickerDialog.show();
                break;
            case KEY_NOTIFICATION_TIME_WEEKLY:
                showWeekPickerDialog();
                break;
            case KEY_NOTIFICATION_TIME_MONTHLY:
                break;

            case KEY_NOTIFICATION_SAMPLE:
                LocalNotificationJob.runJobImmediately();
                break;
        }
        return false;
    }

    private void showWeekPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose an day");

        builder.setItems(weeks, (dialog, which) -> {
            save(KEY_NOTIFICATION_TIME_WEEKLY, String.valueOf(which));
        });
        builder.show();
    }

    private void setupSharedPreferences() {
        SettingsSharedPreferences.getInstance().register(this);

        String time = String.valueOf(SettingsSharedPreferences.getInstance().get(KEY_NOTIFICATION_TIME_DAILY));
        findPreference(KEY_NOTIFICATION_TIME_DAILY).setSummary(formatTime(time));

        int weekIndex = Integer.parseInt(String.valueOf(SettingsSharedPreferences.getInstance().get(KEY_NOTIFICATION_TIME_WEEKLY)));
        findPreference(KEY_NOTIFICATION_TIME_WEEKLY).setSummary(weeks[weekIndex]);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_NOTIFICATION_TIME_DAILY:
                String time = String.valueOf(SettingsSharedPreferences.getInstance().get(KEY_NOTIFICATION_TIME_DAILY));
                findPreference(KEY_NOTIFICATION_TIME_DAILY).setSummary(formatTime(time));
                DailyNotificationJob.schedule();
                break;
            case KEY_NOTIFICATION_TIME_WEEKLY:
                String index = String.valueOf(SettingsSharedPreferences.getInstance().get(KEY_NOTIFICATION_TIME_WEEKLY));
                findPreference(KEY_NOTIFICATION_TIME_WEEKLY).setSummary(weeks[Integer.parseInt(index)]);
            case KEY_NOTIFICATION_SWITCH_DAILY:
                if (sharedPreferences.getBoolean(key, false)) {
                    DailyNotificationJob.schedule();
                } else {
                    cancelTask(KEY_NOTIFICATION_SWITCH_DAILY);
                }
                break;
            case KEY_NOTIFICATION_SWITCH_WEEKLY:
                if (sharedPreferences.getBoolean(key, false)) {
                    WeeklyNotificationJob.schedule();
                } else {
                    cancelTask(KEY_NOTIFICATION_SWITCH_DAILY);
                }
                break;
            case KEY_NOTIFICATION_SWITCH_MONTHLY:
                if (sharedPreferences.getBoolean(key, false)) {
                    MonthlyNotificationJob.schedule();
                } else {
                    cancelTask(KEY_NOTIFICATION_SWITCH_MONTHLY);
                }
                break;
        }
    }

    private void cancelTask(String tag) {
        JobManager.instance().cancelAllForTag(tag);
    }

    private String formatTime(String time) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.getDefault());
            final Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("K:mm a", Locale.getDefault()).format(dateObj);
        } catch (ParseException e) {
            Timber.e(e);
        }

        return time;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SettingsSharedPreferences.getInstance().unregister(this);
    }
}