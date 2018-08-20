package org.bcss.collect.naxa.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.bcss.collect.android.R;

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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public static GlideRequest<Drawable> loadLocalImage(Context context, String path) {
        return GlideApp.with(context)
                .load(path)
                .centerInside()
                .transition(DrawableTransitionOptions.withCrossFade())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
    }


    public static GlideRequest<Drawable> loadRemoteImage(Context context, String path) {
        return GlideApp.with(context)
                .load(path)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerInside()
                .skipMemoryCache(false)
                .priority(Priority.LOW)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }


}

