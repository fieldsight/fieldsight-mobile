package org.bcss.collect.naxa.logout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.bcss.collect.android.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;

/*
 * https://github.com/appwise-labs/NoInternetDialog/blob/master/library/app/src/main/java/am/appwise/components/ni/NoInternetDialog.java
 */

public class LogoutDialog extends Dialog implements View.OnClickListener {

    public static final int GRADIENT_LINEAR = 0;
    public static final int GRADIENT_RADIAL = 1;
    public static final int GRADIENT_SWEEP = 2;
    public static final float NO_RADIUS = -1f;
    private static final float RADIUS = 12f;
    private static final float GHOST_X_ANIMATION_VALUE = 320f;
    private static final float GHOST_Y_ANIMATION_VALUE = -100f;
    private static final float GHOST_SCALE_ANIMATION_VALUE = 1.3f;
    private static final long ANIMATION_DURATION = 1500;
    private static final long ANIMATION_DELAY = 800;
    private static final float FLIGHT_THERE_START = -200f;
    private static final float FLIGHT_THERE_END = 1300f;
    private static final float FLIGHT_BACK_START = 1000f;
    private static final float FLIGHT_BACK_END = -400f;
    private static final long FLIGHT_DURATION = 2500;

    @Retention(RetentionPolicy.RUNTIME)
    @IntDef({ORIENTATION_TOP_BOTTOM,
            ORIENTATION_BOTTOM_TOP,
            ORIENTATION_RIGHT_LEFT,
            ORIENTATION_LEFT_RIGHT,
            ORIENTATION_BL_TR,
            ORIENTATION_TR_BL,
            ORIENTATION_BR_TL,
            ORIENTATION_TL_BR})
    @interface Orientation {
    }

    public static final int ORIENTATION_TOP_BOTTOM = 10;
    public static final int ORIENTATION_BOTTOM_TOP = 11;
    public static final int ORIENTATION_RIGHT_LEFT = 12;
    public static final int ORIENTATION_LEFT_RIGHT = 13;
    public static final int ORIENTATION_BL_TR = 14;
    public static final int ORIENTATION_TR_BL = 15;
    public static final int ORIENTATION_BR_TL = 16;
    public static final int ORIENTATION_TL_BR = 17;

    private Guideline topGuide;
    private FrameLayout root;
    private AppCompatImageView close;
    private AppCompatImageView plane;
    private AppCompatImageView moon;
    private AppCompatImageView ghost;
    private AppCompatImageView tomb;
    private AppCompatImageView ground;
    private AppCompatImageView pumpkin;
    private AppCompatImageView wifiIndicator;
    private AppCompatTextView noInternet;
    private AppCompatTextView noInternetBody;
    private AppCompatTextView turnOn;
    private AppCompatButton wifiOn;
    private AppCompatButton mobileOn;
    private AppCompatButton airplaneOff;
    private ProgressBar wifiLoading;

    private int bgGradientStart;
    private int bgGradientCenter;
    private int bgGradientEnd;
    private int bgGradientOrientation;
    private int bgGradientType;
    private float dialogRadius;
    private Typeface titleTypeface;
    private Typeface messageTypeface;
    private int buttonColor;
    private int buttonTextColor;
    private int buttonIconsColor;
    private int wifiLoaderColor;
    private boolean cancelable;

    private boolean isHalloween;
    private boolean isWifiOn;
    private int direction;
    private ObjectAnimator wifiAnimator;


    private LogoutDialog(@NonNull Context context, boolean cancelable) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_logout);
        initMainWindow();
        initView();
        initGuideLine();
        initBackground();
        initButtonStyle();
        initListeners();

        initClose();
    }

    @Override
    public void onClick(View v) {

    }

    private void initMainWindow() {
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    private void initClose() {
        setCancelable(cancelable);
        close.setVisibility(cancelable ? View.VISIBLE : View.GONE);
    }

    private void initView() {
        root = findViewById(R.id.root);
        close = findViewById(R.id.close);
        moon = findViewById(R.id.moon);
        plane = findViewById(R.id.plane);
        ghost = findViewById(R.id.ghost);
        tomb = findViewById(R.id.tomb);
        ground = findViewById(R.id.ground);
        pumpkin = findViewById(R.id.pumpkin);
        wifiIndicator = findViewById(R.id.wifi_indicator);
        noInternet = findViewById(R.id.no_internet);
        noInternetBody = findViewById(R.id.no_internet_body);
        turnOn = findViewById(R.id.turn_on);
        wifiOn = findViewById(R.id.wifi_on);
        mobileOn = findViewById(R.id.mobile_on);
        airplaneOff = findViewById(R.id.airplane_off);
        wifiLoading = findViewById(R.id.wifi_loading);
        topGuide = findViewById(R.id.top_guide);
    }

    private void initBackground() {
        GradientDrawable.Orientation orientation = getOrientation();

        GradientDrawable drawable = new GradientDrawable(orientation, new int[]{bgGradientStart, bgGradientCenter, bgGradientEnd});
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(dialogRadius);

        switch (bgGradientType) {
            case GRADIENT_RADIAL:
                drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                break;
            case GRADIENT_SWEEP:
                drawable.setGradientType(GradientDrawable.SWEEP_GRADIENT);
                break;
            default:
                drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                break;
        }

        if (isHalloween) {
            drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            drawable.setGradientRadius(getContext().getResources().getDimensionPixelSize(R.dimen.dialog_height) / 2);
        } else {
            drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            root.setBackground(drawable);
        } else {
            root.setBackgroundDrawable(drawable);
        }
    }

    private void initListeners() {
        close.setOnClickListener(this);
        wifiOn.setOnClickListener(this);
        mobileOn.setOnClickListener(this);
        airplaneOff.setOnClickListener(this);
    }

    private void initButtonStyle() {
        wifiOn.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN);
        mobileOn.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN);
        airplaneOff.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN);

        wifiOn.setTextColor(buttonTextColor);
        mobileOn.setTextColor(buttonTextColor);
        airplaneOff.setTextColor(buttonTextColor);

        Drawable wifi = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back_legacy);
        Drawable mobileData = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back_legacy);
        Drawable airplane = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back_legacy);

        wifi.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP);
        mobileData.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP);
        airplane.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wifiOn.setCompoundDrawablesRelativeWithIntrinsicBounds(wifi, null, null, null);
            mobileOn.setCompoundDrawablesRelativeWithIntrinsicBounds(mobileData, null, null, null);
            airplaneOff.setCompoundDrawablesRelativeWithIntrinsicBounds(airplane, null, null, null);
        } else {
            wifiOn.setCompoundDrawablesWithIntrinsicBounds(wifi, null, null, null);
            mobileOn.setCompoundDrawablesWithIntrinsicBounds(mobileData, null, null, null);
            airplaneOff.setCompoundDrawablesWithIntrinsicBounds(airplane, null, null, null);
        }
    }

    private GradientDrawable.Orientation getOrientation() {
        GradientDrawable.Orientation orientation;
        switch (bgGradientOrientation) {
            case ORIENTATION_BOTTOM_TOP:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case ORIENTATION_RIGHT_LEFT:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case ORIENTATION_LEFT_RIGHT:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case ORIENTATION_BL_TR:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case ORIENTATION_TR_BL:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            case ORIENTATION_BR_TL:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case ORIENTATION_TL_BR:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
            default:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
        }

        return orientation;
    }

    private void initGuideLine() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) topGuide.getLayoutParams();
        lp.guidePercent = isHalloween ? 0.34f : 0.3f;
        topGuide.setLayoutParams(lp);
    }

    public static class Builder {
        private Context context;
        private int bgGradientStart;
        private int bgGradientCenter;
        private int bgGradientEnd;
        private int bgGradientOrientation;
        private int bgGradientType;
        private float dialogRadius;
        private Typeface titleTypeface;
        private Typeface messageTypeface;
        private int buttonColor;
        private int buttonTextColor;
        private int buttonIconsColor;
        private int wifiLoaderColor;
        private boolean cancelable;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder(Fragment fragment) {
            this.context = fragment.getContext();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public Builder(android.app.Fragment fragment) {
            this.context = fragment.getContext();
        }

        public Builder setBgGradientStart(@ColorInt int bgGradientStart) {
            this.bgGradientStart = bgGradientStart;
            return this;
        }

        public Builder setBgGradientCenter(@ColorInt int bgGradientCenter) {
            this.bgGradientCenter = bgGradientCenter;
            return this;
        }

        public Builder setBgGradientEnd(@ColorInt int bgGradientEnd) {
            this.bgGradientEnd = bgGradientEnd;
            return this;
        }

        public Builder setBgGradientOrientation(@Orientation int bgGradientOrientation) {
            this.bgGradientOrientation = bgGradientOrientation;
            return this;
        }

        public Builder setBgGradientType(int bgGradientType) {
            this.bgGradientType = bgGradientType;
            return this;
        }

        public Builder setDialogRadius(float dialogRadius) {
            this.dialogRadius = dialogRadius;
            return this;
        }

        public Builder setDialogRadius(@DimenRes int dialogRadiusDimen) {
            this.dialogRadius = context.getResources().getDimensionPixelSize(dialogRadiusDimen);
            return this;
        }

        public Builder setTitleTypeface(Typeface titleTypeface) {
            this.titleTypeface = titleTypeface;
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public Builder setTitleTypeface(int titleTypefaceId) {
            this.titleTypeface = context.getResources().getFont(titleTypefaceId);
            return this;
        }

        public Builder setMessageTypeface(Typeface messageTypeface) {
            this.messageTypeface = messageTypeface;
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public Builder setMessageTypeface(int messageTypefaceId) {
            this.messageTypeface = context.getResources().getFont(messageTypefaceId);
            return this;
        }

        public Builder setButtonColor(int buttonColor) {
            this.buttonColor = buttonColor;
            return this;
        }

        public Builder setButtonTextColor(int buttonTextColor) {
            this.buttonTextColor = buttonTextColor;
            return this;
        }

        public Builder setButtonIconsColor(int buttonIconsColor) {
            this.buttonIconsColor = buttonIconsColor;
            return this;
        }

        public Builder setWifiLoaderColor(int wifiLoaderColor) {
            this.wifiLoaderColor = wifiLoaderColor;
            return this;
        }


        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public LogoutDialog build() {

            return new LogoutDialog(context, cancelable);
        }
    }

}
