package com.flyco.banner.transform;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class RotateUpTransformer implements ViewPager.PageTransformer {

	private static final float ROT_MOD = -15f;

	@Override
	public void transformPage(View page, float position) {
		final float width = page.getWidth();
		final float rotation = ROT_MOD * position;

		page.setPivotX(width * 0.5f);
		page.setPivotY(0f);
		page.setTranslationX(0f);
		page.setRotation(rotation);
	}
}
