package org.bcss.collect.naxa.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import org.bcss.collect.android.R;

public class AnimationUtils {


    public static RotateAnimation getRotationAnimation(float from, float to) {
        RotateAnimation rotate = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setInterpolator(new LinearInterpolator());

        return rotate;
    }

    public static void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                android.view.animation.AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }
}
