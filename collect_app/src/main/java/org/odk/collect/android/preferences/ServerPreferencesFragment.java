/*
 * Copyright 2017 Shobhit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.odk.collect.android.preferences;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.fieldsight.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.http.CollectServerClient;
import org.odk.collect.android.listeners.OnBackPressedListener;
import org.odk.collect.android.listeners.PermissionListener;
import org.odk.collect.android.injection.DaggerUtils;

import org.odk.collect.android.preferences.filters.ControlCharacterFilter;
import org.odk.collect.android.preferences.filters.WhitespaceFilter;
import org.odk.collect.android.utilities.FileUtils;
import org.odk.collect.android.utilities.PermissionUtils;
import org.odk.collect.android.utilities.PlayServicesUtil;
import org.odk.collect.android.utilities.SoftKeyboardUtils;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.android.utilities.Validator;
import org.odk.collect.android.utilities.gdrive.GoogleAccountsManager;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_FORMLIST_URL;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_PROTOCOL;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_SELECTED_GOOGLE_ACCOUNT;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_SMS_GATEWAY;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_SMS_PREFERENCE;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_SUBMISSION_URL;
import static org.odk.collect.android.preferences.GeneralKeys.KEY_TRANSPORT_PREFERENCE;
import static org.odk.collect.android.utilities.DialogUtils.showDialog;
//import android.preference.ListPreference;
//import static org.odk.collect.android.preferences.GeneralKeys.KEY_SUBMISSION_TRANSPORT_TYPE;

//import static org.odk.collect.android.utilities.DialogUtils.showDialog;

public class ServerPreferencesFragment extends BasePreferenceFragment implements
        View.OnTouchListener, OnBackPressedListener {

    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final String KNOWN_URL_LIST = "knownUrlList";
    protected EditTextPreference serverUrlPreference;
    protected EditTextPreference usernamePreference;
    protected EditTextPreference passwordPreference;
    //protected ExtendedEditTextPreference smsGatewayPreference;
    protected EditTextPreference submissionUrlPreference;
    protected EditTextPreference formListUrlPreference;
    private ListPopupWindow listPopupWindow;
    private List<String> urlList;
    private Preference selectedGoogleAccountPreference;
    private boolean allowClickSelectedGoogleAccountPreference = true;

    @Inject
    CollectServerClient collectServerClient;

    @Inject
    GoogleAccountsManager accountsManager;

    /*
    private ListPreference transportPreference;
    private ExtendedPreferenceCategory smsPreferenceCategory;
    */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.getComponent(activity).inject(this);

        ((PreferencesActivity) activity).setOnBackPressedListener(this);
    }

    public void addAggregatePreferences() {
        if (!new AggregatePreferencesAdder(this).add()) {
            return;
        }

        serverUrlPreference = (EditTextPreference) findPreference(
                GeneralKeys.KEY_SERVER_URL);
        usernamePreference = (EditTextPreference) findPreference(GeneralKeys.KEY_USERNAME);
        passwordPreference = (EditTextPreference) findPreference(GeneralKeys.KEY_PASSWORD);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String urlListString = prefs.getString(KNOWN_URL_LIST, "");
        if (urlListString.isEmpty()) {
            urlList = new ArrayList<>();
        } else {
            urlList =
                    new Gson().fromJson(urlListString, new TypeToken<List<String>>() {
                    }.getType());
        }
        if (urlList.isEmpty()) {
            addUrlToPreferencesList(getString(R.string.default_server_url), prefs);
        }

        urlDropdownSetup();

        // TODO: use just 'serverUrlPreference.getEditText().setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);' once minSdkVersion is >= 21
        serverUrlPreference.getEditText().setCompoundDrawablesWithIntrinsicBounds(null, null,
                AppCompatResources.getDrawable(getActivity(), R.drawable.ic_arrow_drop_down), null);
        serverUrlPreference.getEditText().setOnTouchListener(this);
        serverUrlPreference.setOnPreferenceChangeListener(createChangeListener());
        serverUrlPreference.setSummary(serverUrlPreference.getText());
        serverUrlPreference.getEditText().setFilters(
                new InputFilter[]{new ControlCharacterFilter(), new WhitespaceFilter()});

        usernamePreference.setOnPreferenceChangeListener(createChangeListener());
        usernamePreference.setSummary(usernamePreference.getText());
        usernamePreference.getEditText().setFilters(
                new InputFilter[]{new ControlCharacterFilter()});

        passwordPreference.setOnPreferenceChangeListener(createChangeListener());
        maskPasswordSummary(passwordPreference.getText());
        passwordPreference.getEditText().setFilters(
                new InputFilter[]{new ControlCharacterFilter()});

        //setupTransportPreferences();
        getPreferenceScreen().removePreference(findPreference(KEY_TRANSPORT_PREFERENCE));
        getPreferenceScreen().removePreference(findPreference(KEY_SMS_PREFERENCE));
    }

    /*
    public void setupTransportPreferences() {
        transportPreference = (ListPreference) findPreference(KEY_SUBMISSION_TRANSPORT_TYPE);
        transportPreference.setOnPreferenceChangeListener(createTransportChangeListener());
        transportPreference.setSummary(transportPreference.getEntry());

        smsPreferenceCategory = (ExtendedPreferenceCategory) findPreference(KEY_SMS_PREFERENCE);

        smsGatewayPreference = (ExtendedEditTextPreference) findPreference(KEY_SMS_GATEWAY);

        smsGatewayPreference.setOnPreferenceChangeListener(createChangeListener());
        smsGatewayPreference.setSummary(smsGatewayPreference.getText());
        smsGatewayPreference.getEditText().setFilters(
                new InputFilter[]{new ControlCharacterFilter()});

        Transport transport = Transport.fromPreference(GeneralSharedPreferences.getInstance().get(KEY_SUBMISSION_TRANSPORT_TYPE));

        boolean smsEnabled = !transport.equals(Transport.Internet);
        smsGatewayPreference.setEnabled(smsEnabled);
        smsPreferenceCategory.setEnabled(smsEnabled);
    }

    private Preference.OnPreferenceChangeListener createTransportChangeListener() {
        return (preference, newValue) -> {
            if (preference.getKey().equals(KEY_SUBMISSION_TRANSPORT_TYPE)) {
                String stringValue = (String) newValue;
                ListPreference pref = (ListPreference) preference;
                String oldValue = pref.getValue();

                if (!newValue.equals(oldValue)) {
                    pref.setValue(stringValue);

                    Transport transport = Transport.fromPreference(newValue);

                    boolean smsEnabled = !transport.equals(Transport.Internet);
                    smsGatewayPreference.setEnabled(smsEnabled);
                    smsPreferenceCategory.setEnabled(smsEnabled);

                    if (transport.equals(Transport.Internet)) {
                        transportPreference.setSummary(R.string.transport_type_internet);
                    } else if (transport.equals(Transport.Sms)) {
                        transportPreference.setSummary(R.string.transport_type_sms);
                    } else {
                        transportPreference.setSummary(R.string.transport_type_both);
                    }
                }
            }
            return true;
        };
    }
    */

    public void addGooglePreferences() {
        addPreferencesFromResource(R.xml.google_preferences);
        selectedGoogleAccountPreference = findPreference(KEY_SELECTED_GOOGLE_ACCOUNT);

        EditTextPreference googleSheetsUrlPreference = (EditTextPreference) findPreference(
                GeneralKeys.KEY_GOOGLE_SHEETS_URL);
        googleSheetsUrlPreference.setOnPreferenceChangeListener(createChangeListener());

        String currentGoogleSheetsURL = googleSheetsUrlPreference.getText();
        if (currentGoogleSheetsURL != null && currentGoogleSheetsURL.length() > 0) {
            googleSheetsUrlPreference.setSummary(currentGoogleSheetsURL + "\n\n"
                    + getString(R.string.google_sheets_url_hint));
        }

        googleSheetsUrlPreference.getEditText().setFilters(new InputFilter[]{
                new ControlCharacterFilter(), new WhitespaceFilter()
        });
        initAccountPreferences();
        //setupTransportPreferences();
        getPreferenceScreen().removePreference(findPreference(KEY_TRANSPORT_PREFERENCE));
        getPreferenceScreen().removePreference(findPreference(KEY_SMS_PREFERENCE));
    }

    public void addOtherPreferences() {
        addAggregatePreferences();
        addPreferencesFromResource(R.xml.other_preferences);

        formListUrlPreference = (EditTextPreference) findPreference(KEY_FORMLIST_URL);
        submissionUrlPreference = (EditTextPreference) findPreference(KEY_SUBMISSION_URL);

        InputFilter[] filters = {new ControlCharacterFilter(), new WhitespaceFilter()};

        serverUrlPreference.getEditText().setFilters(filters);

        formListUrlPreference.setOnPreferenceChangeListener(createChangeListener());
        formListUrlPreference.setSummary(formListUrlPreference.getText());
        formListUrlPreference.getEditText().setFilters(filters);

        submissionUrlPreference.setOnPreferenceChangeListener(createChangeListener());
        submissionUrlPreference.setSummary(submissionUrlPreference.getText());
        submissionUrlPreference.getEditText().setFilters(filters);
    }

    public void initAccountPreferences() {
        selectedGoogleAccountPreference.setSummary(accountsManager.getLastSelectedAccountIfValid());
        selectedGoogleAccountPreference.setOnPreferenceClickListener(preference -> {
            if (allowClickSelectedGoogleAccountPreference) {
                if (PlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
                    allowClickSelectedGoogleAccountPreference = false;
                    requestAccountsPermission();
                } else {
                    PlayServicesUtil.showGooglePlayServicesAvailabilityErrorDialog(getActivity());
                }
            }
            return true;
        });
    }

    private void requestAccountsPermission() {
        new PermissionUtils().requestGetAccountsPermission(getActivity(), new PermissionListener() {
            @Override
            public void granted() {
                Intent intent = accountsManager.getAccountChooserIntent();
                startActivityForResult(intent, REQUEST_ACCOUNT_PICKER);
            }

            @Override
            public void denied() {
                allowClickSelectedGoogleAccountPreference = true;
            }
        });
    }

    private void addUrlToPreferencesList(String url, SharedPreferences prefs) {
        urlList.add(0, url);
        String urlListString = new Gson().toJson(urlList);
        prefs
                .edit()
                .putString(KNOWN_URL_LIST, urlListString)
                .apply();
    }

    private void urlDropdownSetup() {
        listPopupWindow = new ListPopupWindow(getActivity());
        setupUrlDropdownAdapter();
        listPopupWindow.setAnchorView(serverUrlPreference.getEditText());
        listPopupWindow.setModal(true);
        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            serverUrlPreference.getEditText().setText(urlList.get(position));
            listPopupWindow.dismiss();
        });
    }

    private void setupUrlDropdownAdapter() {
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, urlList);
        listPopupWindow.setAdapter(adapter);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() >= (v.getWidth() - ((EditText) v)
                    .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                SoftKeyboardUtils.hideSoftKeyboard(v);
                listPopupWindow.show();
                return true;
            }
        }
        return false;
    }

    private Preference.OnPreferenceChangeListener createChangeListener() {
        return (preference, newValue) -> {
            switch (preference.getKey()) {
                case GeneralKeys.KEY_SERVER_URL:

                    String url = newValue.toString();

                    // remove all trailing "/"s
                    while (url.endsWith("/")) {
                        url = url.substring(0, url.length() - 1);
                    }

                    if (Validator.isUrlValid(url)) {
                        sendAnalyticsEvent(url);

                        preference.setSummary(newValue.toString());
                        SharedPreferences prefs = PreferenceManager
                                .getDefaultSharedPreferences(getActivity().getApplicationContext());
                        String urlListString = prefs.getString(KNOWN_URL_LIST, "");

                        urlList =
                                new Gson().fromJson(urlListString,
                                        new TypeToken<List<String>>() {
                                        }.getType());

                        if (!urlList.contains(url)) {
                            // We store a list with at most 5 elements
                            if (urlList.size() == 5) {
                                urlList.remove(4);
                            }
                            addUrlToPreferencesList(url, prefs);
                            setupUrlDropdownAdapter();
                        }
                    } else {
                        ToastUtils.showShortToast(R.string.url_error);
                        return false;
                    }
                    break;

                case GeneralKeys.KEY_USERNAME:
                    String username = newValue.toString();

                    // do not allow leading and trailing whitespace
                    if (!username.equals(username.trim())) {
                        ToastUtils.showShortToast(R.string.username_error_whitespace);
                        return false;
                    }

                    preference.setSummary(username);
                    return true;

                case GeneralKeys.KEY_PASSWORD:
                    String pw = newValue.toString();

                    // do not allow leading and trailing whitespace
                    if (!pw.equals(pw.trim())) {
                        ToastUtils.showShortToast(R.string.password_error_whitespace);
                        return false;
                    }

                    maskPasswordSummary(pw);
                    break;

                case GeneralKeys.KEY_GOOGLE_SHEETS_URL:
                    url = newValue.toString();

                    // remove all trailing "/"s
                    while (url.endsWith("/")) {
                        url = url.substring(0, url.length() - 1);
                    }

                    if (Validator.isUrlValid(url)) {
                        preference.setSummary(url + "\n\n" + getString(R.string.google_sheets_url_hint));
                    } else if (url.length() == 0) {
                        preference.setSummary(getString(R.string.google_sheets_url_hint));
                    } else {
                        ToastUtils.showShortToast(R.string.url_error);
                        return false;
                    }
                    break;

                case KEY_SMS_GATEWAY:
                    String phoneNumber = newValue.toString();

                    if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
                        ToastUtils.showShortToast(getString(R.string.sms_invalid_phone_number));
                        return false;
                    }

                    preference.setSummary(phoneNumber);
                    break;
                case KEY_FORMLIST_URL:
                case KEY_SUBMISSION_URL:
                    preference.setSummary(newValue.toString());
                    break;
            }
            return true;
        };
    }

    /**
     * Remotely log the URL scheme, whether the URL is on one of 3 common hosts, and a URL hash.
     * This will help inform decisions on whether or not to allow insecure server configurations
     * (HTTP) and on which hosts to strengthen support for.
     *
     * @param url the URL that the server setting has just been set to
     */
    private void sendAnalyticsEvent(String url) {
        String upperCaseURL = url.toUpperCase(Locale.ENGLISH);
        String scheme = upperCaseURL.split(":")[0];

        String host = "Other";
        if (upperCaseURL.contains("APPSPOT")) {
            host = "Appspot";
        } else if (upperCaseURL.contains("KOBOTOOLBOX.ORG") ||
                upperCaseURL.contains("HUMANITARIANRESPONSE.INFO")) {
            host = "Kobo";
        } else if (upperCaseURL.contains("ONA.IO")) {
            host = "Ona";
        }

        String urlHash = FileUtils.getMd5Hash(
                new ByteArrayInputStream(url.getBytes()));

        Collect.getInstance().logRemoteAnalytics("SetServer", scheme + " " + host, urlHash);
    }

    private void maskPasswordSummary(String password) {
        passwordPreference.setSummary(password != null && password.length() > 0
                ? "********"
                : "");
    }

    protected void setDefaultAggregatePaths() {
        GeneralSharedPreferences sharedPreferences = GeneralSharedPreferences.getInstance();
        sharedPreferences.reset(KEY_FORMLIST_URL);
        sharedPreferences.reset(KEY_SUBMISSION_URL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    accountsManager.selectAccount(accountName);
                    selectedGoogleAccountPreference.setSummary(accountName);
                }
                allowClickSelectedGoogleAccountPreference = true;
                break;
        }
    }

    /**
     * Shows a dialog if SMS submission is enabled but the phone number isn't set.
     */
    /*
    private void runSmsPhoneNumberValidation() {
        Transport transport = Transport.fromPreference(GeneralSharedPreferences.getInstance().get(KEY_SUBMISSION_TRANSPORT_TYPE));

        if (!transport.equals(Transport.Internet)) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String gateway = settings.getString(KEY_SMS_GATEWAY, null);

            if (!PhoneNumberUtils.isGlobalPhoneNumber(gateway)) {

                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(getString(R.string.sms_invalid_phone_number))
                        .setMessage(R.string.sms_invalid_phone_number_description)
                        .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                        .create();

                showDialog(alertDialog, getActivity());
            } else {
                runGoogleAccountValidation();
            }
        } else {
            runGoogleAccountValidation();
        }
    }
    */

    private void runGoogleAccountValidation() {
        String account = (String) GeneralSharedPreferences.getInstance().get(KEY_SELECTED_GOOGLE_ACCOUNT);
        String protocol = (String) GeneralSharedPreferences.getInstance().get(KEY_PROTOCOL);

        if (TextUtils.isEmpty(account) && protocol.equals(getString(R.string.protocol_google_sheets))) {

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.missing_google_account_dialog_title)
                    .setMessage(R.string.missing_google_account_dialog_desc)
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                    .create();

            showDialog(alertDialog, getActivity());
        } else {
            continueOnBackPressed();
        }
    }

    private void continueOnBackPressed() {
        ((PreferencesActivity) getActivity()).setOnBackPressedListener(null);
        getActivity().onBackPressed();
    }

    @Override
    public void doBack() {
        runGoogleAccountValidation();
    }
}
