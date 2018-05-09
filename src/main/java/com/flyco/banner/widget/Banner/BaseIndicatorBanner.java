package com.flyco.banner.widget.Banner;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.flyco.banner.anim.BaseAnimator;
import com.flyco.banner.widget.Banner.base.BaseBanner;
import java.util.ArrayList;
import common.base.R;

/**
 * 封装好了Indicator布局中以ImageView为承载圆点或者自定义小图标的Banner
 * @param <E>
 * @param <T>
 */
public abstract class BaseIndicatorBanner<E, T extends BaseIndicatorBanner<E, T>> extends BaseBanner<E, T> {
    /**
     * 填充在指示器ImageView上的样式为资源
     */
    public static final int STYLE_DRAWABLE_RESOURCE = 0;
    /**
     * 填充在指示器ImageView上的样式为圆角矩形(其实相当于Shape资源)
     */
    public static final int STYLE_CORNER_RECTANGLE = 1;

    private ArrayList<ImageView> mIndicatorViews = new ArrayList<>();
    /**
     * Banner翻页时的指示器样式
     * {@link #STYLE_DRAWABLE_RESOURCE} 图形资源<br>
     * {@link #STYLE_CORNER_RECTANGLE} 圆角矩形
     */
    private int mIndicatorStyle;
    /**
     * 单个的指示器的宽度，默认为：dp2px(6)
     */
    private int mIndicatorWidth;
    /**
     * 单个的指示器的调试，默认为：dp2px(6)
     */
    private int mIndicatorHeight;
    /**
     * 指示器之间的间隔
     */
    private int mIndicatorGap;
    /**
     * 当指示器为圆角矩形样式时：其圆角半径
     */
    private int mIndicatorCornerRadius;

    private Drawable mSelectDrawable;
    private Drawable mUnSelectDrawable;
    /**
     * 当指示器为圆角矩形样式时，当前页面选中时的颜色
     */
    private int mSelectColor;
    /**
     * 当指示器为圆角矩形样式时，页面未选中时的颜色
     */
    private int mUnselectColor;

    private Class<? extends BaseAnimator> mSelectAnimClass;
    private Class<? extends BaseAnimator> mUnselectAnimClass;

    private LinearLayout mLlIndicators;

    public BaseIndicatorBanner(Context context) {
        this(context, null, 0);
    }

    public BaseIndicatorBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseIndicatorBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseIndicatorBanner);
        //如果样式中没有指定，则默认为圆角矩形样式
        mIndicatorStyle = ta.getInt(R.styleable.BaseIndicatorBanner_bb_indicatorStyle, STYLE_CORNER_RECTANGLE);
        mIndicatorWidth = ta.getDimensionPixelSize(R.styleable.BaseIndicatorBanner_bb_indicatorWidth, dp2px(6));
        mIndicatorHeight = ta.getDimensionPixelSize(R.styleable.BaseIndicatorBanner_bb_indicatorHeight, dp2px(6));
        mIndicatorGap = ta.getDimensionPixelSize(R.styleable.BaseIndicatorBanner_bb_indicatorGap, dp2px(6));
        mIndicatorCornerRadius = ta.getDimensionPixelSize(R.styleable.BaseIndicatorBanner_bb_indicatorCornerRadius, dp2px(3));
        mSelectColor = ta.getColor(R.styleable.BaseIndicatorBanner_bb_indicatorSelectColor, Color.parseColor("#ffffff"));
        mUnselectColor = ta.getColor(R.styleable.BaseIndicatorBanner_bb_indicatorUnselectColor, Color.parseColor("#88ffffff"));

        int selectRes = ta.getResourceId(R.styleable.BaseIndicatorBanner_bb_indicatorSelectRes, 0);
        int unselectRes = ta.getResourceId(R.styleable.BaseIndicatorBanner_bb_indicatorUnselectRes, 0);
        ta.recycle();

        //create indicator container
        mLlIndicators = new LinearLayout(context);
        mLlIndicators.setGravity(Gravity.CENTER);

        setIndicatorSelectorRes(unselectRes, selectRes);
    }

    @Override
    public View onCreateIndicator() {
        if (mIndicatorStyle == STYLE_CORNER_RECTANGLE) {//rectangle
            this.mUnSelectDrawable = getDrawable(mUnselectColor, mIndicatorCornerRadius);
            this.mSelectDrawable = getDrawable(mSelectColor, mIndicatorCornerRadius);
        }

        int size = mDatas.size();
        mIndicatorViews.clear();

        mLlIndicators.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView iv = new ImageView(mContext);
            iv.setImageDrawable(i == mCurrentPositon ? mSelectDrawable : mUnSelectDrawable);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mIndicatorWidth,
                    mIndicatorHeight);
            lp.leftMargin = i == 0 ? 0 : mIndicatorGap;
            mLlIndicators.addView(iv, lp);
            mIndicatorViews.add(iv);
        }

        setCurrentIndicator(mCurrentPositon);

        return mLlIndicators;
    }

    @Override
    public void setCurrentIndicator(int position) {
        for (int i = 0; i < mIndicatorViews.size(); i++) {
            mIndicatorViews.get(i).setImageDrawable(i == position ? mSelectDrawable : mUnSelectDrawable);
        }
        try {
            if (mSelectAnimClass != null) {
                if (position == mLastPositon) {
                    mSelectAnimClass.newInstance().playOn(mIndicatorViews.get(position));
                } else {
                    mSelectAnimClass.newInstance().playOn(mIndicatorViews.get(position));
                    if (mUnselectAnimClass == null) {
                        mSelectAnimClass.newInstance().interpolator(new ReverseInterpolator()).playOn(mIndicatorViews.get(mLastPositon));
                    } else {
                        mUnselectAnimClass.newInstance().playOn(mIndicatorViews.get(mLastPositon));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 设置显示样式,STYLE_DRAWABLE_RESOURCE or STYLE_CORNER_RECTANGLE */
    public T setIndicatorStyle(int indicatorStyle) {
        this.mIndicatorStyle = indicatorStyle;
        return (T) this;
    }

    /** 设置显示宽度,单位dp,默认6dp */
    public T setIndicatorWidth(float indicatorWidth) {
        this.mIndicatorWidth = dp2px(indicatorWidth);
        return (T) this;
    }

    /** 设置显示器高度,单位dp,默认6dp */
    public T setIndicatorHeight(float indicatorHeight) {
        this.mIndicatorHeight = dp2px(indicatorHeight);
        return (T) this;
    }

    /** 设置两个显示器间距,单位dp,默认6dp */
    public T setIndicatorGap(float indicatorGap) {
        this.mIndicatorGap = dp2px(indicatorGap);
        return (T) this;
    }

    /** 设置显示器选中颜色(for STYLE_CORNER_RECTANGLE),默认"#ffffff" */
    public T setIndicatorSelectColor(int selectColor) {
        this.mSelectColor = selectColor;
        return (T) this;
    }

    /** 设置显示器未选中颜色(for STYLE_CORNER_RECTANGLE),默认"#88ffffff" */
    public T setIndicatorUnselectColor(int unselectColor) {
        this.mUnselectColor = unselectColor;
        return (T) this;
    }

    /** 设置显示器圆角弧度(for STYLE_CORNER_RECTANGLE),单位dp,默认3dp */
    public T setIndicatorCornerRadius(float indicatorCornerRadius) {
        this.mIndicatorCornerRadius = dp2px(indicatorCornerRadius);
        return (T) this;
    }

    /** 设置显示器选中以及未选中资源(for STYLE_DRAWABLE_RESOURCE) */
    public T setIndicatorSelectorRes(@DrawableRes int unselectRes, @DrawableRes int selectRes) {
        try {
            if (mIndicatorStyle == STYLE_DRAWABLE_RESOURCE) {
                if (selectRes != 0) {
                    this.mSelectDrawable = getResources().getDrawable(selectRes);
                }
                if (unselectRes != 0) {
                    this.mUnSelectDrawable = getResources().getDrawable(unselectRes);
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return (T) this;
    }

    /** 设置显示器选中动画*/
    public T setSelectAnimClass(Class<? extends BaseAnimator> selectAnimClass) {
        this.mSelectAnimClass = selectAnimClass;
        return (T) this;
    }

    /** 设置显示器未选中动画 */
    public T setUnselectAnimClass(Class<? extends BaseAnimator> unselectAnimClass) {
        this.mUnselectAnimClass = unselectAnimClass;
        return (T) this;
    }

    private class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }
    }

    protected GradientDrawable getDrawable(int color, float raduis) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(raduis);
        drawable.setColor(color);

        return drawable;
    }
}
