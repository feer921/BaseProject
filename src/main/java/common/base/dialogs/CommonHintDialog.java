package common.base.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import common.base.R;
import common.base.utils.Util;

/**
 * 一个通用的提示性对象框，适用有头部Title、取消、确定按钮，以及中部的EditText、TextView切换的Dialog效果
 */
public class CommonHintDialog extends BaseDialog<CommonHintDialog>{
    private TextView tvTitle;
    private EditText edtContentView;
    private TextView tvBtnCancle;
    private TextView tvBtnSure;
    private TextView tvHintMsg;
    
    public CommonHintDialog(Context context,int width,int height) {
        super(context, R.style.login_dialog);
        dialogWidth = width;
        dialogHeigth = height;
    }
    @Override
    protected int getContentViewResID() {
        return R.layout.common_dialog_layout;
    }
    @Override
    protected void initViews(View containerView) {
        tvTitle = (TextView) containerView.findViewById(R.id.tv_dialog_title);
        edtContentView = (EditText)containerView.findViewById(R.id.edt_as_dialog_msg_or_edit);
        tvBtnCancle = (TextView)containerView.findViewById(R.id.tv_btn_dialog_cancel);
        tvBtnSure = (TextView)containerView.findViewById(R.id.tv_btn_dialog_commit);
        tvHintMsg = (TextView)containerView.findViewById(R.id.tv_4_dialog_msg);
        tvBtnCancle.setOnClickListener(this);
        tvBtnSure.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        int viewId = v.getId();
        int buttonType = DialogInterface.BUTTON_POSITIVE;
        if (v == tvBtnCancle) {
            buttonType = DialogInterface.BUTTON_NEGATIVE;
            dismiss();
        } else if (v == tvBtnSure) {

        }
//        switch (viewId) {
//            case R.id.tv_btn_dialog_cancel:
//                buttonType = DialogInterface.BUTTON_NEGATIVE;
//                dismiss();
//                break;
//            case R.id.tv_btn_dialog_commit:
//                break;
//        }
        if(dialogClickListener != null){
            dialogClickListener.onClick(this, buttonType);
        }
    }
    @Override
    public void setTitle(CharSequence title) {
        if(tvTitle != null){
            if(Util.isEmpty(title)){
                tvTitle.setText(R.string.hint);
            }
            else{
                tvTitle.setText(title);
            }
        }
    }
    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getString(titleId));
    }
    public CommonHintDialog setCancleBtnName(String cancelBtnName){
        if(tvBtnCancle != null){
            tvBtnCancle.setVisibility(View.VISIBLE);
            if(Util.isEmpty(cancelBtnName)){
                tvBtnCancle.setText(R.string.cancel);
            }
            else{
                tvBtnCancle.setText(cancelBtnName);
                if ("hide".equals(cancelBtnName)) {
                    tvBtnCancle.setVisibility(View.GONE);
                }
            }
        }
        return self();
    }
    public CommonHintDialog setCommitBtnName(String CommitBtnName){
        if(tvBtnSure != null){
            if(Util.isEmpty(CommitBtnName)){
                tvBtnSure.setText(R.string.confirm);
            }
            else{
                tvBtnSure.setText(CommitBtnName);
            }
        }
        return self();
    }
    /**
     * 让EditTex 是否可编辑，如果不可编辑，则显示tvHintMsg控件以承载dialog消息
     * @param needEdit
     */
    public CommonHintDialog edtViewCanEdit(boolean needEdit){
        if(edtContentView != null){
            edtContentView.setEnabled(needEdit);
            edtContentView.setVisibility(needEdit ? View.VISIBLE : View.INVISIBLE);
        }
        if(tvHintMsg != null){
            tvHintMsg.setVisibility(needEdit ? View.INVISIBLE : View.VISIBLE);
        }
        return self();
    }

    public CommonHintDialog setHintMsg(String hintMsg) {
        if (tvHintMsg != null) {
            tvHintMsg.setText(hintMsg);
        }
        return self();
    }
    public EditText getTheEditText(){
        if(edtContentView == null){
            edtContentView = (EditText) findViewById(R.id.edt_as_dialog_msg_or_edit);
        }
        return edtContentView;
    }
    /**
     * 让取消按钮显示或者隐藏
     * @param ignoreAndHideIt
     */
    public CommonHintDialog toggleCancelBtnVisibility(boolean ignoreAndHideIt){
        if(tvBtnCancle != null){
            tvBtnCancle.setVisibility(ignoreAndHideIt ? View.GONE : View.VISIBLE);
        }
        return self();
    }
}
