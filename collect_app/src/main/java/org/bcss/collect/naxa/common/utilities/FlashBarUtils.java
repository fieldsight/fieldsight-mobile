package org.bcss.collect.naxa.common.utilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.sync.ContentDownloadActivity;
import org.bcss.collect.naxa.v3.network.SyncActivity;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;

import timber.log.Timber;

public class FlashBarUtils {



    public static void showErrorFlashbar(@NonNull Activity context, @NonNull String message) {
        showFlashbar(context, message, false);
    }


    public static void showFlashbar(@NonNull Activity context, @NonNull String message) {
        showFlashbar(context, message, false);
    }

    public static void showFlashbar(@NonNull Activity context, @NonNull String message, boolean progressIcon) {


        if (message.isEmpty()) {
            return;
        }


        try {
            View rootView = context.getWindow().getDecorView().getRootView();
            Snackbar snack = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
            SnackbarHelper.configSnackbar(context, snack);
            snack.show();
        } catch (Exception e) {
            Timber.e(e);
            ToastUtils.showLongToast(message);


        }
    }
}
