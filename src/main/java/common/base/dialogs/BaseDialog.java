package common.base.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import common.base.R;

public abstract class BaseDialog extends Dialog implements View.OnClickListener{
    protected OnClickListener dialogClickListener;
    protected View containerView;
    protected int contentLayoutResId;
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
    protected abstract void initViews(View containerView);

    protected int getContentViewResID(){
        return 0;
    }
    @Override
    public void onClick(View v) {
        
    }

    public void setDialogClickListener(OnClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
    }


    public View getContainerView() {
        return containerView;
    }


    public void setContainerView(View containerView) {
        this.containerView = containerView;
    }


    public void setContentLayoutResId(int contentLayoutResId) {
        this.contentLayoutResId = contentLayoutResId;
    }

    public void setDialogShowGrivity(int dialogShowGrivity) {
        this.dialogShowGrivity = dialogShowGrivity;
    }

    public void setDialogAnimStyle(int dialogAnimStyle) {
        this.dialogAnimStyle = dialogAnimStyle;
    }
    protected String getStrFromResId(int resID){
        return getContext().getResources().getString(resID);
    }

    public void setHintMsg(String hintMsg) {

    }

    public void setCancleBtnName(String cancleBtnName) {

    }

    public void setCommitBtnName(String commitBtnName) {

    }
    public void edtViewCanEdit(boolean needEdit){

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
    public void toggleCancelBtnVisibility(boolean hideCancelBtn){

    }
}
