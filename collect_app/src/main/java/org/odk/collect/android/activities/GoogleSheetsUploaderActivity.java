/*
 * Copyright (C) 2017 Nafundi
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

/**
 * Activity to upload completed forms to gme.
 *
 * @author Carl Hartung (chartung@nafundi.com)
 */

package org.odk.collect.android.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.fieldsight.collect.android.R;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.fragments.dialogs.GoogleSheetsUploaderProgressDialog;
import org.odk.collect.android.injection.DaggerUtils;
import org.odk.collect.android.listeners.InstanceUploaderListener;
import org.odk.collect.android.listeners.PermissionListener;
import org.odk.collect.android.preferences.GeneralKeys;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.tasks.InstanceGoogleSheetsUploaderTask;
import org.odk.collect.android.utilities.ArrayUtils;
import org.odk.collect.android.utilities.InstanceUploaderUtils;
import org.odk.collect.android.utilities.PermissionUtils;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.android.utilities.gdrive.GoogleAccountsManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import timber.log.Timber;

import static org.odk.collect.android.fragments.dialogs.GoogleSheetsUploaderProgressDialog.GOOGLE_SHEETS_UPLOADER_PROGRESS_DIALOG_TAG;
import static org.odk.collect.android.utilities.gdrive.GoogleAccountsManager.showSettingsDialog;

public class GoogleSheetsUploaderActivity extends CollectAbstractActivity implements InstanceUploaderListener, GoogleSheetsUploaderProgressDialog.OnSendingFormsCanceledListener {

    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int GOOGLE_USER_DIALOG = 3;
    private static final String ALERT_MSG = "alertmsg";
    private static final String ALERT_SHOWING = "alertshowing";
    private AlertDialog alertDialog;
    private String alertMsg;
    private boolean alertShowing;
    private Long[] instancesToSend;
    private InstanceGoogleSheetsUploaderTask instanceGoogleSheetsUploaderTask;

    @Inject
    GoogleAccountsManager accountsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate: %s", savedInstanceState == null ? "creating" : "re-initializing");

        DaggerUtils.getComponent(this).inject(this);

        // if we start this activity, the following must be true:
        // 1) Google Sheets is selected in preferences
        // 2) A google user is selected

        // default initializers
        alertMsg = getString(R.string.please_wait);
        alertShowing = false;

        setTitle(getString(R.string.send_data));

        // get any simple saved state...
        // resets alert message and showing dialog if the screen is rotated
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ALERT_MSG)) {
                alertMsg = savedInstanceState.getString(ALERT_MSG);
            }
            if (savedInstanceState.containsKey(ALERT_SHOWING)) {
                alertShowing = savedInstanceState.getBoolean(ALERT_SHOWING, false);
            }
        }

        long[] selectedInstanceIDs;

        Intent intent = getIntent();
        selectedInstanceIDs = intent.getLongArrayExtra(FormEntryActivity.KEY_INSTANCES);

        instancesToSend = ArrayUtils.toObject(selectedInstanceIDs);

        // at this point, we don't expect this to be empty...
        if (instancesToSend.length == 0) {
            Timber.e("onCreate: No instances to upload!");
            // drop through --
            // everything will process through OK
        } else {
            Timber.i("onCreate: Beginning upload of %d instances!", instancesToSend.length);
        }

        getResultsFromApi();
    }

    private void runTask() {
        instanceGoogleSheetsUploaderTask = (InstanceGoogleSheetsUploaderTask) getLastCustomNonConfigurationInstance();
        if (instanceGoogleSheetsUploaderTask == null) {
            instanceGoogleSheetsUploaderTask = new InstanceGoogleSheetsUploaderTask(accountsManager);

            // ensure we have a google account selected
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String googleUsername = prefs.getString(
                    GeneralKeys.KEY_SELECTED_GOOGLE_ACCOUNT, null);
            if (googleUsername == null || googleUsername.equals("")) {
                showDialog(GOOGLE_USER_DIALOG);
            } else {
                new AuthorizationChecker().execute();
            }
        } else {
            // it's not null, so we have a task running
            // progress dialog is handled by the system
        }
    }

    /*
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!accountsManager.isAccountSelected()) {
            selectAccount();
        } else if (!isDeviceOnline()) {
            ToastUtils.showShortToast("No network connection available.");
        } else {
            runTask();
        }
    }

    private void selectAccount() {
        new PermissionUtils().requestGetAccountsPermission(this, new PermissionListener() {
            @Override
            public void granted() {
                String account = accountsManager.getLastSelectedAccountIfValid();
                if (!account.isEmpty()) {
                    accountsManager.selectAccount(account);

                    // re-attempt to list google drive files
                    getResultsFromApi();
                } else {
                    showSettingsDialog(GoogleSheetsUploaderActivity.this);
                }
            }

            @Override
            public void denied() {
                finish();
            }
        });
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            Timber.d("AUTHORIZE_DRIVE_ACCESS failed, asking to choose new account:");
            finish();
        }

        switch (requestCode) {
            case REQUEST_AUTHORIZATION:
                dismissProgressDialog();
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        if (instanceGoogleSheetsUploaderTask != null) {
            instanceGoogleSheetsUploaderTask.setUploaderListener(this);
        }
        if (alertShowing) {
            createAlertDialog(alertMsg);
        }
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ALERT_MSG, alertMsg);
        outState.putBoolean(ALERT_SHOWING, alertShowing);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return instanceGoogleSheetsUploaderTask;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (instanceGoogleSheetsUploaderTask != null) {
            if (!instanceGoogleSheetsUploaderTask.isCancelled()) {
                instanceGoogleSheetsUploaderTask.cancel(true);
            }
            instanceGoogleSheetsUploaderTask.setUploaderListener(null);
        }
        finish();
        super.onDestroy();
    }

    @Override
    public void uploadingComplete(HashMap<String, String> result) {
        try {
            dismissProgressDialog();
        } catch (Exception e) {
            // tried to close a dialog not open. don't care.
        }

        if (result == null) {
            // probably got an auth request, so ignore
            return;
        }
        Timber.i("uploadingComplete: Processing results ( %d ) from upload of %d instances!",
                result.size(), instancesToSend.length);

        StringBuilder selection = new StringBuilder();
        Set<String> keys = result.keySet();
        String message;

        if (keys.isEmpty()) {
            message = getString(R.string.no_forms_uploaded);
        } else {
            Iterator<String> it = keys.iterator();

            String[] selectionArgs = new String[keys.size()];
            int i = 0;
            while (it.hasNext()) {
                String id = it.next();
                selection.append(InstanceProviderAPI.InstanceColumns._ID + "=?");
                selectionArgs[i++] = id;
                if (i != keys.size()) {
                    selection.append(" or ");
                }
            }

            try (Cursor results = new InstancesDao().getInstancesCursor(selection.toString(), selectionArgs)) {
                if (results != null && results.getCount() > 0) {
                    message = InstanceUploaderUtils.getUploadResultMessage(results, result);
                } else {
                    message = getString(R.string.no_forms_uploaded);
                }
            }
        }
        createAlertDialog(message.trim());
    }

    @Override
    public void progressUpdate(int progress, int total) {
        alertMsg = getString(R.string.sending_items, String.valueOf(progress), String.valueOf(total));
        GoogleSheetsUploaderProgressDialog progressDialog = getProgressDialog();
        if (progressDialog != null) {
            progressDialog.setMessage(alertMsg);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case GOOGLE_USER_DIALOG:
                AlertDialog.Builder gudBuilder = new AlertDialog.Builder(this);

                gudBuilder.setTitle(getString(R.string.no_google_account));
                gudBuilder.setMessage(getString(R.string.google_set_account));
                gudBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                gudBuilder.setCancelable(false);
                return gudBuilder.create();
        }
        return null;
    }

    private void createAlertDialog(String message) {
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.upload_results));
        alertDialog.setMessage(message);
        DialogInterface.OnClickListener quitListener = (dialog, i) -> {
            switch (i) {
                case DialogInterface.BUTTON1: // ok
                    // always exit this activity since it has no interface
                    alertShowing = false;
                    finish();
                    break;
            }
        };
        alertDialog.setCancelable(false);
        alertDialog.setButton(getString(R.string.ok), quitListener);
        alertShowing = true;
        alertMsg = message;
        alertDialog.show();
    }

    @Override
    public void authRequest(Uri url, HashMap<String, String> doneSoFar) {
        // in interface, but not needed
    }

    private void authorized() {
        GoogleSheetsUploaderProgressDialog.newInstance(alertMsg)
                .show(getSupportFragmentManager(), GOOGLE_SHEETS_UPLOADER_PROGRESS_DIALOG_TAG);

        instanceGoogleSheetsUploaderTask.setUploaderListener(this);
        instanceGoogleSheetsUploaderTask.execute(instancesToSend);
    }

    private void dismissProgressDialog() {
        GoogleSheetsUploaderProgressDialog progressDialog = getProgressDialog();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private GoogleSheetsUploaderProgressDialog getProgressDialog() {
        return (GoogleSheetsUploaderProgressDialog) getSupportFragmentManager().findFragmentByTag(GOOGLE_SHEETS_UPLOADER_PROGRESS_DIALOG_TAG);
    }

    @Override
    public void onSendingFormsCanceled() {
        instanceGoogleSheetsUploaderTask.cancel(true);
        instanceGoogleSheetsUploaderTask.setUploaderListener(null);
        finish();
    }

    private class AuthorizationChecker extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Must be run from a background thread, not the main UI thread.
                if (accountsManager.getToken() != null) {
                    return true;
                }
            } catch (UserRecoverableAuthException e) {
                // Collect is not yet authorized to access current account, so request for authorization
                runOnUiThread(() -> startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION));
            } catch (IOException | GoogleAuthException e) {
                // authorization failed
                runOnUiThread(() -> createAlertDialog(getString(R.string.google_auth_io_exception_msg)));
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                authorized();
            }
        }
    }
}
