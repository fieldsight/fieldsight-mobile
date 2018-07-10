package org.odk.collect.naxa.common;

import android.content.Context;
import android.util.TypedValue;

public final class ViewUtils {
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}

