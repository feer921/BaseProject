package common.base.dialogs;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import common.base.R;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-15
 * Time: 18:09
 * DESC:
 */
public class CommonMdLoadialog extends BaseDialog{
    private ImageView ivLoadIcon;
    private TextView tvLoadHint;
    private Animation loadingAnimation;
    public CommonMdLoadialog(Context context) {
        super(context,R.style.common_load_dialog_style);
    }

    @Override
    protected int getContentViewResID() {
        return R.layout.common_md_load_dialog_layout;
    }

    @Override
    protected void initViews(View containerView) {
        ivLoadIcon = (ImageView) containerView.findViewById(R.id.iv_dialog_loading);
        tvLoadHint = (TextView) containerView.findViewById(R.id.tv_dialog_load_hint);
        loadingAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.common_rotate_anim);
        loadingAnimation.setInterpolator(new LinearInterpolator());
        ivLoadIcon.setAnimation(loadingAnimation);
    }

    @Override
    public void setHintMsg(String hintMsg) {
        if (tvLoadHint != null) {
            tvLoadHint.setText(hintMsg);
        }
    }

    public void setHintMsg(int hintMsgResID) {
        setHintMsg(getStrFromResId(hintMsgResID));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        loadingAnimation.start();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        loadingAnimation.cancel();
    }
}
