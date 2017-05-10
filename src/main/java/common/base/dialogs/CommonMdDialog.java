package common.base.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
public class CommonMdDialog extends BaseDialog<CommonMdDialog>{
    private TextView tvDialogTitle;
    private TextView tvDialogHint;
    private TextView tvDialogCancel;
    private TextView tvDialogCommit;
    private EditText edtInDialog;
    private ListView lvItems;
    private ContentLayouType curContentLayoutType;
    private View llBottomBtnsLayout;
    private View mdDialogContentLayout;
    private int contentLayoutTop,contentLayoutBottom;
    /**
     * Dialog中 中间部分的内容布局类型：目前有三种，提示类、编辑输入类、ListView列表选择类
     */
    public enum ContentLayouType {
        HINT_MSG,
        EDIT_INPUT,
        LIST_SELECTIONS;
    }
    public CommonMdDialog(Context context) {
        this(context, android.R.style.Theme_Translucent_NoTitleBar);
    }
    public CommonMdDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected int getDialogViewResID() {
        return R.layout.common_md_dialog_layout;
    }

    @Override
    protected void initViews(View containerView) {
        ViewGroup containerLayout = (ViewGroup) containerView;
        mdDialogContentLayout = ViewUtil.findViewInContainer(containerLayout, R.id.common_md_dialog_content_layout);

        tvDialogTitle = ViewUtil.findViewInContainer(containerLayout, R.id.tv_dialog_title);
        tvDialogHint = ViewUtil.findViewInContainer(containerLayout, R.id.tv_4_dialog_msg);
        tvDialogCancel = ViewUtil.findViewInContainer(containerLayout, R.id.tv_btn_dialog_cancel);
        tvDialogCommit = ViewUtil.findViewInContainer(containerLayout, R.id.tv_btn_dialog_commit);
        if(tvDialogCancel != null){
            tvDialogCancel.setOnClickListener(this);
        }
        if (tvDialogCommit != null) {
            tvDialogCommit.setOnClickListener(this);
        }
        edtInDialog = ViewUtil.findViewInContainer(containerLayout, R.id.edt_as_dialog_msg_or_edit);
        lvItems = ViewUtil.findViewInContainer(containerLayout, R.id.lv_selection_items);
        lvItems.addHeaderView(getLayoutInflater().inflate(R.layout.gray_divider_line,null));
        llBottomBtnsLayout = ViewUtil.findViewInContainer(containerLayout, R.id.ll_btns);
        clearOldTexts();
    }

    /**
     * 把除取消、确定按钮外写在控件上的文本全部清空了
     * @return
     */
    public CommonMdDialog clearOldTexts() {
        if (tvDialogTitle != null) {
            tvDialogTitle.setText(R.string.hint);
        }
        if (tvDialogHint != null) {
            tvDialogHint.setText(null);
        }
        if (edtInDialog != null) {
            edtInDialog.setText(null);
            edtInDialog.setHint(null);
        }
        return self();
    }
    public CommonMdDialog switchContentLayoutType(ContentLayouType needLayoutType) {
        if (needLayoutType == null) {
            return self();
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
        if (llBottomBtnsLayout != null) {
            llBottomBtnsLayout.setVisibility(View.VISIBLE);
        }
        switch (needLayoutType) {
            case LIST_SELECTIONS:
                if (lvItems != null) {
                    lvItems.setVisibility(View.VISIBLE);
                }
                if (llBottomBtnsLayout != null) {
                    llBottomBtnsLayout.setVisibility(View.GONE);
                }
                break;
            case HINT_MSG:
                if (tvDialogHint != null) {
                    //modified by fee 2017-05-10,Dialog布局内容切换为HINT_MSG模式时应该是让tvDialogHint显示
                    tvDialogHint.setVisibility(View.VISIBLE);
                }
                break;
            case EDIT_INPUT:
                if (edtInDialog != null) {
                    edtInDialog.setVisibility(View.VISIBLE);
                }
                break;
        }
        curContentLayoutType = needLayoutType;
        return self();
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        int curClickedBtnType = DialogInterface.BUTTON_NEGATIVE;
        if (v == tvDialogCancel) {
            curClickedBtnType = DialogInterface.BUTTON_NEGATIVE;
            dismiss();
        } else if (v == tvDialogCommit) {
            curClickedBtnType = DialogInterface.BUTTON_POSITIVE;
        }
        if(dialogClickListener != null){
            dialogClickListener.onClick(this, curClickedBtnType);
        }
    }

    /**
     * 这里重写的是Dialog.java中的方法，原方法为给window设置title
     * @param title
     */
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
    public CommonMdDialog setHintMsg(String hintMsg) {
//        if (Util.isEmpty(hintMsg)) {
//            return;
//        }
        if (tvDialogHint != null) {
            tvDialogHint.setText(hintMsg);
        }
        return self();
    }
    @Override
    public CommonMdDialog setCancleBtnName(String cancleBtnName) {
        if (tvDialogCancel != null) {
            tvDialogCancel.setVisibility(View.VISIBLE);
            if (Util.isEmpty(cancleBtnName)) {
                tvDialogCancel.setText(R.string.cancel);
            }
            else{
                tvDialogCancel.setText(cancleBtnName);
                if ("hide".equals(cancleBtnName)) {
                    tvDialogCancel.setVisibility(View.GONE);
                }
            }
        }
        return self();
    }

    @Override
    public CommonMdDialog setCommitBtnName(String commitBtnName) {
        if (tvDialogCommit != null) {
            if (Util.isEmpty(commitBtnName)) {
                tvDialogCommit.setText(R.string.confirm);
            }
            else{
                tvDialogCommit.setText(commitBtnName);
            }
        }
        return self();
    }

    @Override
    public CommonMdDialog edtViewCanEdit(boolean needEdit) {
        if (needEdit) {
            switchContentLayoutType(ContentLayouType.EDIT_INPUT);
        }
        if (edtInDialog != null) {
            edtInDialog.setEnabled(needEdit);
        }
        return self();
    }

    @Override
    public CommonMdDialog toggleCancelBtnVisibility(boolean hideCancelBtn) {
        if (tvDialogCancel != null) {
            tvDialogCancel.setVisibility(hideCancelBtn ? View.GONE : View.VISIBLE);
        }
        return self();
    }

    public CommonMdDialog setAdapter4Lv(ListAdapter dataAdapter) {
        if (lvItems != null) {
            lvItems.setAdapter(dataAdapter);
        }
        return self();
    }

    public CommonMdDialog setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        if (lvItems != null) {
            lvItems.setOnItemClickListener(itemClickListener);
        }
        return self();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (cancelableOutSide) {
                    if (contentLayoutTop == 0) {
                        contentLayoutTop = mdDialogContentLayout.getTop();
                    }
                    if (contentLayoutBottom == 0) {
                        contentLayoutBottom = mdDialogContentLayout.getBottom();
                    }
                    float toutchedY = event.getY();
                    if (toutchedY < contentLayoutTop || toutchedY > contentLayoutBottom) {
                        cancel();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public CommonMdDialog setHintMsgGravity(int gravity) {
        if (tvDialogHint != null) {
            tvDialogHint.setGravity(gravity);
        }
        return self();
    }

    @Override
    public CommonMdDialog adjustDialogContentWH(int dpUnitW, int dpUnitH) {
        if (mdDialogContentLayout != null) {
            ViewGroup.LayoutParams vlp = mdDialogContentLayout.getLayoutParams();
            if (vlp != null) {
                vlp.width = Util.dip2px(mContext,dpUnitW);
                vlp.height = Util.dip2px(mContext, dpUnitH);
                mdDialogContentLayout.setLayoutParams(vlp);
            }
        }
        return self();
    }

    public CommonMdDialog adjustContentViewPaddingLR(int dpPaddingLR) {
        if (dialogView != null) {
            int paddingLrPix = Util.dip2px(mContext, dpPaddingLR);
            dialogView.setPadding(paddingLrPix, dialogView.getPaddingTop(), paddingLrPix, dialogView.getPaddingBottom());
        }
        return self();
    }

    public CommonMdDialog changeDialogContentBackground(int bgResId) {
        if (mdDialogContentLayout != null) {
            mdDialogContentLayout.setBackgroundResource(bgResId);
        }
        return self();
    }

    /**
     * 获取dialog中负责显示title的控件,从而外部就可以再定义其样式了
     *
     * @return
     */
    @Override
    public TextView getDialogTitleView() {
        return tvDialogTitle;
    }

    /**
     * 获取dialog中负责显示hint msg的控件,从而外部就可以再定义其样式了
     *
     * @return
     */
    @Override
    public <T extends View> T getDialogHintView() {
        return (T) tvDialogHint;
    }

    /**
     * 获取dialog中 取消按钮控件,从而外部就可以再定义其样式了
     *
     * @return
     */
    @Override
    public <T extends View> T getDialogCancelBtn() {
        return (T) tvDialogCancel;
    }

    /**
     * 获取dialog中确定按钮控件,从而外部就可以再定义其样式了
     *
     * @return
     */
    @Override
    public <T extends View> T getDialogCommitBtn() {
        return (T) tvDialogCommit;
    }
}
