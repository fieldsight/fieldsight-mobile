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

package org.bcss.collect.android.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.injection.config.AppComponent;
import org.bcss.collect.android.R;
import org.bcss.collect.android.utilities.LocaleHelper;
import org.bcss.collect.android.utilities.ThemeUtils;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.utilities.FlashBarUtils;

import timber.log.Timber;

import static org.bcss.collect.android.utilities.PermissionUtils.checkIfStoragePermissionsGranted;
import static org.bcss.collect.android.utilities.PermissionUtils.finishAllActivities;
import static org.bcss.collect.android.utilities.PermissionUtils.isEntryPointActivity;

public abstract class CollectAbstractActivity extends AppCompatActivity {

    private boolean isInstanceStateSaved;
    protected ThemeUtils themeUtils;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        themeUtils = new ThemeUtils(this);
        setTheme(this instanceof FormEntryActivity ? themeUtils.getFormEntryActivityTheme() : themeUtils.getAppTheme());
        super.onCreate(savedInstanceState);

        /**
         * If a user has revoked the storage permission then this check ensures the app doesn't quit unexpectedly and
         * informs the user of the implications of their decision before exiting. The app can't function with these permissions
         * so if a user wishes to grant them they just restart.
         *
         * This code won't run on activities that are entry points to the app because those activities
         * are able to handle permission checks and requests by themselves.
         */
        if (!checkIfStoragePermissionsGranted(this) && !isEntryPointActivity(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);

            builder.setTitle(R.string.storage_runtime_permission_denied_title)
                    .setMessage(R.string.storage_runtime_permission_denied_desc)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        finishAllActivities(this);
                    })
                    .setIcon(R.drawable.sd)
                    .setCancelable(false)
                    .show();
        }
    }

    public AppComponent getComponent() {
        return Collect.getInstance().getComponent();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        isInstanceStateSaved = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        isInstanceStateSaved = true;
        super.onSaveInstanceState(outState);
    }

    public boolean isInstanceStateSaved() {
        return isInstanceStateSaved;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(new LocaleHelper().updateLocale(base));
    }

    public void showProgress() {
        try {
            RelativeLayout relativeLayout = findViewById(R.id.fl_toolbar_progress_wrapper);
            if (relativeLayout != null) {
                relativeLayout.setVisibility(View.VISIBLE);
            } else {
                progressDialog = DialogFactory.createProgressDialogHorizontal(this, getString(R.string.please_wait));
                progressDialog.show();
            }
        } catch (Exception e) {
            FlashBarUtils.showFlashbar(this, e.getMessage());
            Timber.e(e);
        }

    }

    public void hideProgress() {
        try {
            RelativeLayout relativeLayout = findViewById(R.id.fl_toolbar_progress_wrapper);
            if (relativeLayout != null) {
                relativeLayout.setVisibility(View.GONE);
            } else {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        } catch (Exception e) {
            FlashBarUtils.showFlashbar(this, e.getMessage());
            Timber.e(e);
        }
    }
}
