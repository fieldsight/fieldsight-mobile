package org.fieldsight.naxa.common.utilities;

import android.content.Context;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import android.view.ViewGroup;

import org.fieldsight.collect.android.R;

public class SnackbarHelper {

    static void configSnackbar(Context context, Snackbar snack) {
        addMargins(snack);
        setRoundBordersBg(context, snack);

        ViewCompat.setElevation(snack.getView(), 6f);
    }

    private static void addMargins(Snackbar snack) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snack.getView().getLayoutParams();
        params.setMargins(12, 12, 12, 150);
        snack.getView().setLayoutParams(params);
    }

    private static void setRoundBordersBg(Context context, Snackbar snackbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snackbar.getView().setBackground(context.getDrawable(R.drawable.bg_snackbar));
        } else {
            snackbar.getView().setBackground(ContextCompat.getDrawable(context, R.drawable.bg_snackbar));
        }
    }


}
