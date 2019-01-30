package com.flyco.banner.anim.unselect;

import android.animation.ObjectAnimator;
import android.view.View;

import com.flyco.banner.anim.BaseAnimator;

public class NoAnimExist extends BaseAnimator {
    public NoAnimExist() {
        this.mDuration = 200;
    }

    public void setAnimation(View view) {
        this.mAnimatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "alpha", 1, 1));
    }
}
