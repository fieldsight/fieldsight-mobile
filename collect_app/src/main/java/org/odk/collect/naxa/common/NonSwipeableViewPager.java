package org.odk.collect.naxa.common;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by nishon on 4/9/17.
 */

public class NonSwipeableViewPager extends ViewPager {
    private boolean allowSwipe = false;


    public void allowSwipe() {
        this.allowSwipe = true;
    }

    public void disableSwipe() {
        this.allowSwipe = false;
    }

    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return allowSwipe;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return allowSwipe;
    }


}
