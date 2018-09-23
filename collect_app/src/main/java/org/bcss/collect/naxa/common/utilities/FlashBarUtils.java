package org.bcss.collect.naxa.common.utilities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;


import org.bcss.collect.android.R;
import org.bcss.collect.naxa.onboarding.DownloadActivity;

import java.util.concurrent.TimeUnit;

import de.mateware.snacky.Snacky;

public class FlashBarUtils {

    private static final int LONG_DURATION_MS = (int) TimeUnit.SECONDS.toMillis(5);


    public static void showOutOfSyncMsg(@NonNull int outOfSyncUid, @NonNull Activity context, @NonNull String message) {
        if (message.isEmpty()) {
            return;
        }


        Flashbar.Builder bar = new Flashbar.Builder(context)
                .gravity(Flashbar.Gravity.BOTTOM)
                .title("Out of sync")
                .message(message)
                .castShadow(false)
                .titleColorRes(R.color.white)
                .enableSwipeToDismiss()
                .backgroundDrawable(R.drawable.flashbar_frame)
                .icon(R.drawable.information_outline)
                .iconColorFilterRes(R.color.white)
                .showIcon()
                .positiveActionTextColorRes(R.color.colorGreenPrimaryLight)
                .positiveActionText("Resolve")
                .positiveActionTapListener(new Flashbar.OnActionTapListener() {
                    @Override
                    public void onActionTapped(Flashbar flashbar) {
                        DownloadActivity.start(context, outOfSyncUid);
                    }
                })
                .enterAnimation(FlashAnim.with(context)
                        .animateBar()
                        .duration(400)
                        .alpha()
                        .overshoot())
                .exitAnimation(FlashAnim.with(context)
                        .animateBar()
                        .duration(750)
                        .accelerateDecelerate());


        new Handler().postDelayed(() -> bar.build().show(), 2000);


    }


    public static void showErrorFlashbar(@NonNull Activity context, @NonNull String message) {
        showFlashbar(context, message, false);
    }

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
