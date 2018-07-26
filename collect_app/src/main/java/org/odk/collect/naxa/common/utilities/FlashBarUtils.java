package org.odk.collect.naxa.common.utilities;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;


import org.odk.collect.android.R;

import java.util.concurrent.TimeUnit;

import de.mateware.snacky.Snacky;

public class FlashBarUtils {

    private static final int LONG_DURATION_MS = (int) TimeUnit.SECONDS.toMillis(5);


    public static void showFlashbar(@NonNull Activity context, @NonNull String message, boolean progressIcon) {
        if (message.isEmpty()) {
            return;
        }

        Flashbar.Builder bar = new Flashbar.Builder(context)
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
                        .accelerateDecelerate());

        if (progressIcon) {
            bar.showProgress(Flashbar.ProgressPosition.LEFT);
        }


        bar.build().show();

    }
}
