package org.bcss.collect.naxa.common;

import android.support.design.widget.AppBarLayout;

public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

    public static class State {
        public static final int EXPANDED = 1;
        public static final int COLLAPSED = 0;
        public static final int IDLE = 2;
    }


    private int mCurrentState = State.IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, int state);
}
