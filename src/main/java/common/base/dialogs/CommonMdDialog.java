package common.base.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import common.base.R;
import common.base.utils.Util;
import common.base.utils.ViewUtil;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-15
 * Time: 13:49
 * DESC: 通用MD风格的Dialog
 */
public class CommonMdDialog extends BaseDialog{
    private TextView tvDialogTitle;
    private TextView tvDialogHint;
    private TextView tvDialogCancle;
    private TextView tvDialogCommit;
    private EditText edtInDialog;
    private ListView lvItems;
    private ContentLayouType curContentLayoutType;

    /**
     * Dialog中 中间部分的内容布局类型：目前有三种，提示类、编辑输入类、ListView列表选择类
     */
    public enum ContentLayouType {
        HINT_MSG,
        EDIT_INPUT,
        LIST_SELECTIONS;
    }
    public CommonMdDialog(Context context) {
        this(context, android.R.style.Theme_Translucent);
    }
    protected CommonMdDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected int getContentViewResID() {
        return R.layout.common_md_dialog_layout;
    }

    @Override
    protected void initViews(View containerView) {
        ViewGroup containerLayout = (ViewGroup) containerView;
        tvDialogTitle = ViewUtil.findViewInContainer(containerLayout, R.id.tv_dialog_title);
        tvDialogHint = ViewUtil.findViewInContainer(containerLayout, R.id.tv_4_dialog_msg);
        tvDialogCancle = ViewUtil.findViewInContainer(containerLayout, R.id.tv_btn_dialog_cancel);
        tvDialogCommit = ViewUtil.findViewInContainer(containerLayout, R.id.tv_btn_dialog_commit);
        if(tvDialogCancle != null){
            tvDialogCancle.setOnClickListener(this);
        }
        if (tvDialogCommit != null) {
            tvDialogCommit.setOnClickListener(this);
        }
        edtInDialog = ViewUtil.findViewInContainer(containerLayout, R.id.edt_as_dialog_msg_or_edit);
        lvItems = ViewUtil.findViewInContainer(containerLayout, R.id.lv_selection_items);
    }

    public void switchContentLayoutType(ContentLayouType needLayoutType) {
        if (needLayoutType == null) {
            return;
        }
        if (tvDialogHint != null) {
            tvDialogHint.setVisibility(View.GONE);
        }
        if (lvItems != null) {
            lvItems.setVisibility(View.GONE);
        }
        if (edtInDialog != null) {
            edtInDialog.setVisibility(View.GONE);
        }
        switch (needLayoutType) {
            case LIST_SELECTIONS:
                if (lvItems != null) {
                    lvItems.setVisibility(View.VISIBLE);
                }
                break;
            case HINT_MSG:
                if (tvDialogHint != null) {
                    tvDialogHint.setVisibility(View.GONE);
                }
                break;
            case EDIT_INPUT:
                if (edtInDialog != null) {
                    edtInDialog.setVisibility(View.VISIBLE);
                }
                break;
        }
        curContentLayoutType = needLayoutType;
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        int curClickedBtnType = DialogInterface.BUTTON_NEGATIVE;
        if (v == tvDialogCancle) {
            curClickedBtnType = DialogInterface.BUTTON_NEGATIVE;
            dismiss();
        } else if (v == tvDialogCommit) {
            curClickedBtnType = DialogInterface.BUTTON_POSITIVE;
        }
        if(dialogClickListener != null){
            dialogClickListener.onClick(this, curClickedBtnType);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        if (tvDialogTitle != null) {
            if (Util.isEmpty(title)) {
                tvDialogTitle.setText(R.string.hint);
            }
            else{
                tvDialogTitle.setText(title);
            }
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getStrFromResId(titleId));
    }

    @Override
    public void setHintMsg(String hintMsg) {
        if (Util.isEmpty(hintMsg)) {
            return;
        }
        if (tvDialogHint != null) {
            tvDialogHint.setText(hintMsg);
        }
    }

    @Override
    public void setCancleBtnName(String cancleBtnName) {
        if (tvDialogCancle != null) {
            if (Util.isEmpty(cancleBtnName)) {
                tvDialogCancle.setText(R.string.cancel);
            }
            else{
                tvDialogCancle.setText(cancleBtnName);
            }
        }
    }

    @Override
    public void setCommitBtnName(String commitBtnName) {
        if (tvDialogCommit != null) {
            if (Util.isEmpty(commitBtnName)) {
                tvDialogCommit.setText(R.string.confirm);
            }
            else{
                tvDialogCommit.setText(commitBtnName);
            }
        }
    }

    @Override
    public void edtViewCanEdit(boolean needEdit) {
        if (needEdit) {
            switchContentLayoutType(ContentLayouType.EDIT_INPUT);
        }
        if (edtInDialog != null) {
            edtInDialog.setEnabled(needEdit);
        }
    }

    @Override
    public void toggleCancelBtnVisibility(boolean hideCancelBtn) {
        if (tvDialogCancle != null) {
            tvDialogCancle.setVisibility(hideCancelBtn ? View.GONE : View.VISIBLE);
        }
    }

    public void setAdapter4Lv(ListAdapter dataAdapter) {
        if (lvItems != null) {
            lvItems.setAdapter(dataAdapter);
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        if (lvItems != null) {
            lvItems.setOnItemClickListener(itemClickListener);
        }
    }
}
