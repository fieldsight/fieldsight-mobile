package org.fieldsight.naxa.common.utilities;

import android.app.Activity;

import androidx.annotation.NonNull;

import org.odk.collect.android.utilities.ToastUtils;

public class SnackBarUtils {

    private SnackBarUtils(){

    }

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

        ToastUtils.showLongToast(message);

//
//        try {
//            View rootView = context.getWindow().getDecorView().getRootView();
//            Snackbar snack = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
//            SnackbarHelper.configSnackbar(context, snack);
//            snack.show();
//        } catch (Exception e) {
//            Timber.e(e);
//            ToastUtils.showLongToast(message);
//
//
//        }
    }
}
