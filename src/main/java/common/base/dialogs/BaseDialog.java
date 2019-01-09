package common.base.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import common.base.R;
import common.base.utils.CommonLog;
import common.base.utils.Util;
import common.base.utils.ViewUtil;

public abstract class BaseDialog<I extends BaseDialog<I>> extends Dialog implements View.OnClickListener{
    protected final String TAG = getClass().getSimpleName();
    protected DialogInterface.OnClickListener dialogClickListener;
    /**
     * 对话框的布局视图
     */
    View dialogView;
//    protected int contentLayoutResId;

    protected Context mContext;
    /**
     * 这两个变量，是调整加载Dialog内容视图的Window的整体宽、高
     * 如果没有指定，系统会有默认的
     */
    protected int dialogWidth,dialogHeigth;
    /**
     * Dialog 在window中的重心位置
     */
    protected int dialogShowGravity;
    private int dialogOffsetX,dialogOffsetY;
    /**
     * Dialog 进、出的动画效果
     */
    protected int dialogAnimStyle;
    /**
     * 点击Dialog内容外部空间时，是否能dismiss
     * 默认为点击dialog外部不dismiss
     */
    protected boolean cancelableOutSide = false;
    /**
     * 当前Dialog 所处的提示类型
     */
    public int curDialogInCase;



    protected float dialogBgBehindAlpha = -1;
    /**
     * 初始化时，Dialog的Window是否使用内容布局的宽
     * def : false
     */
    protected boolean isWindowUseContentViewW = false;
    protected boolean isWindowUseContentViewH = false;
    @ColorInt
    protected int dialogWindowBgColor = -1;
    /**
     * Dialog弹出时是否要关心Activity(应用Window)有沉浸式效果，如果不关心，会导致Dialog弹出时Activity退出沉浸式效果
     * <P>https://www.jianshu.com/p/d10dd0c1a344</P>
     * def: true
     */
    protected boolean needCareActivityImmersion = true;
    public BaseDialog(Context context) {
//        this(context, android.R.style.Theme_Material_Light_Dialog_Alert);//android.R.style.Theme_Material_Light_Dialog_Alert//这个style不错
        this(context, R.style.common_dialog_bg_dim);
    }

    /**
     * 该构造方法下调用了{@link #initViews(View)}
     * @param context
     * @param theme
     */
    public BaseDialog(Context context, int theme) {
        super(context, theme);
        if (getDialogViewResID() > 0) {
            dialogView = getLayoutInflater().inflate(getDialogViewResID(), null);
        }
        else{
            dialogView = getDialogView();
        }
        mContext = context;
        initViews(dialogView);
    }

    public void setDialogWindowBgColor(@ColorInt int dialogWindowBgColor) {
        this.dialogWindowBgColor = dialogWindowBgColor;
    }

    public void setDialogWindowBg(Drawable windowBgDrawable) {
        if (windowBgDrawable == null) {
            return;
        }
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setBackgroundDrawable(windowBgDrawable);
        }
    }

    public void setDialogWindowBgRes(@DrawableRes int bgResId) {
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setBackgroundDrawableResource(bgResId);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if (needCareActivityImmersion) {
//            ViewUtil.hideNavigationBar(getWindow());
//        }
        //added by fee 2018-05-26
        if (mOnDialogPreCreateListener != null) {
            mOnDialogPreCreateListener.onDialogViewCreated(this);
        }

        setCanceledOnTouchOutside(cancelableOutSide);
        setContentView(dialogView);
        configDialogWindow();
    }

    /**
     * show()-->[onCreate()如果没有创建的话]--->onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (needCareActivityImmersion) {
            ViewUtil.hideNavigationBar(getWindow());
        }
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        if (hasFocus && needCareActivityImmersion) {
//            ViewUtil.hideNavigationBar(getWindow());
//        }
//    }


    protected void configDialogWindow() {
        Window w = getWindow();//该窗口是控制Dialog window窗口的
        if (w != null) {
            WindowManager.LayoutParams lp = w.getAttributes();//默认Dialog的Window的width和height都是WRAP_CONTENT(-2) //更正：2018-06-06：也不一定是-2，也有-1,初步结论为与dialog的style有关
            ViewGroup.LayoutParams dialogViewLp =  dialogView.getLayoutParams();
            CommonLog.e("info", TAG + "--> onCreate() window.lp.height = " + lp.height + "  window.lp.width = " + lp.width + "  dialogViewLp=" + dialogViewLp);
            if(dialogWidth > 0 ){
                lp.width = dialogWidth;
            }
            else{
                //lp.width = WindowManager.LayoutParams.MATCH_PARENT 会使得该Dialog的窗口的宽与手机屏幕的宽相同
                if (dialogViewLp != null && isWindowUseContentViewW) {
                    lp.width  = dialogViewLp.width;//hint by fee 2018-06-06:就算在dialogView中指定了宽、高，也还是-1???
//                    CommonLog.e("info", "---->^^^^^^^^^  " + lp.width +" " +
//                            " MeasuredWidth= "  + dialogView.getMeasuredWidth()+  // 0
//                            " getMeasuredHeight =" + dialogView.getMeasuredHeight()); // 0
                }
            }
            if(dialogHeigth > 0){
                lp.height = dialogHeigth;
            }
            else{
//                lp.height = WindowManager.LayoutParams.MATCH_PARENT;//不加上这句，默认高度会match_parent ; changed by fee 2017-07-14:
                if (dialogViewLp != null && isWindowUseContentViewH) {//让本来由Dialog原生设置的窗口宽、高转为由dialogView来决定Dialog的宽、高
                    lp.height = dialogViewLp.height;//注意：内容布局里的高度很可能就是MATCH_PARENT,为什么内容布局里是wrap_content 也是Match_parent

                }
            }
            if(dialogAnimStyle != 0){
                w.setWindowAnimations(dialogAnimStyle);
            }
            if(dialogShowGravity != 0){
                lp.gravity = dialogShowGravity;
            }
            //add 背景透明度调整 2017-10-21
            if (dialogBgBehindAlpha != -1) {
                lp.alpha = dialogBgBehindAlpha;
                //若不显示设置FLAG_DIM_BEHIND参数在大多数手机上也能work，但是在某些手机如华为Mate7上不能正常work，显示设置之后能够适配更多机型。
                w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
            lp.x = dialogOffsetX;
            lp.y = dialogOffsetY;
            if (dialogWindowBgColor != -1) {
                w.setBackgroundDrawable(new ColorDrawable(dialogWindowBgColor));
            }
            CommonLog.e("info", TAG + "--> onCreate() lp.height = " + lp.height + " lp.width = " + lp.width
                    + " dialogWindowBgColor= " + dialogWindowBgColor

            );
            w.setAttributes(lp);
        }
    }
    /**
     * 构造方法中完成调用
     * @param dialogView 子类所提供的内容视图
     */
    protected abstract void initViews(View dialogView);

    /**
     * 获取本Dialog的布局视图资源ID
     * @return
     */
    protected abstract int getDialogViewResID();
    @Override
    public void onClick(View v) {
        
    }

    public I setDialogClickListener(DialogInterface.OnClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
        return self();
    }


    public View getDialogView() {
        return dialogView;
    }

//    /**
//     * 各子类按需需要
//     * @param dialogView
//     * @return
//     */
//    public I setContainerView(View dialogView) {
//        this.dialogView = dialogView;
//        return self();
//    }
//
//
//    public I setContentLayoutResId(int contentLayoutResId) {
//        this.contentLayoutResId = contentLayoutResId;
//        return self();
//    }

    public I setDialogShowGravity(int dialogShowGravity) {
        changeDialogGravityInfo(dialogShowGravity, 0, 0);
        return self();
    }

    public I changeDialogAnimStyle(@StyleRes int dialogAnimStyle) {
        if (this.dialogAnimStyle != dialogAnimStyle) {
            this.dialogAnimStyle = dialogAnimStyle;
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(dialogAnimStyle);
            }
        }
        return self();
    }

    /**
     * //根据重力方向，x，y坐标设置窗口需要显示的位置
     * @param gravity
     * @param x
     * @param y
     * @return
     */
    public I changeDialogGravityInfo(int gravity, int x, int y) {
        boolean needToReset = false;
        if (this.dialogShowGravity != gravity) {
            needToReset = true;
            this.dialogShowGravity = gravity;
        }
        if (dialogOffsetX != x) {
            this.dialogOffsetX = x;
            needToReset = true;
        }
        if (dialogOffsetY != y) {
            this.dialogOffsetY = y;
            needToReset = true;
        }
        if (needToReset) {
            Window w = getWindow();
            if (w != null) {
                WindowManager.LayoutParams wlp = w.getAttributes();
                if (wlp != null) {
                    wlp.gravity = gravity;
                    wlp.x = x;
                    wlp.y = y;
                    w.setAttributes(wlp);
                }
            }
        }
        return self();
    }
    protected String getStrFromResId(@StringRes int resID){
        return getContext().getResources().getString(resID);
    }

    /**
     * 设置提示对话框的提示标题
     * @param title
     * @return
     */
    public I setHintTitle(String title) {
        setTitle(title);
        return self();
    }
    public I setHintMsg(String hintMsg) {
        return self();
    }

    /**
     * 由于可能一些界面上需要提示信息以不同的对齐方式显示，故添加此方法
     * @param gravity
     */
    public I setHintMsgGravity(int gravity) {
        return self();
    }
    public I setCancleBtnName(String cancleBtnName) {
        return self();
    }

    public I setCommitBtnName(String commitBtnName) {
        return self();
    }
    public I edtViewCanEdit(boolean needEdit){
        return self();
    }

    /**
     * Sets whether this dialog is canceled when touched outside the window's
     * bounds. If setting to true, the dialog is set to be cancelable if not
     * already set.
     *
     * @param cancel Whether the dialog should be canceled when touched outside
     *               the window.
     */
    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        this.cancelableOutSide = cancel;
    }

    public I setCanceledOnTouchOut(boolean cancelableOutSide) {
        setCanceledOnTouchOutside(cancelableOutSide);
        return self();
    }
    /**
     * 是否隐藏“取消”按钮
     * @param hideCancelBtn
     */
    public I toggleCancelBtnVisibility(boolean hideCancelBtn){
        return self();
    }

    /**
     * 调整Dialog内的内容的宽，高
     * @param dpUnitW dp值的宽
     * @param dpUnitH dp值的高
     * @return
     */
    public I adjustDialogContentWH(int dpUnitW, int dpUnitH) {
        return self();
    }
    public I adjustContentViewPaddingLR(int dpPaddingLR) {
        if (dialogView != null && dpPaddingLR >= 0) {
            int paddingLrPix = Util.dip2px(mContext, dpPaddingLR);
            dialogView.setPadding(paddingLrPix, dialogView.getPaddingTop(), paddingLrPix, dialogView.getPaddingBottom());
        }
        return self();
    }
    public I setDialogWidth(int dialogWidth) {
        this.dialogWidth = dialogWidth;
        return self();
    }

    public I setDialogHeight(int dialogHeigth) {
        this.dialogHeigth = dialogHeigth;
        return self();
    }

    /**
     * 获取dialog中负责显示title的控件,从而外部就可以再定义其样式了
     * @param <T>
     * @return
     */
    public <T extends View> T getDialogTitleView() {
        return null;
    }

    /**
     * 获取dialog中负责显示hint msg的控件,从而外部就可以再定义其样式了
     * @param <T>
     * @return
     */
    public <T extends View> T getDialogHintView() {
        return null;
    }
    /**
     * 获取dialog中 取消按钮控件,从而外部就可以再定义其样式了
     * @param <T>
     * @return
     */
    public <T extends View> T getDialogCancelBtn() {
        return null;
    }
    /**
     * 获取dialog中确定按钮控件,从而外部就可以再定义其样式了
     * @param <T>
     * @return
     */
    public <T extends View> T getDialogCommitBtn() {
        return null;
    }

    /**
     * 供外部调用来获取当前Dialog内部的View
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getViewFromDialog(@IdRes int viewId) {
        if (dialogView == null) {
            return null;
        }
//        ViewGroup viewGroup = (ViewGroup) dialogView;
        return (T) dialogView.findViewById(viewId);
    }

    protected Handler mHandler;


    protected static final int MSG_TYPE_DISMISS = 0x10;
    /**
     * 显示维持时间
     */
    private long showHoldTime = 0;
    public I showHoldTime(long showTimeInMs){
        this.showHoldTime = showTimeInMs;
//        clearShowHoldTaskInfo();//如果已经show了，不能移除show hold task,不然就不能自动dismiss了
        return self();
    }

    public I withHoldTimeAndShow(long showHoldTimeMills) {
        clearShowHoldTaskInfo();
        this.showHoldTime = showHoldTimeMills;
        show();
//        if (!isShowing()) {

//        }
        return self();
    }


    protected void handlerMsg(Message msg) {
        if (msg == null) {
            return;
        }
        int what = msg.what;
        switch (what) {
            case MSG_TYPE_DISMISS:
                if (isShowing()) {
                    dismiss();
                }
                break;
        }
    }
    @Override
    public void show() {
//        if (needCareActivityImmersion) {
//            // Dialog 在初始化时会生成新的 Window，先禁止 Dialog Window 获取焦点，等 Dialog 显示后对
//            // Dialog Window 的 DecorView 设置 setSystemUiVisibility ，接着再获取焦点。 这样表面上看起来就没有退出沉浸模式。
//            // Set the dialog to not focusable (makes navigation ignore us adding the window)
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//        }
        super.show();
//        if (needCareActivityImmersion) {
//            //Set the dialog to immersive
//            if (getWindow() != null) {
//                ViewUtil.fullScreenImmersive(getWindow().getDecorView());
//                //Clear the not focusable flag from the window
//                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//            }
//        }
        if (showHoldTime > 0) {
            if (mHandler == null) {
                mHandler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        handlerMsg(msg);
                    }
                };
            }
            mHandler.removeMessages(MSG_TYPE_DISMISS);
            mHandler.sendEmptyMessageDelayed(MSG_TYPE_DISMISS, showHoldTime);
        }
    }

    public void showInCase(int curDialogInCase) {
        this.curDialogInCase = curDialogInCase;
        show();
    }
    @Override
    public void dismiss() {
        clearShowHoldTaskInfo();
        super.dismiss();
    }

    public I clearShowHoldTaskInfo() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        return self();
    }
    protected I self() {
        return (I) this;
    }

    protected <T extends View> T viewInDialogView(int viewId) {
        if (dialogView == null || viewId <= 0) {
            return null;
        }
        return (T) dialogView.findViewById(viewId);
    }

    public int[] screenWhInfos() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return new int[]{
          wm.getDefaultDisplay().getWidth(),
                wm.getDefaultDisplay().getHeight()
        };
    }

    public I setDialogBgBehindAlpha(float dialogBgBehindAlpha) {
        this.dialogBgBehindAlpha = dialogBgBehindAlpha;
        return self();
    }

    public I useContentViewWidth(boolean isWindowUseContentViewW) {
        this.isWindowUseContentViewW = isWindowUseContentViewW;
        return self();
    }
    private OnDialogPreCreateListener mOnDialogPreCreateListener;
    public I withOnDialogPreCreateListener(OnDialogPreCreateListener listener) {
        this.mOnDialogPreCreateListener = listener;
        return self();
    }
    interface OnDialogPreCreateListener{
        /**
         * 在Dialog的onCreate()方法中回调出来，可以让监听者再初始化一下视图等
         * 目前添加为测试
         * @param theDialog
         */
        void onDialogViewCreated(BaseDialog theDialog);
    }
}
