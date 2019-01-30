package com.flyco.banner.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.Interpolator;


public abstract class BaseAnimator {
    protected long mDuration = 500;

    protected AnimatorSet mAnimatorSet = new AnimatorSet();
    private Interpolator mInterpolator;
    private long mDelay;
    public abstract void setAnimation(View view);

    protected void start(final View view) {
        reset(view);
        setAnimation(view);

        mAnimatorSet.setDuration(mDuration);
        if (mInterpolator != null) {
            mAnimatorSet.setInterpolator(mInterpolator);
        }
        if (mDelay > 0) {
            mAnimatorSet.setStartDelay(mDelay);
        }
        mAnimatorSet.start();
    }

    public static void reset(View view) {
        if (view == null) {
            return;
        }
        view.setAlpha(1);
        view.setScaleX(1);
        view.setScaleY(1);
        view.setTranslationX(0);
        view.setTranslationY(0);
        view.setRotation(0);
        view.setRotationY(0);
        view.setRotationX(0);
    }

    public BaseAnimator duration(long duration) {
        this.mDuration = duration;
        return this;
    }

    public BaseAnimator delay(long delay) {
        this.mDelay = delay;
        return this;
    }

    public BaseAnimator interpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
        return this;
    }

    public BaseAnimator listener(Animator.AnimatorListener listener) {
        if (mAnimatorSet != null) {
            mAnimatorSet.addListener(listener);
        }
        return this;
    }

    public void playOn(View view) {
        start(view);
    }
}
