package com.flyco.banner.anim.select;

import android.animation.ObjectAnimator;
import android.view.View;
import com.flyco.banner.anim.BaseAnimator;

public class RotateEnter extends BaseAnimator {
    public RotateEnter() {
        this.mDuration = 200;
    }

    public void setAnimation(View view) {
        this.mAnimatorSet.playTogether(ObjectAnimator.ofFloat(view, "rotation", 0, 180)
        );
    }
}
