package common.base.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import common.base.R;
import common.base.activitys.IProxyCallback;
import common.base.utils.ViewUtil;

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
    ProgressBar pb;
    public CommonProgressDialog(Context context) {
        this(context, 0);
    }

    public CommonProgressDialog(Context context, int theme) {
        super(context, theme);
//        initView();
    }

    public CommonProgressDialog withAnim(@DrawableRes int animResId) {
        if (this.animResId != animResId) {
            if (mIvLoadingAnim != null) {
                mIvLoadingAnim.setBackgroundResource(animResId);
                mAnimBgDrawable = (AnimationDrawable) mIvLoadingAnim.getBackground();
            }
        }
        if (pb != null) {
            pb.setVisibility(View.GONE);
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
        clearOldMsg();
        if (!isShowing()) {
            show();
        }
    }

    public void over() {
        dismiss();
    }

    @Override
    public void show() {
        clearOldMsg();
        super.show();
        showAnim();
    }
    @Override
    public void dismiss() {
        clearOldMsg();
//        CommonLog.e("info","-------CommonProgressDialog-------dismiss()---------------------------");
        if (mAnimBgDrawable != null) {
            mAnimBgDrawable.stop();
        }
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
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

    public CommonProgressDialog withContentView(View contentView) {
        this.contentView = contentView;
        initView();
        return this;
    }
    private void initView() {
        if (contentView == null) {
            contentView = getLayoutInflater().inflate(R.layout.common_progress_dialog_layout2, null);
        }
        mTvLoadingHint = ViewUtil.findViewInView(contentView, R.id.tv_dialog_load_hint);
        mIvLoadingAnim = ViewUtil.findViewInView(contentView, R.id.iv_dialog_loading);
        try {
            pb = ViewUtil.findViewInView(contentView, R.id.loading_progress);
        } catch (Exception ignore) {

        }
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

    public void dismissDelay(long delayTimeMs) {
        if (delayTimeMs > 0) {
            if (mHandler == null) {
                mHandler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        int msgWhat = msg.what;
                        switch (msgWhat) {
                            case MSG_DELAY_TO_DISMISS:
                                dismiss();
                                break;
                        }
                    }
                };
            }
            clearOldMsg();
            mHandler.sendEmptyMessageDelayed(MSG_DELAY_TO_DISMISS, delayTimeMs);
        }
    }

    private void clearOldMsg() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
    private static final int MSG_DELAY_TO_DISMISS = 0x10;
    private Handler mHandler;
}
