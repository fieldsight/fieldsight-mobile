/*
 * Copyright (C) 2017 Shobhit
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.preferences;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.view.View;

import androidx.annotation.Nullable;

import org.fieldsight.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.tasks.ServerPollingJob;

import static org.odk.collect.android.preferences.AdminKeys.ALLOW_OTHER_WAYS_OF_EDITING_FORM;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_AUTOMATIC_UPDATE;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_AUTOSEND;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_CONSTRAINT_BEHAVIOR;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_GUIDANCE_HINT;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_IMAGE_SIZE;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_PERIODIC_FORM_UPDATES_CHECK;
import static org.odk.collect.android.preferences.PreferencesActivity.INTENT_KEY_ADMIN_MODE;

public class FormManagementPreferences extends BasePreferenceFragment {

    public static FormManagementPreferences newInstance(boolean adminMode) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(INTENT_KEY_ADMIN_MODE, adminMode);

        FormManagementPreferences formManagementPreferences = new FormManagementPreferences();
        formManagementPreferences.setArguments(bundle);

        return formManagementPreferences;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.form_management_preferences);

        initListPref(KEY_PERIODIC_FORM_UPDATES_CHECK);
        initPref(KEY_AUTOMATIC_UPDATE);  
        initListPref(KEY_CONSTRAINT_BEHAVIOR);
        initListPref(KEY_AUTOSEND);
        initListPref(KEY_IMAGE_SIZE);
        initGuidancePrefs();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setTitle(R.string.form_management_preferences);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (toolbar != null) {
            toolbar.setTitle(R.string.general_preferences);
        }
    }

    private void initListPref(String key) {
        final ListPreference pref = (ListPreference) findPreference(key);

        if (pref != null) {
            pref.setSummary(pref.getEntry());
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                int index = ((ListPreference) preference).findIndexOfValue(newValue.toString());
                CharSequence entry = ((ListPreference) preference).getEntries()[index];
                preference.setSummary(entry);

                if (key.equals(KEY_PERIODIC_FORM_UPDATES_CHECK)) {
                    ServerPollingJob.schedulePeriodicJob((String) newValue);

                    Collect.getInstance().logRemoteAnalytics("PreferenceChange", "Periodic form updates check", (String) newValue);

                    if (newValue.equals(getString(R.string.never_value))) {
                        Preference automaticUpdatePreference = findPreference(KEY_AUTOMATIC_UPDATE);
                        if (automaticUpdatePreference != null) {
                            automaticUpdatePreference.setEnabled(false);
                        }
                    }
                    getActivity().recreate();
                }
                return true;
            });
            if (key.equals(KEY_CONSTRAINT_BEHAVIOR)) {
                pref.setEnabled((Boolean) AdminSharedPreferences.getInstance().get(ALLOW_OTHER_WAYS_OF_EDITING_FORM));
            }
        }
    }

    private void initPref(String key) {
        final Preference pref = findPreference(key);

        if (pref != null) {
            if (key.equals(KEY_AUTOMATIC_UPDATE)) {
                String formUpdateCheckPeriod = (String) GeneralSharedPreferences.getInstance()
                        .get(KEY_PERIODIC_FORM_UPDATES_CHECK);

                // Only enable automatic form updates if periodic updates are set
                pref.setEnabled(!formUpdateCheckPeriod.equals(getString(R.string.never_value)));

                pref.setOnPreferenceChangeListener((preference, newValue) -> {
                    Collect.getInstance().logRemoteAnalytics("PreferenceChange", "Automatic form updates", newValue + " " + formUpdateCheckPeriod);

                    return true;
                });
            }
        }
    }

    private void initGuidancePrefs() {
        final ListPreference guidance = (ListPreference) findPreference(KEY_GUIDANCE_HINT);

        if (guidance == null) {
            return;
        }

        guidance.setSummary(guidance.getEntry());
        guidance.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = ((ListPreference) preference).findIndexOfValue(newValue.toString());
                String entry = (String) ((ListPreference) preference).getEntries()[index];
                preference.setSummary(entry);
                return true;
            }
        });
    }

}
