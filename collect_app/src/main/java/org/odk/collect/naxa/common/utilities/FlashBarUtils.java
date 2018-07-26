package org.odk.collect.naxa.common.utilities;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;


import org.odk.collect.android.R;

import java.util.concurrent.TimeUnit;

public class FlashBarUtils {

    private static final int LONG_DURATION_MS = (int) TimeUnit.SECONDS.toMillis(5);


    public static void showFlashBar(@NonNull Activity context, @NonNull String message) {
        if (message.isEmpty()) {
            return;
        }

        Flashbar bar = new Flashbar.Builder(context)
                .gravity(Flashbar.Gravity.BOTTOM)
                .title(message)
                .messageColor(R.color.white)
                .castShadow(false)
                .backgroundColorRes(R.color.colorPrimary)
                .duration(LONG_DURATION_MS)
                .enterAnimation(FlashAnim.with(context)
                        .animateBar()
                        .duration(400)
                        .alpha()
                        .overshoot())
                .exitAnimation(FlashAnim.with(context)
                        .animateBar()
                        .duration(750)
                        .accelerateDecelerate())
                .build();

        bar.show();

    }
}
