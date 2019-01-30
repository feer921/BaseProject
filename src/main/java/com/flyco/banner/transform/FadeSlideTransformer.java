package com.flyco.banner.transform;

import android.view.View;
import android.support.v4.view.ViewPager;

public class FadeSlideTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {

        page.setTranslationX(0);

        if (position <= -1.0F || position >= 1.0F) {
            page.setAlpha( 0.0F);
        } else if (position == 0.0F) {
            page.setAlpha( 1.0F);
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            page.setAlpha( 1.0F - Math.abs(position));
        }
    }
}
