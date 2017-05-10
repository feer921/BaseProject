package common.base.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import common.base.R;

public abstract class BaseDialog<I extends BaseDialog<I>> extends Dialog implements View.OnClickListener{
    protected OnClickListener dialogClickListener;
    protected View containerView;
    protected int contentLayoutResId;

    protected Context mContext;
    /**
     * 这两个变量，是调整加载Dialog内容视图的Window的整体宽、高
     */
    protected int dialogWidth,dialogHeigth;
    protected int dialogShowGrivity;
    protected int dialogAnimStyle;
    protected boolean cancelableOutSide = false;
    /**
     * 当前Dialog 所处的提示类型
     */
    public int curDialogInCase;
    public BaseDialog(Context context) {
        this(context, R.style.login_dialog);
    }


    public BaseDialog(Context context, int theme) {
        super(context, theme);
        containerView = getLayoutInflater().inflate(getContentViewResID(), null);
        initViews(containerView);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(cancelableOutSide);
        setContentView(getContainerView());
        Window w = getWindow();
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
        if(dialogAnimStyle != 0){
            setDialogAnimStyle(dialogAnimStyle);
        }
        w.setAttributes(lp);
    }

    /**
     * 构造方法中完成调用
     * @param containerView 子类所提供的内容视图
     */
    protected abstract void initViews(View containerView);

    protected int getContentViewResID(){
        return 0;
    }
    @Override
    public void onClick(View v) {
        
    }

    public I setDialogClickListener(OnClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
        return self();
    }


    public View getContainerView() {
        return containerView;
    }


    public I setContainerView(View containerView) {
        this.containerView = containerView;
        return self();
    }


    public I setContentLayoutResId(int contentLayoutResId) {
        this.contentLayoutResId = contentLayoutResId;
        return self();
    }

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

    public I setHintMsg(String hintMsg) {
        return self();
    }

    public TextView getTvHintMsg() {
        return null;
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

    public I setDialogHeigth(int dialogHeigth) {
        this.dialogHeigth = dialogHeigth;
        return self();
    }
    protected I self() {
        return (I) this;
    }
}
