package common.base.dialogs;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import common.base.R;
import common.base.utils.Util;
import common.base.utils.ViewUtil;

/**
 * ******************(^_^)***********************<br>
 * User: 11776610771@qq.com<br>
 * Date: 2017/11/12<br>
 * Time: 15:29<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */

public class SuperCommonMdDialog extends BaseDialog<SuperCommonMdDialog> {
    //要方便子类来使用？
    protected LinearLayout dialogContentRootView;
    protected RelativeLayout rlTopWrapTitleView;
    protected TextView tvTopCancel;
    protected TextView tvTopConfirm;
    protected TextView tvTopTitle;
    protected View dividerBelowTopTitle;
    protected TextView tvDialogTitle;
    protected View dividerBelowTitle;
    protected View dividerAboveBottomBtns;
    protected LinearLayout llBottomBtnContainer;
    protected TextView tvBottomCancel;
    protected TextView tvBottomConfirm;

    protected View dividerBetweenBottmBtns;
    //提示内容
    protected TextView tvDialogHint;
    protected EditText edtInDialog;
    protected RecyclerView recyclerViewInDialog;
    public SuperCommonMdDialog(Context context) {
        this(context,R.style.common_dialog_bg_dim);
    }

    /**
     * 该构造方法下调用了{@link #initViews(View)}
     *
     * @param context
     * @param theme
     */
    public SuperCommonMdDialog(Context context, int theme) {
        super(context, theme);
    }
    public EditText getEdtInDialog() {
        return edtInDialog;
    }

    public SuperCommonMdDialog withHint(CharSequence hintText) {
        if (tvDialogHint != null) {
            tvDialogHint.setText(hintText);
        }
        return self();
    }

    @Override
    public SuperCommonMdDialog setHintMsg(String hintMsg) {
        return withHint(hintMsg);
    }

    public SuperCommonMdDialog withRecyclerViewAdapter(BaseQuickAdapter adapter) {
        if (recyclerViewInDialog != null) {
            recyclerViewInDialog.setAdapter(adapter);
        }
        return self();
    }
    public SuperCommonMdDialog withHintTextColor(@ColorInt int textColor) {
        if (tvDialogHint != null) {
            tvDialogHint.setTextColor(textColor);
        }
        return self();
    }
    public SuperCommonMdDialog withHintTextGravity(int gravity) {
        if (tvDialogHint != null) {
            tvDialogHint.setGravity(gravity);
        }
        return self();
    }
    public SuperCommonMdDialog withHintTextSize(float spSize) {
        if (tvDialogHint != null) {
            tvDialogHint.setTextSize(spSize);
        }
        return self();
    }
    public SuperCommonMdDialog visiableDividerBetweenBtns(boolean visiable) {
        if (dividerBetweenBottmBtns != null) {
            dividerBetweenBottmBtns.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return self();
    }

    public SuperCommonMdDialog visiableBottomCancelBtn(boolean visiable) {
        if (tvBottomCancel != null) {
            tvBottomCancel.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return visiableDividerBetweenBtns(visiable);
    }

    public SuperCommonMdDialog visiableBottomConfirmBtn(boolean visiable) {
        if (tvBottomConfirm != null) {
            tvBottomConfirm.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return visiableDividerBetweenBtns(visiable);
    }
    public SuperCommonMdDialog withConfirmBtnTextSize(float spSize) {
        if (tvBottomConfirm != null) {
            tvBottomConfirm.setTextSize(spSize);
        }
        if (tvTopConfirm != null) {
            tvTopConfirm.setTextSize(spSize);
        }
        return self();
    }
    public SuperCommonMdDialog withConfirmBtnText(CharSequence confirmBtnText) {
        if (Util.isEmpty(confirmBtnText)) {
            confirmBtnText = getStrFromResId(R.string.confirm_no_gap);
        }
        if (tvBottomConfirm != null) {
            tvBottomConfirm.setText(confirmBtnText);
        }
        if (tvTopConfirm != null) {
            tvTopConfirm.setText(confirmBtnText);
        }
        return self();
    }

    public SuperCommonMdDialog withConfirmBtnBg(@DrawableRes int bg) {
        if (tvBottomConfirm != null) {
            tvBottomConfirm.setBackgroundResource(bg);
        }
        if (tvTopConfirm != null) {
            tvTopConfirm.setBackgroundResource(bg);
        }
        return self();
    }
    public SuperCommonMdDialog withConfirmBtnTextColor(@ColorInt int textColor) {
        if (tvBottomConfirm != null) {
            tvBottomConfirm.setTextColor(textColor);
        }
        if (tvTopConfirm != null) {
            tvTopConfirm.setTextColor(textColor);
        }
        return self();
    }
    public SuperCommonMdDialog withCancelBtnTextSize(float spSize) {
        if (tvBottomCancel != null) {
            tvBottomCancel.setTextSize(spSize);
        }
        if (tvTopCancel != null) {
            tvTopCancel.setTextSize(spSize);
        }
        return self();
    }
    public SuperCommonMdDialog withCancelBtnText(CharSequence cancelBtnText) {
        if (Util.isEmpty(cancelBtnText)) {
            cancelBtnText = getStrFromResId(R.string.cancel_no_gap);
        }
        if (tvBottomCancel != null) {
            tvBottomCancel.setText(cancelBtnText);
        }
        if (tvTopCancel != null) {
            tvTopCancel.setText(cancelBtnText);
        }
        return self();
    }

    public SuperCommonMdDialog withCancelBtnBg(@DrawableRes int bg) {
        if (tvBottomCancel != null) {
            tvBottomCancel.setBackgroundResource(bg);
        }
        if (tvTopCancel != null) {
            tvTopCancel.setBackgroundResource(bg);
        }
        return self();
    }
    public SuperCommonMdDialog withCancelBtnTextColor(@ColorInt int textColor) {
        if (tvBottomCancel != null) {
            tvBottomCancel.setTextColor(textColor);
        }
        if (tvTopCancel != null) {
            tvTopCancel.setTextColor(textColor);
        }
        return self();
    }
    public SuperCommonMdDialog visiableBottomBtnsLayout(boolean visiable) {
        if (llBottomBtnContainer != null) {
            llBottomBtnContainer.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return self();
    }
    public SuperCommonMdDialog visiableDividerAboveBottomBtns(boolean visiable) {
        if (dividerAboveBottomBtns != null) {
            dividerAboveBottomBtns.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return self();
    }
    public SuperCommonMdDialog visiableDividerBelowTitle(boolean visiable) {
        if (dividerBelowTitle != null) {
            dividerBelowTitle.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return self();
    }
    public SuperCommonMdDialog visiableTitle(boolean visiable) {
        if (tvDialogTitle != null) {
            tvDialogTitle.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return self();
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
//        super.setTitle(title);
        withTitle(title);
    }

    public SuperCommonMdDialog withTitleTextSize(int spSize) {
        if (tvDialogTitle != null) {
            tvDialogTitle.setTextSize(spSize);
        }
        return self();
    }
    public SuperCommonMdDialog withTitleTextColor(@ColorInt int textColor) {
        if (tvDialogTitle != null) {
            tvDialogTitle.setTextColor(textColor);
        }
        return self();
    }
    public SuperCommonMdDialog withTitle(CharSequence title) {
        if (tvDialogTitle != null) {
            if (Util.isEmpty(title)) {
                title = getStrFromResId(R.string.hint);
            }
            tvDialogTitle.setText(title);
        }
        return self();
    }
    public SuperCommonMdDialog visiableDividerBelowTopLayout(boolean visiable) {
        if (dividerBelowTopTitle != null) {
            dividerBelowTopTitle.setVisibility(visiable ? View.VISIBLE : View.INVISIBLE);
        }
        return self();
    }
    public SuperCommonMdDialog withTopTitle(CharSequence topTitle) {
        if (tvTopTitle != null) {
            tvTopTitle.setText(topTitle);
        }
        return self();
    }

    public SuperCommonMdDialog withTopTitleTextsize(float spSize) {
        if (tvTopTitle != null) {
            tvTopTitle.setTextSize(spSize);
        }
        return self();
    }

    public SuperCommonMdDialog withTopTitleTextColor(@ColorInt int textColor) {
        if (tvTopTitle != null) {
            tvTopTitle.setTextColor(textColor);
        }
        return self();
    }
    public SuperCommonMdDialog visiableTopTitle(boolean visiable) {
        if (tvTopTitle != null) {
            tvTopTitle.setVisibility(visiable ? View.VISIBLE : View.INVISIBLE);
        }
        return self();
    }
    public SuperCommonMdDialog visiableTopConfirmBtn(boolean visiable) {
        if (tvTopConfirm != null) {
            tvTopConfirm.setVisibility(visiable ? View.VISIBLE : View.INVISIBLE);
        }
        return self();
    }
    public SuperCommonMdDialog visiableTopCancelBtn(boolean visiable) {
        if (tvTopCancel != null) {
            tvTopCancel.setVisibility(visiable ? View.VISIBLE : View.INVISIBLE);
        }
//        if (visiable) {
//            visiableTopLayout(visiable);
//        }
        return self();
    }
//    public SuperCommonMdDialog withTopConfirmBtnText(CharSequence btnText) {
//        if (tvTopConfirm != null) {
//            tvTopConfirm.setText(btnText);
//        }
//        return self();
//    }
//
//    public SuperCommonMdDialog withTopConfirmBtnBg(@DrawableRes int btnBg) {
//        if (tvTopConfirm != null) {
//            tvTopConfirm.setBackgroundResource(btnBg);
//        }
//        return self();
//    }
//    public SuperCommonMdDialog withTopConfirmBtnTextColor(@ColorInt int textColor) {
//        if (tvTopConfirm != null) {
//            tvTopConfirm.setTextColor(textColor);
//        }
//        return self();
//    }
//
//    public SuperCommonMdDialog withTopCancelBtnText(CharSequence cancelBtnText) {
//        if (tvTopCancel != null) {
//            tvTopCancel.setText(cancelBtnText);
//        }
//        return self();
//    }
//
//    public SuperCommonMdDialog withTopCancelBtnBg(@DrawableRes int topCancelBtnBg) {
//        if (tvTopCancel != null) {
//            tvTopCancel.setBackgroundResource(topCancelBtnBg);
//        }
//        return self();
//    }
//    public SuperCommonMdDialog withTopCancelBtnTextColor(@ColorInt int textColor) {
//        if (tvTopCancel != null) {
//            tvTopCancel.setTextColor(textColor);
//        }
//        return self();
//    }
    public SuperCommonMdDialog withContentViewBg(@DrawableRes int contentViewBgResId) {
        if (dialogContentRootView != null) {
            dialogContentRootView.setBackgroundResource(contentViewBgResId);
        }
        return self();
    }

    public SuperCommonMdDialog visiableTopLayout(boolean visiable) {
        if (rlTopWrapTitleView != null) {
            rlTopWrapTitleView.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return self();
    }
    /**
     * 构造方法中完成调用
     *
     * @param dialogView 子类所提供的内容视图
     */
    @Override
    protected void initViews(View dialogView) {
        dialogContentRootView = ViewUtil.findViewInView(dialogView, R.id.common_md_dialog_content_layout);
        rlTopWrapTitleView = ViewUtil.findViewInView(dialogView, R.id.rl_content_header);
        tvTopCancel = ViewUtil.findViewInView(dialogView, R.id.tv_top_cancel);
        tvTopConfirm = ViewUtil.findViewInView(dialogView, R.id.tv_top_confirm);
        tvTopTitle = ViewUtil.findViewInView(dialogView, R.id.tv_dialog_header_title);
        dividerBelowTopTitle = ViewUtil.findViewInView(dialogView, R.id.divider_below_top_title);
        tvDialogTitle = ViewUtil.findViewInView(dialogView, R.id.tv_dialog_title);
        dividerBelowTitle = ViewUtil.findViewInView(dialogView, R.id.divider_below_title);
        dividerAboveBottomBtns = ViewUtil.findViewInView(dialogView, R.id.btns_top_divider);
        tvBottomCancel = ViewUtil.findViewInView(dialogView, R.id.tv_bottom_cancel);
        tvBottomConfirm = ViewUtil.findViewInView(dialogView, R.id.tv_bottom_confirm);
        dividerBetweenBottmBtns = ViewUtil.findViewInView(dialogView, R.id.bottom_btns_divider);
        tvDialogHint = ViewUtil.findViewInView(dialogView, R.id.tv_4_dialog_msg);
        edtInDialog = ViewUtil.findViewInView(dialogView, R.id.edt_as_dialog_msg_or_edit);
        recyclerViewInDialog = ViewUtil.findViewInView(dialogView, R.id.recycleview);
        FrameLayout centerContentView = ViewUtil.findViewInView(dialogView, R.id.content_container);
        initCenterContentView(centerContentView);
        initRecyclerView(recyclerViewInDialog);
        llBottomBtnContainer = ViewUtil.findViewInView(dialogView, R.id.ll_bottom_btns);

        tvTopCancel.setOnClickListener(this);
        tvTopConfirm.setOnClickListener(this);
        tvBottomCancel.setOnClickListener(this);
        tvBottomConfirm.setOnClickListener(this);

    }

    protected void initCenterContentView(FrameLayout centerContentView) {
    }

    /**
     * 初始化Dialog中的RecyclerView，设置Divier/Adapter/LayoutManager等
     * @param recyclerViewInDialog
     */
    protected void initRecyclerView(RecyclerView recyclerViewInDialog) {
    }



    @Override
    public void onClick(View v) {
        super.onClick(v);
        int which = BUTTON_NEGATIVE;
        int viewId = v.getId();
        if (viewId == R.id.tv_top_cancel || viewId == R.id.tv_bottom_cancel) {
            which = BUTTON_NEGATIVE;
        } else if (viewId == R.id.tv_top_confirm || viewId == R.id.tv_bottom_confirm) {
            which = BUTTON_POSITIVE;
        }
        if (dialogClickListener != null) {
            dialogClickListener.onClick(this,which);
        }
        if (which == BUTTON_NEGATIVE) {
            dismiss();
        }
    }

    /**
     * 获取本Dialog的布局视图资源ID
     *
     * @return
     */
    @Override
    protected int getDialogViewResID() {
        return R.layout.super_commondialog_layout;
    }

    public static final int CONTENT_TYPE_DEF_HINT = 1;
    public static final int CONTENT_TYPE_EDIT = CONTENT_TYPE_DEF_HINT + 1;
    public static final int CONTENT_TYPE_LIST = CONTENT_TYPE_EDIT + 1;
    public static final int CONTENT_TYPE_TITLE_AS_HINT = CONTENT_TYPE_LIST + 1;

    public SuperCommonMdDialog simpleContentViewType(int contentViewType) {
        tvDialogHint.setVisibility(View.GONE);
        edtInDialog.setVisibility(View.GONE);
        recyclerViewInDialog.setVisibility(View.GONE);
        switch (contentViewType) {
            case CONTENT_TYPE_DEF_HINT:
                visiableTopLayout(false);
                visiableDividerBelowTopLayout(false);
                visiableDividerBelowTitle(false);
                tvDialogHint.setVisibility(View.VISIBLE);
                break;
            case CONTENT_TYPE_EDIT:
                visiableTopLayout(false);
                visiableDividerBelowTopLayout(false);
                visiableDividerBelowTitle(false);
                edtInDialog.setVisibility(View.VISIBLE);
                break;
            case CONTENT_TYPE_LIST:
                tvDialogTitle.setVisibility(View.GONE);
                visiableDividerBelowTitle(false);
                recyclerViewInDialog.setVisibility(View.VISIBLE);
                //....
                break;
            case CONTENT_TYPE_TITLE_AS_HINT:
                visiableTopLayout(false);
                visiableDividerBelowTopLayout(false);
                visiableDividerBelowTitle(false);
                //...
                break;
        }
        return self();
    }

    @Override
    public TextView getDialogTitleView() {
        return tvDialogTitle;
    }

    @Override
    public TextView getDialogHintView() {
        return tvDialogHint;
    }
}
