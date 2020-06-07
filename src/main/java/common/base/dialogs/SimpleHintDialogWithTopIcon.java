package common.base.dialogs;

import android.content.Context;
import androidx.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import common.base.R;
import common.base.utils.CheckUtil;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/9/27<br>
 * Time: 18:49<br>
 * <P>DESC:
 * 通用、简单提示性对话框，带 Top 位置有显示 Icon
 * </p>
 * ******************(^_^)***********************
 */
public class SimpleHintDialogWithTopIcon extends SimpleHintDialog {

    private ImageView ivDialogTopHintIcon;
    private TextView tvDialogSubTitle;

    private LinearLayout llContainerView;

    public SimpleHintDialogWithTopIcon(Context context) {
        super(context);
    }

    @Override
    protected void initViews(View dialogView) {
        super.initViews(dialogView);
        if (dialogView != null) {
            ivDialogTopHintIcon = dialogView.findViewById(R.id.iv_hint_top_icon);
            tvDialogSubTitle = dialogView.findViewById(R.id.tv_dialog_sub_title);
            llContainerView = dialogView.findViewById(R.id.ll_contaner_view);
        }
    }

    /**
     * 获取本Dialog的布局视图资源ID
     *
     * @return
     */
    @Override
    protected int getDialogViewResID() {
        return R.layout.simple_hint_dialog_with_top_icon;
    }


    public SimpleHintDialogWithTopIcon withTopIcon(@DrawableRes int iconResId) {
        if (ivDialogTopHintIcon != null) {
            ivDialogTopHintIcon.setImageResource(iconResId);
        }
        return this;
    }


    public SimpleHintDialogWithTopIcon withSubTitleText(CharSequence subTitleText) {
        if (tvDialogSubTitle != null) {
            if (!CheckUtil.isEmpty(subTitleText)) {
                if (HIDE_FLAG.equals(subTitleText.toString())) {
                    visibleSubTitle(false);
                }
                else {
                    visibleSubTitle(true);
                }
                tvDialogSubTitle.setText(subTitleText);
            }
        }
        return this;
    }

    /**
     * 这里要重写父类的给dialogView设置背景的方法，因为该dialog只适合给 llContainerView设置背景
     * @param bgResId
     * @return
     */
    @Override
    public SimpleHintDialogWithTopIcon setDialogViewBackground(int bgResId) {
        if (llContainerView != null) {
            llContainerView.setBackgroundResource(bgResId);
        }
        return this;
    }

    public SimpleHintDialogWithTopIcon visibleSubTitle(boolean visible) {
        if (tvDialogSubTitle != null) {
            tvDialogSubTitle.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
        return this;
    }

    public ImageView getIvDialogTopHintIcon() {
        return ivDialogTopHintIcon;
    }

    public TextView getTvDialogSubTitle() {
        return tvDialogSubTitle;
    }
}
