package com.flyco.banner.transform;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class RotateDownTransformer implements ViewPager.PageTransformer {

	private static final float ROT_MOD = -15f;

	@Override
	public void transformPage(View page, float position) {
		final float width = page.getWidth();
		final float height = page.getHeight();
		final float rotation = ROT_MOD * position * -1.25f;

		page.setPivotX(width * 0.5f);
		page.setPivotY(height);
		page.setRotation(rotation);
	}
}
