package org.bcss.collect.naxa.common.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class ScaleUpAndDownItemAnimator extends DefaultItemAnimator {
    private static final float MAX_SCALE = 3f;
    private static final long DURATION_SCALE = 300;

    private final AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator(2f);
    private final DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(2f);

    // Used to construct the new change animation based on where the previous one was at when it was interrupted.
    private final ArrayMap<RecyclerView.ViewHolder, AnimatorInfo> animatorMap = new ArrayMap<>();

    @Override public boolean canReuseUpdatedViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder) {
        // This allows our custom change animation on the contents of the holder instead
        // of the default behavior of replacing the viewHolder entirely
        return true;
    }

    // Custom change animation to expand the item then shrink it back to its original size.
    // If a new change animation occurs on an item that is currently animating a change, we stop the
    // previous change and start the new one where the old one left off.
    @Override public boolean animateChange(@NonNull final RecyclerView.ViewHolder oldHolder, @NonNull final RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {
        if (oldHolder != newHolder) {
            // use default behavior if not re-using view holders
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
        }

        // Check to see if there's a change animation already running on this item
        final AnimatorInfo runningInfo = animatorMap.get(newHolder);
        long prevAnimPlayTime = 0;
        boolean firstHalf = false;
        if (runningInfo != null) {
            // The information we need to construct the new animators is whether we are in the 'first half'
            // (scaling the size up) and how far we are into whichever half is running
            firstHalf = runningInfo.zoomInAnimator != null && runningInfo.zoomInAnimator.isRunning();
            prevAnimPlayTime = firstHalf ? runningInfo.zoomInAnimator.getCurrentPlayTime() : runningInfo.zoomOutAnimator.getCurrentPlayTime();
            // done with previous animation - cancel it
            runningInfo.overallAnim.cancel();
        }
        final View itemView = newHolder.itemView;

        ValueAnimator scaleUpAnimator = null, scaleDownAnimator;
        if (runningInfo == null || firstHalf) {
            // The first part of the animation scales the view
            // Skip this phase if we're interrupting an animation that was already in the second phase.
            scaleUpAnimator = ValueAnimator.ofFloat(1f, MAX_SCALE);
            scaleUpAnimator.setInterpolator(accelerateInterpolator);
            scaleUpAnimator.setDuration(DURATION_SCALE);
            scaleUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(@NonNull final ValueAnimator animation) {
                    final float scale = (float) animation.getAnimatedValue();
                    itemView.setScaleX(scale);
                    itemView.setScaleY(scale);
                }
            });
            if (runningInfo != null) {
                scaleUpAnimator.setCurrentPlayTime(prevAnimPlayTime);
            }
        }

        scaleDownAnimator = ValueAnimator.ofFloat(MAX_SCALE, 1f);
        scaleDownAnimator.setInterpolator(decelerateInterpolator);
        scaleDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(@NonNull final ValueAnimator animation) {
                final float scale = (float) animation.getAnimatedValue();
                itemView.setScaleX(scale);
                itemView.setScaleY(scale);
            }
        });
        if (runningInfo != null && !firstHalf) {
            // If we're interrupting a previous second-phase animation, seek to that time
            scaleDownAnimator.setCurrentPlayTime(prevAnimPlayTime);
        }

        // Choreograph first and second half. First half may be null if we interrupted a second-phase animation
        final AnimatorSet overallAnimation = new AnimatorSet();
        if (scaleUpAnimator != null) {
            overallAnimation.playSequentially(scaleUpAnimator, scaleDownAnimator);
        } else {
            overallAnimation.play(scaleDownAnimator);
        }

        overallAnimation.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationStart(@NonNull final Animator animation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemView.setTranslationZ(1);
                }
            }

            @Override public void onAnimationEnd(@NonNull final Animator animation) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemView.setTranslationZ(0);
                }
                dispatchAnimationFinished(newHolder);
                animatorMap.remove(newHolder);
            }
        });
        overallAnimation.start();

        // Store info about this animation to be re-used if a succeeding change event occurs while it's still running
        final AnimatorInfo runningAnimInfo = new AnimatorInfo(overallAnimation, scaleUpAnimator, scaleDownAnimator);
        animatorMap.put(newHolder, runningAnimInfo);

        return true;
    }

    @Override public void endAnimation(@NonNull final RecyclerView.ViewHolder item) {
        super.endAnimation(item);
        if (!animatorMap.isEmpty()) {
            final int numRunning = animatorMap.size();
            for (int i = numRunning; i >= 0; i--) {
                if (item == animatorMap.keyAt(i)) {
                    animatorMap.valueAt(i).overallAnim.cancel();
                }
            }
        }
    }

    @Override public boolean isRunning() {
        return super.isRunning() || !animatorMap.isEmpty();
    }

    @Override public void endAnimations() {
        super.endAnimations();

        if (!animatorMap.isEmpty()) {
            final int numRunning = animatorMap.size();
            for (int i = numRunning; i >= 0; i--) {
                animatorMap.valueAt(i).overallAnim.cancel();
            }
        }
    }

    // Holds child animator objects for any change animation. Used when a new change animation interrupts one already
    // in progress; the new one is constructed to start from where the previous one was at when the interruption occurred.
    private static class AnimatorInfo {
        final Animator overallAnim;
        final ValueAnimator zoomInAnimator;
        final ValueAnimator zoomOutAnimator;

        AnimatorInfo(@NonNull final Animator overallAnim, @Nullable final ValueAnimator zoomInAnimator, @NonNull final ValueAnimator zoomOutAnimator) {
            this.overallAnim = overallAnim;
            this.zoomInAnimator = zoomInAnimator;
            this.zoomOutAnimator = zoomOutAnimator;
        }
    }
}
