package common.base.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import common.base.R;
import common.base.utils.Util;
import common.base.utils.ViewUtil;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-14
 * Time: 14:19
 * DESC: 通用条形item 控件
 */
public class CommonItemView extends RelativeLayout{
    private ImageView ivItemTypeIcon;
    private TextView tvItmeName;
    private TextView tvItemExtraDesc;
    private ImageView ivRightArrow;
    public CommonItemView(Context context) {
        this(context, null);
    }

    public CommonItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CommonItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.common_item_layout, this);
        ivItemTypeIcon = ViewUtil.findViewInContainer(this, R.id.iv_itme_type_icon);
        tvItmeName = ViewUtil.findViewInContainer(this, R.id.tv_item_name);
        tvItemExtraDesc = ViewUtil.findViewInContainer(this, R.id.tv_item_extra_desc);
        ivRightArrow = ViewUtil.findViewInContainer(this, R.id.iv_item_right_arrow);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CommonItemView, defStyleAttr,0);
        ColorStateList tvExtraDescTextColor = null;
        ColorStateList tvItemNameTextColor = null;
        int attrsCount = typedArray.getIndexCount();
        for(int i = 0 ; i < attrsCount ; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.CommonItemView_item_extra_desc) {
                String itemExtraDescStr = typedArray.getString(attr);
                if (!Util.isEmpty(itemExtraDescStr)) {
                    tvItemExtraDesc.setVisibility(View.VISIBLE);
                    tvItemExtraDesc.setText(itemExtraDescStr);
                }

            } else if (attr == R.styleable.CommonItemView_item_extra_desc_textColor) {
                tvExtraDescTextColor = typedArray.getColorStateList(attr);

            } else if (attr == R.styleable.CommonItemView_item_extra_desc_textSize) {
                int size = typedArray.getDimensionPixelSize(attr, 15);
                tvItemExtraDesc.setTextSize(0, size);

            } else if (attr == R.styleable.CommonItemView_item_extra_margin_right_arrow) {
                int marginRight = typedArray.getDimensionPixelSize(attr, 16);
                MarginLayoutParams mlp1 = (MarginLayoutParams) tvItemExtraDesc.getLayoutParams();
                mlp1.rightMargin = marginRight;
                tvItemExtraDesc.setLayoutParams(mlp1);

            } else if (attr == R.styleable.CommonItemView_item_name_margin_icon) {
                int marginLeft = typedArray.getDimensionPixelSize(attr, 16);
                MarginLayoutParams mlp = (MarginLayoutParams) tvItmeName.getLayoutParams();
                mlp.leftMargin = marginLeft;
                tvItmeName.setLayoutParams(mlp);

            } else if (attr == R.styleable.CommonItemView_item_name_text_color) {
                tvItemNameTextColor = typedArray.getColorStateList(attr);

            } else if (attr == R.styleable.CommonItemView_item_name_textSize) {//注意：该方法已经把布局中属性写的值，经过转换成像素了，即如果 属性赋值为8sp 则经过下面方法后，像素值为 8 * scaleDensty [4] = 32,所以下面不能直接
                //调用setTextSize(sizea),因为该方法又会依据sp规则再去拿一遍像素值，会导致上面的例子32 * scaleDensty [4] = 128像素了
                //而应该是直接设置成像素值
                int sizea = typedArray.getDimensionPixelSize(attr, 15);
                tvItmeName.setTextSize(0, sizea);

            } else if (attr == R.styleable.CommonItemView_item_type_name_desc) {
                String itmeTypeName = typedArray.getString(attr);
                tvItmeName.setText(itmeTypeName);

            } else if (attr == R.styleable.CommonItemView_itme_type_icon_res) {
                int itemIconResId = typedArray.getResourceId(attr, 0);
                if (itemIconResId > 0) {
                    ivItemTypeIcon.setImageResource(itemIconResId);
                }

            } else if (attr == R.styleable.CommonItemView_item_hide_type_icon) {
                boolean isNeedHideIcon = typedArray.getBoolean(attr, false);
                if (isNeedHideIcon && ivItemTypeIcon != null) {
                    ivItemTypeIcon.setVisibility(View.GONE);
                }

            } else if (attr == R.styleable.CommonItemView_item_hide_right_arrow) {
                boolean isNeedHideRightArrow = typedArray.getBoolean(attr, false);
                if (isNeedHideRightArrow && ivRightArrow != null) {
                    ivRightArrow.setVisibility(INVISIBLE);
                }
            } else if (attr == R.styleable.CommonItemView_item_right_arrow_res_id) {
                int rightArrowResId = typedArray.getResourceId(attr, 0);
                if (rightArrowResId > 0 && ivRightArrow != null) {
                    ivRightArrow.setImageResource(rightArrowResId);
                }
            }
        }
        tvItmeName.setTextColor(tvItemNameTextColor != null ? tvItemNameTextColor : ColorStateList.valueOf(0xFF000000));
        tvItemExtraDesc.setTextColor(tvExtraDescTextColor != null ? tvExtraDescTextColor : ColorStateList.valueOf(0xFF000000));
        typedArray.recycle();
    }

    public ImageView setItemTypeIcon(int iconResId) {
        if (ivItemTypeIcon != null && iconResId > 0) {
            ivItemTypeIcon.setImageResource(iconResId);
            ivItemTypeIcon.setVisibility(View.VISIBLE);
        }
        if (iconResId <= 0) {
            if (ivItemTypeIcon != null) {
                ivItemTypeIcon.setVisibility(View.GONE);
            }
        }
        return ivItemTypeIcon;
    }

    public void setItemTypeIconVisibility(boolean isNeedToHide) {
        if (ivItemTypeIcon != null) {
            ivItemTypeIcon.setVisibility(isNeedToHide ? GONE : VISIBLE);
        }
    }
    public TextView setItemTypeName(String itemName) {
        if (tvItmeName != null) {
            tvItmeName.setText(itemName);
        }
        return tvItmeName;
    }
    public TextView setItemTypeName(int itemNameResID) {
        if (tvItmeName != null) {
            tvItmeName.setText(itemNameResID);
        }
        return tvItmeName;
    }

    public TextView setItemExtraDesc(String itemExtraDesc) {
        if (!Util.isEmpty(itemExtraDesc)) {
            tvItemExtraDesc.setVisibility(View.VISIBLE);
        }
        tvItemExtraDesc.setText(itemExtraDesc);
        return tvItemExtraDesc;
    }
    public TextView setItemExtraDesc(int itemExtraDescResId) {
        return setItemExtraDesc(getResources().getString(itemExtraDescResId));
    }

    public ImageView setRightIcon(int rightIconResId) {
        if (rightIconResId > 0) {
            if (ivRightArrow != null) {
                ivRightArrow.setImageResource(rightIconResId);
            }
        }
        return ivRightArrow;
    }
}
