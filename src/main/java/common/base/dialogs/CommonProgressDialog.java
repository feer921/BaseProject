package common.base.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import common.base.R;
import common.base.activitys.IProxyCallback;

/**
 * ******************(^_^)***********************<br>
 * User: 11776610771@qq.com<br>
 * Date: 2017/5/18<br>
 * Time: 14:52<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */

public class CommonProgressDialog extends ProgressDialog implements DialogInterface.OnCancelListener{

    private AnimationDrawable mAnimBgDrawable;
    private ImageView mIvLoadingAnim;
    private TextView mTvLoadingHint;
    private View contentView;
    private IProxyCallback callback;
    private int animResId;
    public CommonProgressDialog(Context context) {
        this(context, 0);
    }

    public CommonProgressDialog(Context context, int theme) {
        super(context, theme);
        initView();
    }

    public CommonProgressDialog withAnim(@DrawableRes int animResId) {
        if (this.animResId != animResId) {
            if (mIvLoadingAnim != null) {
                mIvLoadingAnim.setBackgroundResource(animResId);
                mAnimBgDrawable = (AnimationDrawable) mIvLoadingAnim.getBackground();
            }
        }
        this.animResId = animResId;
        return this;
    }
    public CommonProgressDialog withHint(@StringRes int hintStrResId) {
        if (mTvLoadingHint != null) {
            mTvLoadingHint.setText(hintStrResId);
        }
        return this;
    }

    /**
     * @param hintTextColor A color value in the form 0xAARRGGBB.
     * @return self
     */
    public CommonProgressDialog withHintTextColor(@ColorInt int hintTextColor) {
        if (mTvLoadingHint != null) {
            mTvLoadingHint.setTextColor(hintTextColor);
        }
        return this;
    }
    public CommonProgressDialog withHint(String hintMsg) {
        if (mTvLoadingHint != null) {
            mTvLoadingHint.setText(hintMsg);
        }
        return this;
    }
    public CommonProgressDialog withCancel(boolean cancelAble) {
        setCancelable(cancelAble);
        return this;
    }

    public CommonProgressDialog withCancelOutside(boolean cancelOutside) {
        setCanceledOnTouchOutside(cancelOutside);
        return this;
    }
    public CommonProgressDialog withCancelCallback(IProxyCallback cancelCallback) {
        this.callback = cancelCallback;
        if (cancelCallback != null) {
            setOnCancelListener(this);
        }
        else{
            setOnCancelListener(null);
        }
        return this;
    }
    public TextView getHintView() {
        return mTvLoadingHint;
    }
    public void run() {
        show();
    }

    public void over() {
        dismiss();
    }

    @Override
    public void show() {
        super.show();
        showAnim();
    }

    @Override
    public void dismiss() {
//        CommonLog.e("info","-------CommonProgressDialog-------dismiss()---------------------------");
        if (mAnimBgDrawable != null) {
            mAnimBgDrawable.stop();
        }
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentView);
    }

    private void showAnim() {
        if (mIvLoadingAnim != null) {
            if (mAnimBgDrawable != null && !mAnimBgDrawable.isRunning()) {
                mIvLoadingAnim.post(new Runnable() {
                    @Override
                    public void run() {
                        mAnimBgDrawable.start();
                    }
                });
            }
        }
    }
    private void initView() {
        contentView = getLayoutInflater().inflate(R.layout.common_progress_dialog_layout, null);
        mTvLoadingHint = (TextView) contentView.findViewById(R.id.tv_dialog_load_hint);
        mIvLoadingAnim = (ImageView) contentView.findViewById(R.id.iv_dialog_loading);
    }

    /**
     * This method will be invoked when the dialog is canceled.
     *
     * @param dialog The dialog that was canceled will be passed into the
     *               method.
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        if (callback != null) {
            callback.ownerToCancelLoadingRequest();
        }
    }
}
