package com.flyco.banner.widget.Banner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * ******************(^_^)***********************
 * User: 11776610771@qq.com
 * Date: 2017/4/25
 * Time: 16:08
 * DESC:
 * ******************(^_^)***********************
 */

public class JustImageViewBanner<DataType> extends BaseIndicatorBanner<DataType,JustImageViewBanner<DataType>>{
    /**
     * 默认的占位Drawable
     */
    protected ColorDrawable defHolderColorDrawable;
    public JustImageViewBanner(Context context) {
        this(context,null);
    }

    public JustImageViewBanner(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public JustImageViewBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        defHolderColorDrawable = new ColorDrawable(Color.parseColor("#555555"));
    }

    /**
     * 创建ViewPager的Item布局
     *
     * @param position
     */
    @Override
    public View onCreateItemView(int position) {
        ImageView ivBanner = new ImageView(mContext);
        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ivBanner.setLayoutParams(vlp);
        bindImage2BannerView(ivBanner,position);
        return ivBanner;
    }

    protected void bindImage2BannerView(ImageView theJustImageView,int curInitPosition) {

    }
}
