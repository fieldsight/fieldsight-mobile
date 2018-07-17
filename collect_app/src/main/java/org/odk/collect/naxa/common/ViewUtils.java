package org.odk.collect.naxa.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.odk.collect.android.application.Collect;

import static org.odk.collect.naxa.network.APIEndpoint.BASE_URL;

public final class ViewUtils {
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void showOrHide(TextView textView, String text) {
        if (android.text.TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
            return;
        }

        textView.setVisibility(View.VISIBLE);
        text = " " + text + " ";
        textView.setText(text);


    }

    public static void setButtonTint(FloatingActionButton button, ColorStateList tint) {
        button.setBackgroundTintList(tint);
    }


    public static DrawableRequestBuilder<String> loadImage(@NonNull String imagePath) {

        return Glide
                .with(Collect.getInstance())
                .load(BASE_URL + imagePath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .crossFade();
    }

}

