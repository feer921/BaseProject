package common.base.views;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import common.base.R;
import common.base.utils.ViewUtil;

/**
 * ******************(^_^)***********************<br>
 * User: 11776610771@qq.com<br>
 * Date: 2017/8/12<br>
 * Time: 16:04<br>
 * <P>DESC:
 * 通用的APP头部视图
 * </p>
 * ******************(^_^)***********************
 */

public class CommonAppHeaderView extends LinearLayout implements View.OnClickListener{
    private ImageView ivHeaderLeft;

    private TextView tvHeaderLeftDesc;

    private TextView tvHeaderTitle;

    private LinearLayout headerLeftBlock;

    private LinearLayout headerRightBlock;


    private OnClickListener outSideClickListener;

    public CommonAppHeaderView(Context context) {
        this(context,null);
    }

    public CommonAppHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.base_common_header_view, this);
        initViews(context);
        //自定义属性

    }

    private void initViews(Context context) {
        ivHeaderLeft = ViewUtil.findViewInContainer(this, R.id.header_left_icon);
        tvHeaderLeftDesc = ViewUtil.findViewInContainer(this, R.id.header_left_desc);
        tvHeaderTitle = ViewUtil.findViewInContainer(this, R.id.header_title);
        headerLeftBlock = ViewUtil.findViewInContainer(this, R.id.header_left_block);
        headerRightBlock = ViewUtil.findViewInContainer(this, R.id.header_right_block);

        //set onClick listener

        ivHeaderLeft.setOnClickListener(this);
        headerLeftBlock.setOnClickListener(this);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (outSideClickListener != null) {
            if (v == headerLeftBlock) {
                v = ivHeaderLeft;
            }
            outSideClickListener.onClick(v);
        }
    }

    public CommonAppHeaderView withTitle(CharSequence title) {
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText(title);
        }
        return this;
    }

    public CommonAppHeaderView withTitle(@StringRes int titleResId) {
        return withTitle(getStrText(titleResId));
    }

    private CharSequence getStrText(@StringRes int strResId) {
        return getResources().getString(strResId);
    }
    public CommonAppHeaderView withClickListener(OnClickListener clickListener) {
        this.outSideClickListener = clickListener;
        return this;
    }

    public CommonAppHeaderView withLeftIconDrawable(@DrawableRes int backgroundResId) {
        if (ivHeaderLeft != null) {
            ivHeaderLeft.setBackgroundResource(backgroundResId);
        }
        return this;
    }

    public CommonAppHeaderView withLeftDesc(CharSequence desc) {
        if (tvHeaderLeftDesc != null) {
            tvHeaderLeftDesc.setVisibility(desc != null ? VISIBLE : GONE);
            tvHeaderLeftDesc.setText(desc);
        }
        return this;
    }

    public CommonAppHeaderView withLeftDesc(@StringRes int descResId) {
        return withLeftDesc(getStrText(descResId));
    }

    public CommonAppHeaderView withLeftBlockBg(@DrawableRes int backgroundResId) {
        if (headerLeftBlock != null) {
            headerLeftBlock.setBackgroundResource(backgroundResId);
        }
        return this;
    }

    public CommonAppHeaderView withRightView(View fixToRightView) {
        if (headerRightBlock != null) {
            headerRightBlock.addView(fixToRightView);
        }
        return this;
    }



}
