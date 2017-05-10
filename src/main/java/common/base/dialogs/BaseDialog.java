package common.base.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseDialog<I extends BaseDialog<I>> extends Dialog implements View.OnClickListener{
    protected OnClickListener dialogClickListener;
    /**
     * 对话框的布局视图
     */
    View dialogView;
//    protected int contentLayoutResId;

    protected Context mContext;
    /**
     * 这两个变量，是调整加载Dialog内容视图的Window的整体宽、高
     */
    protected int dialogWidth,dialogHeigth;
    protected int dialogShowGrivity;
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
    public BaseDialog(Context context) {
        this(context, android.R.style.Theme_Translucent_NoTitleBar);
    }


    public BaseDialog(Context context, int theme) {
        super(context, theme);
        if (getDialogViewResID() > 0) {
            dialogView = getLayoutInflater().inflate(getDialogViewResID(), null);
        }
        else{
            dialogView = getDialogView();
        }
        initViews(dialogView);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(cancelableOutSide);
        setContentView(dialogView);
        Window w = getWindow();
        if (w != null) {
            WindowManager.LayoutParams lp = w.getAttributes();
            if(dialogWidth > 0 ){
                lp.width = dialogWidth;
            }
            else{
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            }
            if(dialogHeigth > 0){
                lp.height = dialogHeigth;
            }
            else{
                //            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }
            if(dialogShowGrivity != 0){
                lp.gravity = dialogShowGrivity;
            }
            w.setAttributes(lp);
        }
        if(dialogAnimStyle != 0){//???没什么作用目前
            setDialogAnimStyle(dialogAnimStyle);
        }
    }

    /**
     * 构造方法中完成调用
     * @param containerView 子类所提供的内容视图
     */
    protected abstract void initViews(View containerView);

    /**
     * 获取本Dialog的布局视图资源ID
     * @return
     */
    protected abstract int getDialogViewResID();
    @Override
    public void onClick(View v) {
        
    }

    public I setDialogClickListener(OnClickListener dialogClickListener) {
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

    public I setDialogShowGrivity(int dialogShowGrivity) {
        this.dialogShowGrivity = dialogShowGrivity;
        return self();
    }

    public I setDialogAnimStyle(int dialogAnimStyle) {
        this.dialogAnimStyle = dialogAnimStyle;
        return self();
    }
    protected String getStrFromResId(int resID){
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
    protected I self() {
        return (I) this;
    }
}
