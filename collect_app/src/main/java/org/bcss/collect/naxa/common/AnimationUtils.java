package org.bcss.collect.naxa.common;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class AnimationUtils {


    public static RotateAnimation getRotationAnimation(float from, float to) {
        RotateAnimation rotate = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setInterpolator(new LinearInterpolator());

        return rotate;
    }
}
