package common.base.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import common.base.R;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/1/8<br>
 * Time: 20:29<br>
 * <P>DESC:
 * 纯提示性的dialog
 * 主要用于提供类似于Toast的提示功能
 * </p>
 * ******************(^_^)***********************
 */
public class HintDialog extends BaseDialog<HintDialog> {
    private LinearLayout llToastRootView;
    private TextView tvToast;
    private ImageView ivDefToastHint;
    public HintDialog(Context context) {
        this(context, R.style.common_dialog);
    }

    public HintDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void initViews(View dialogView) {
        llToastRootView = (LinearLayout) dialogView;
        tvToast = dialogView.findViewById(R.id.toast_msg);
        ivDefToastHint = dialogView.findViewById(R.id.toast_icon);
    }

    @Override
    protected int getDialogViewResID() {
        return R.layout.ok_toast;
    }


    public HintDialog withDefToastHintIcon(boolean isShow) {
        if (ivDefToastHint != null) {
            ivDefToastHint.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
        return this;
    }
    public HintDialog withToastText(CharSequence toastText) {
        if (tvToast != null) {
            tvToast.setText(toastText);
        }
        return this;
    }

    /**
     * 该方法目前只接收大于0的正数padding值
     * 注：单位为像素
     * @param leftPadding 内部左边距
     * @param topPadding 内部上边距
     * @param rightPadding 内部右边距
     * @param bottomPadding 内部底边距
     * @return self
     */
    public HintDialog withTextPadding(int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        if (tvToast != null) {
            int newLeftPadding = leftPadding > 0 ? leftPadding : tvToast.getPaddingLeft();
            int newTopPadding = topPadding > 0 ? topPadding : tvToast.getPaddingTop();
            int newRightPadding = rightPadding > 0 ? rightPadding : tvToast.getPaddingRight();
            int newBottomPadding = bottomPadding > 0 ? bottomPadding : tvToast.getPaddingBottom();
            tvToast.setPadding(newLeftPadding, newTopPadding, rightPadding, newBottomPadding);
        }
        return this;
    }

    public TextView peekToastTextView() {
        return tvToast;
    }
    private SparseArray<View> extraViews;
    public HintDialog withToastTopView(View topView) {
        return withExtraView(topView, 0);
    }

    public HintDialog withExtraView(View extraView, int viewPos) {
        if (llToastRootView != null) {
            llToastRootView.addView(extraView,viewPos);
        }
        if (extraViews == null) {
            extraViews = new SparseArray<>();
        }
        extraViews.put(viewPos, extraView);
        return this;
    }

    public LinearLayout.LayoutParams withExtraViewResultLLP(View extraView, int viewPos) {
        withExtraView(extraView, viewPos);
        return (LinearLayout.LayoutParams) extraView.getLayoutParams();
    }
    public HintDialog clearAllExtraViews() {
        if (llToastRootView != null && extraViews != null && extraViews.size() > 0) {
            for(int i = 0; i < extraViews.size(); i++) {
                View addedView = extraViews.valueAt(i);
                llToastRootView.removeView(addedView);
            }
            extraViews.clear();
        }
        return this;
    }
    public HintDialog withBackground(@DrawableRes int background) {
        if (llToastRootView != null) {
            llToastRootView.setBackgroundResource(background);
        }
        return this;
    }

    public HintDialog withBackground(Drawable backgroundDrawable) {
        if (llToastRootView != null) {
            llToastRootView.setBackgroundDrawable(backgroundDrawable);
        }
        return self();
    }
    public HintDialog withTextSize(float spSize) {
        if (tvToast != null) {
            tvToast.setTextSize(spSize);
        }
        return this;
    }

    /**
     * 直接使用像素值来设置TextView的文本大小
     * @param pixelValue 像素值
     * @return self
     */
    public HintDialog withTextSizePixelValue(float pixelValue) {
        if (tvToast != null) {
            tvToast.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelValue);
        }
        return this;
    }
    public HintDialog withTextColor(@ColorInt int textColor) {
        if (tvToast != null) {
            tvToast.setTextColor(textColor);
        }
        return this;
    }
}
