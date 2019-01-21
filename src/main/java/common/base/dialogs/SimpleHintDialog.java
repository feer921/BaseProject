package common.base.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import common.base.R;
import common.base.utils.CheckUtil;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/1/19<br>
 * Time: 17:05<br>
 * <P>DESC:
 * 简单的提示性的dialog,含标题，提示内容，取消、确定按钮
 * </p>
 * ******************(^_^)***********************
 */
public class SimpleHintDialog extends BaseDialog<SimpleHintDialog> {
    private TextView tvTitle,tvHint,tvBtnCancel,tvBtnCommit;


    public SimpleHintDialog(Context context) {
        this(context, R.style.common_dialog_bg_dim);
    }

    /**
     * 该构造方法下调用了{@link #initViews(View)}
     *
     * @param context
     * @param theme
     */
    public SimpleHintDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * 构造方法中完成调用
     *
     * @param dialogView 子类所提供的内容视图
     */
    @Override
    protected void initViews(View dialogView) {
        if (dialogView != null) {
            tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
            tvHint = dialogView.findViewById(R.id.tv_dialog_hint);
            tvBtnCancel = dialogView.findViewById(R.id.tv_btn_dialog_cancel);
            tvBtnCommit = dialogView.findViewById(R.id.tv_btn_dialog_commit);
            if (tvBtnCancel != null) {
                tvBtnCancel.setOnClickListener(this);
            }
            if (tvBtnCommit != null) {
                tvBtnCommit.setOnClickListener(this);
            }
        }
    }
    @Override
    public void onClick(View v) {
        int curClickedBtnType = DialogInterface.BUTTON_NEGATIVE;
        if (v == tvBtnCancel) {
            curClickedBtnType = DialogInterface.BUTTON_NEGATIVE;
            dismiss();
        } else if (v == tvBtnCommit) {
            curClickedBtnType = DialogInterface.BUTTON_POSITIVE;
        }
        if(dialogClickListener != null){
            dialogClickListener.onClick(this, curClickedBtnType);
        }
    }

    /**
     * 获取本Dialog的布局视图资源ID
     *
     * @return
     */
    @Override
    protected int getDialogViewResID() {
        return R.layout.simple_hint_dialog;
    }

    public SimpleHintDialog withTitle(CharSequence dialogTitle) {
        if (CheckUtil.isEmpty(dialogTitle)) {
            dialogTitle = getStrFromResId(R.string.hint);
        }
        if (tvTitle != null) {
            tvTitle.setText(dialogTitle);
        }
        return self();
    }

    public SimpleHintDialog withDialogHint(CharSequence dialogHint) {
        if (tvHint != null) {
            tvHint.setText(dialogHint);
        }
        return self();
    }

    public SimpleHintDialog withCancelBtnText(CharSequence cancelBtnText) {
        if (CheckUtil.isEmpty(cancelBtnText)) {
            cancelBtnText = getStrFromResId(R.string.cancel_no_gap);
        }

        if (tvBtnCancel != null) {
            if ("HiDe".equals(cancelBtnText.toString())) {
                tvBtnCancel.setVisibility(View.GONE);
            }
            else{
                tvBtnCancel.setVisibility(View.VISIBLE);
            }
            tvBtnCancel.setText(cancelBtnText);
        }
        return self();
    }

    public SimpleHintDialog withCommitBtnText(CharSequence commitBtnText) {
        if (CheckUtil.isEmpty(commitBtnText)) {
            commitBtnText = getStrFromResId(R.string.confirm_no_gap);
        }
        if (tvBtnCommit != null) {
            tvBtnCommit.setText(commitBtnText);
        }
        return self();
    }

    @Override
    public TextView getDialogTitleView() {
        return tvTitle;
    }

    @Override
    public TextView getDialogCancelBtn() {
        return tvBtnCancel;
    }

    @Override
    public TextView getDialogCommitBtn() {
        return tvBtnCommit;
    }

    @Override
    public TextView getDialogHintView() {
        return tvHint;
    }
}
