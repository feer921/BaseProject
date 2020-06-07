package com.flyco.banner.transform;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class FlowTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View page, float position) {
        page.setRotationY( position * -30f);
    }
}
