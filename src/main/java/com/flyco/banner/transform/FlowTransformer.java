package com.flyco.banner.transform;

import android.support.v4.view.ViewPager;
import android.view.View;

public class FlowTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View page, float position) {
        page.setRotationY( position * -30f);
    }
}
