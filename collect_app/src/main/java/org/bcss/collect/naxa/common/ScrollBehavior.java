package org.bcss.collect.naxa.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;



public class ScrollBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {


    private static final Long HIDE_DURATION = 250L;
    private static final float TRANSLATION_HIDE = 500f;
    private static final float TRANSLATION_SHOW = 0f;
    private ViewPropertyAnimatorCompat animation = null;
    private static final Interpolator HIDE_INTERPOLATOR = new FastOutSlowInInterpolator();

    public ScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {

        return super.onLayoutChild(parent, child, layoutDirection);
    }


    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if (dyConsumed > 0) {
            show(child);
        } else if (dyConsumed < 0) {
            hide(child);

        }
    }


    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        //ensure we are scroll vertically
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }


    private void hide(FloatingActionButton floatingTextButton) {


        animation = ViewCompat.animate(floatingTextButton)
                .translationY(TRANSLATION_HIDE)
                .setDuration(HIDE_DURATION);
        animation.start();
    }

    private void show(FloatingActionButton floatingTextButton) {

        if(floatingTextButton.getVisibility() == View.GONE)floatingTextButton.setVisibility(View.VISIBLE);
        animation = ViewCompat.animate(floatingTextButton)
                .translationY(TRANSLATION_SHOW)
                .setDuration(HIDE_DURATION);
        animation.start();
    }

    @Override
    public boolean layoutDependsOn(
            CoordinatorLayout parent,
            FloatingActionButton child,
            View dependency
    ) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(
            CoordinatorLayout parent,
            FloatingActionButton child,
            View dependency
    ) {
        if (child.getTranslationY() > 0) {
            return true;
        }
        if (animation != null) {
            animation.cancel();
            animation = null;
        }

        child.setTranslationY(
                Math.min(0f, dependency.getTranslationY() - dependency.getHeight())
        );
        return true;
    }

    @Override
    public void onDependentViewRemoved(
            CoordinatorLayout parent,
            FloatingActionButton child,
            View dependency
    ) {
        if (dependency instanceof Snackbar.SnackbarLayout) {

            animation = ViewCompat.animate(child)
                    .translationY(0f)
                    .setInterpolator(HIDE_INTERPOLATOR)
                    .setDuration(HIDE_DURATION);

            animation.start();
        }
        super.onDependentViewRemoved(parent, child, dependency);
    }
}