package common.base.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import android.view.View;
import android.widget.TextView;
import common.base.R;
import common.base.activitys.IProxyCallback;
import common.base.utils.ViewUtil;
import common.base.views.SuperEmptyLoadingView;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/10/17<br>
 * Time: 14:10<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */
public class SimpleDialogLoading extends android.app.ProgressDialog implements DialogInterface.OnCancelListener{
    private SuperEmptyLoadingView loadingView;
    private View contentView;
    private IProxyCallback callback;
    private int animResId;

    private boolean isAttachToWindowManager = false;
//    public SimpleDialogLoading(Context context) {
//        this(context,0);
//    }

    public SimpleDialogLoading(Context context, int theme) {
        super(context, theme);
        initView();
    }

    public SimpleDialogLoading withAnim(@DrawableRes int animResId) {
        if (this.animResId != animResId) {
            if (loadingView != null) {
                loadingView.withLoadingAnim(animResId);
            }
        }
        this.animResId = animResId;
        return this;
    }
    public SimpleDialogLoading withHint(@StringRes int hintStrResId) {
        if (loadingView != null) {
            loadingView.withHintMsg(SuperEmptyLoadingView.LayoutStatus.Loading, hintStrResId);
        }
        return this;
    }

    public SimpleDialogLoading withHint(String hintMsg) {
        if (loadingView != null) {
            loadingView.withLoadingHint(hintMsg);
        }
        return this;
    }
    public SimpleDialogLoading withCancel(boolean cancelAble) {
        setCancelable(cancelAble);
        return this;
    }

    public SimpleDialogLoading withCancelOutside(boolean cancelOutside) {
        setCanceledOnTouchOutside(cancelOutside);
        return this;
    }
    public SimpleDialogLoading withCancelCallback(IProxyCallback cancelCallback) {
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
        return loadingView == null ? null : loadingView.getTvHintMsg();
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
        isAttachToWindowManager = true;//主动设置为true，因为该方法的回调会比较晚
        clearOldMsg();
        super.show();
        showAnim();
    }
    @Override
    public void dismiss() {
        clearOldMsg();
        if (loadingView != null) {
            loadingView.reset();
        }
        //防止dismiss的时候，window已无
//        if (getWindow() == null) {
//            return;
//        }
        if (!isAttachToWindowManager) {
            return;
        }
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtil.hideNavigation(getWindow());
        initView();
        setContentView(contentView);
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        if (hasFocus) {
//            ViewUtil.hideNavigationBar(getWindow());
//        }
//    }

    private void showAnim() {
        if (loadingView != null) {
            loadingView.loading();
        }
    }

    public SimpleDialogLoading withContentView(View contentView) {
        this.contentView = contentView;
        initView();
        return this;
    }
    private void initView() {
        if (contentView == null) {
            contentView = getLayoutInflater().inflate(R.layout.simple_dialog_loading, null);
            loadingView = contentView.findViewById(R.id.empty_layout_4_loading);
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

    @Override
    public void onAttachedToWindow() {
        //该方法的回调有可能比较晚
        isAttachToWindowManager = true;
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        isAttachToWindowManager = false;
        super.onDetachedFromWindow();
    }
}
