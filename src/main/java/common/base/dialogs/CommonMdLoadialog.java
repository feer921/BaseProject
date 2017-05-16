package common.base.dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import common.base.R;
import common.base.utils.CommonLog;
import common.base.utils.Util;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-15
 * Time: 18:09
 * DESC:
 */
public class CommonMdLoadialog extends BaseDialog<CommonMdLoadialog>{
    private ImageView ivLoadIcon;
    private TextView tvLoadHint;
    private Animation loadingAnimation;
    private RelativeLayout rlContentView;
    public CommonMdLoadialog(Context context) {
        super(context,R.style.common_load_dialog_style);
    }

    @Override
    protected int getDialogViewResID() {
        return R.layout.common_md_load_dialog_layout;
    }

    @Override
    protected void initViews(View containerView) {
        rlContentView = (RelativeLayout) containerView.findViewById(R.id.common_md_dialog_content_layout);
        ivLoadIcon = (ImageView) containerView.findViewById(R.id.iv_dialog_loading);
        tvLoadHint = (TextView) containerView.findViewById(R.id.tv_dialog_load_hint);
        loadingAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.common_rotate_anim);
        loadingAnimation.setInterpolator(new LinearInterpolator());
        ivLoadIcon.setAnimation(loadingAnimation);
    }

    @Override
    public CommonMdLoadialog setHintMsg(String hintMsg) {
        if (tvLoadHint != null) {
            tvLoadHint.setText(hintMsg);
        }
        return self();
    }

    public CommonMdLoadialog setHintMsg(int hintMsgResID) {
        return setHintMsg(getStrFromResId(hintMsgResID));
    }

    public CommonMdLoadialog changeLoadingHintIcon(@DrawableRes int iconResId) {
        if (ivLoadIcon != null) {
            ivLoadIcon.setImageResource(iconResId);
        }
        return self();
    }

    public CommonMdLoadialog changeLoadingHintIcon(Drawable drawable) {
        if (ivLoadIcon != null) {
            ivLoadIcon.setImageDrawable(drawable);
        }
        return self();
    }
    public CommonMdLoadialog changeLoadingContentViewBg(@DrawableRes int bgResId) {
        if (rlContentView != null) {
            rlContentView.setBackgroundResource(bgResId);
        }
        return self();
    }
    @Override
    public TextView getDialogHintView() {
        return tvLoadHint;
    }

    public ImageView getIvLoadIcon() {
        return ivLoadIcon;
    }
    @Override
    public void show() {
        ivLoadIcon.setAnimation(loadingAnimation);
        super.show();
    }

    @Override
    public void dismiss() {
        ivLoadIcon.clearAnimation();
        super.dismiss();
    }

    @Override
    public CommonMdLoadialog adjustDialogContentWH(int dpUnitW, int dpUnitH) {
        if (rlContentView != null) {
            ViewGroup.LayoutParams vlp = rlContentView.getLayoutParams();
            if (vlp != null) {
                if (dpUnitW > 0) {
                    vlp.width = Util.dip2px(mContext,dpUnitW);
                }
                if (dpUnitH > 0) {
                    vlp.height = Util.dip2px(mContext, dpUnitH);
                }
                CommonLog.e("info","--> width  " + vlp.width + " h;" + vlp.height);
                if (dpUnitW > 0 || dpUnitH > 0) {
                    rlContentView.setLayoutParams(vlp);
                }
            }
        }
        return self();
    }
}
