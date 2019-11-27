/*
 * Copyright 2017 Nafundi
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

package org.fieldsight.naxa.preferences;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.bcss.collect.android.R;
import org.odk.collect.android.fragments.dialogs.ResetSettingsResultDialog;
import org.odk.collect.android.preferences.AdminPreferencesActivity;
import org.odk.collect.android.utilities.ResetUtility;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.odk.collect.android.fragments.dialogs.ResetSettingsResultDialog.RESET_SETTINGS_RESULT_DIALOG_TAG;
import static org.odk.collect.android.utilities.ResetUtility.ResetAction.RESET_PREFERENCES;

;

public class FieldSightResetDialogPreference extends DialogPreference implements CompoundButton.OnCheckedChangeListener, ResetSettingsResultDialog.ResetSettingsResultDialogListener {
    private CheckBox instances;
    private CheckBox forms;
    private CheckBox cache;
    private CheckBox flaggedForms;
    private ProgressDialog progressDialog;

    public FieldSightResetDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.fieldsight_reset_dialog_layout);
    }

    @Override
    public void onBindDialogView(View view) {
        instances = view.findViewById(R.id.instances);
        forms = view.findViewById(R.id.forms);
        cache = view.findViewById(R.id.cache);
        flaggedForms = view.findViewById(R.id.flagged_forms);
        instances.setOnCheckedChangeListener(this);
        forms.setOnCheckedChangeListener(this);
        flaggedForms.setOnCheckedChangeListener(this);
        cache.setOnCheckedChangeListener(this);
        super.onBindDialogView(view);
    }

    @Override
    public void showDialog(Bundle bundle) {
        super.showDialog(bundle);
        adjustResetButtonAccessibility();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            resetSelected();
        }
    }

    private void resetSelected() {
        final List<Integer> resetActions = new ArrayList<>();

        if (instances.isChecked()) {
            resetActions.add(ResetUtility.ResetAction.RESET_INSTANCES);
        }
        if (forms.isChecked()) {
            resetActions.add(ResetUtility.ResetAction.RESET_FORMS);
        }
        if (cache.isChecked()) {
            resetActions.add(ResetUtility.ResetAction.RESET_CACHE);
        }
        if (flaggedForms.isChecked()) {
            resetActions.add(FieldSightResetUtility.FieldSightResetActions.RESET_FLAGGED_SUBMISSIONS);
        }


        if (!resetActions.isEmpty()) {
            showProgressDialog();
            Runnable runnable = () -> {
                new FieldSightResetUtility().resetAsync(getContext(), resetActions, failedResetActions -> {
                    hideProgressDialog();
                    handleResult(resetActions, failedResetActions);
                });
            };
            new Thread(runnable).start();
        }
    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(getContext(),
                getContext().getString(R.string.please_wait),
                getContext().getString(R.string.reset_in_progress),
                true);
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    private void handleResult(final List<Integer> resetActions, List<Integer> failedResetActions) {
        final StringBuilder resultMessage = new StringBuilder();
        for (int action : resetActions) {
            switch (action) {
                case RESET_PREFERENCES:
                    if (failedResetActions.contains(action)) {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_settings_result),
                                getContext().getString(R.string.error_occured)));
                    } else {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_settings_result),
                                getContext().getString(R.string.success)));
                    }
                    break;
                case ResetUtility.ResetAction.RESET_INSTANCES:
                    if (failedResetActions.contains(action)) {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_saved_forms_result),
                                getContext().getString(R.string.error_occured)));
                    } else {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_saved_forms_result),
                                getContext().getString(R.string.success)));
                    }
                    break;
                case ResetUtility.ResetAction.RESET_FORMS:
                    if (failedResetActions.contains(action)) {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_blank_forms_result),
                                getContext().getString(R.string.error_occured)));
                    } else {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_blank_forms_result),
                                getContext().getString(R.string.success)));
                    }
                    break;
                case ResetUtility.ResetAction.RESET_CACHE:
                    if (failedResetActions.contains(action)) {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_cache_result),
                                getContext().getString(R.string.error_occured)));
                    } else {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_cache_result),
                                getContext().getString(R.string.success)));
                    }
                    break;
                case ResetUtility.ResetAction.RESET_LAYERS:
                    if (failedResetActions.contains(action)) {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_layers_result),
                                getContext().getString(R.string.error_occured)));
                    } else {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_layers_result),
                                getContext().getString(R.string.success)));
                    }
                    break;
                case ResetUtility.ResetAction.RESET_OSM_DROID:
                    if (failedResetActions.contains(action)) {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_osm_tiles_result),
                                getContext().getString(R.string.error_occured)));
                    } else {
                        resultMessage.append(String.format(getContext().getString(R.string.reset_osm_tiles_result),
                                getContext().getString(R.string.success)));
                    }
                    break;
            }
            if (resetActions.indexOf(action) < resetActions.size() - 1) {
                resultMessage.append("\n\n");
            }
        }
        if (!((SettingsActivity) getContext()).isInstanceStateSaved()) {
            ((SettingsActivity) getContext()).runOnUiThread(() -> {
                if (resetActions.contains(RESET_PREFERENCES)) {
                    ((SettingsActivity) getContext()).recreate();
                }
                ResetSettingsResultDialog resetSettingsResultDialog = ResetSettingsResultDialog.newInstance(String.valueOf(resultMessage));
                try {
                    resetSettingsResultDialog.show(((SettingsActivity) getContext()).getSupportFragmentManager(), RESET_SETTINGS_RESULT_DIALOG_TAG);
                } catch (ClassCastException e) {
                    Timber.i(e);
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        adjustResetButtonAccessibility();
    }

    private void adjustResetButtonAccessibility() {
        if (instances.isChecked() || forms.isChecked() || cache.isChecked() || flaggedForms.isChecked()) {
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).getCurrentTextColor());
        } else {
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getPartiallyTransparentColor(((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).getCurrentTextColor()));
        }
    }

    private int getPartiallyTransparentColor(int color) {
        return Color.argb(150, Color.red(color), Color.green(color), Color.blue(color));
    }

    @Override
    public void onDialogClosed() {

    }
}