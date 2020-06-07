package common.base.dialogs;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import common.base.R;
import common.base.utils.Util;

/**
 * ******************(^_^)***********************<br>
 * User: 11776610771@qq.com<br>
 * Date: 2017/9/19<br>
 * Time: 10:53<br>
 * <P>DESC:
 * 带有RecyclerView列表功能、上部Title、底部“确定”、“取消”，按钮Dialog
 * </p>
 * ******************(^_^)***********************
 */

public class BaseRecyclerViewDialog<I extends BaseRecyclerViewDialog<I>> extends BaseDialog<I> {
    protected TextView tvTitle;
    protected TextView tvTopConfirm;
    protected TextView tvTopCancel;

    protected TextView tvBottomCancel;
    protected TextView tvBottomConfirm;

    protected RecyclerView recyclerView;
    /**
     * 在上部的标题下的分隔线View
     */
    protected View dividerBelowOfTopTitle;
    /**
     * 在底部按钮栏上面的分隔View
     */
    protected View dividerTopOfBottomBtns;
    /**
     * 在底部两按钮之间的分隔View
     */
    protected View dividerBetweenBottomBtns;

    protected FrameLayout contentContainer;



    public BaseRecyclerViewDialog(Context context) {
        this(context,R.style.common_dialog);
    }

    public BaseRecyclerViewDialog(Context context, int theme) {
        super(context, theme);
        if (theme > 0) {
            //有样式 处理样式
        }

    }

    /**
     * 构造方法中完成调用
     * @param dialogView 子类所提供的内容视图
     */
    @Override
    protected void initViews(View dialogView) {
        tvTitle = viewInDialogView(R.id.tv_dialog_title);
        tvTopConfirm = viewInDialogView(R.id.tv_top_confirm);
        tvTopCancel = viewInDialogView(R.id.tv_top_cancel);
        tvBottomCancel = viewInDialogView(R.id.tv_bottom_cancel);
        tvBottomConfirm = viewInDialogView(R.id.tv_bottom_confirm);
        recyclerView = viewInDialogView(R.id.recycleview);
        dividerTopOfBottomBtns = viewInDialogView(R.id.btns_top_divider);
        initRecyclerView(recyclerView);
        dividerBelowOfTopTitle = viewInDialogView(R.id.view_gray_divider);
        dividerBetweenBottomBtns = viewInDialogView(R.id.bottom_btns_divider);
        contentContainer = viewInDialogView(R.id.content_container);
        addViewsInContentContainer(contentContainer);
        tvTopCancel.setOnClickListener(this);
        tvTopConfirm.setOnClickListener(this);
        tvBottomCancel.setOnClickListener(this);
        tvBottomConfirm.setOnClickListener(this);

    }

    /**
     * 子类可以重写该方法来设置RecyclerView
     * @param recyclerView
     */
    protected void initRecyclerView(RecyclerView recyclerView) {


    }

    /**
     * 获取本Dialog的布局视图资源ID
     * @return
     */
    @Override
    protected int getDialogViewResID() {
        return R.layout.common_bottom_dialog;
    }
    /**
     * 这里重写的是Dialog.java中的方法，原方法为给window设置title
     * @param title
     */
    @Override
    public void setTitle(CharSequence title) {
        if (tvTitle != null) {
            if (Util.isEmpty(title)) {
                tvTitle.setText(R.string.hint);
            }
            else{
                tvTitle.setText(title);
            }
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getStrFromResId(titleId));
    }

    public I withTitle(CharSequence title) {
        setTitle(title);
        return self();
    }

    public I dividerBetweenBtnsVisiable(boolean visiable) {
        if (dividerBetweenBottomBtns != null) {
            dividerBetweenBottomBtns.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return self();
    }
    /**
     * 给所有的TextView 的按钮设置一致的背景资源
     * @param backgroundRes
     * @return
     */
    public I withTvBtnBackgroundRes(@DrawableRes int backgroundRes) {
        if (tvTopCancel != null) {
            tvTopCancel.setBackgroundResource(backgroundRes);
        }
        if (tvTopConfirm != null) {
            tvTopConfirm.setBackgroundResource(backgroundRes);
        }
        if (tvBottomCancel != null) {
            tvBottomCancel.setBackgroundResource(backgroundRes);
        }
        if (tvBottomConfirm != null) {
            tvBottomConfirm.setBackgroundResource(backgroundRes);
        }
        return self();
    }

    protected void addViewsInContentContainer(FrameLayout contentContainer) {

    }
    /**
     * 不要上面的标题栏以及标题栏下面的divider
     * @return
     */
    public I withNoTitle() {
        viewInDialogView(R.id.rl_content_header).setVisibility(View.GONE);
        dividerBelowOfTopTitle.setVisibility(View.GONE);
        return self();
    }

    public I withTopTitleVisiable(boolean visiable) {
        if (!visiable) {
          return withNoTitle();
        }
        viewInDialogView(R.id.rl_content_header).setVisibility(View.VISIBLE);
        dividerBelowOfTopTitle.setVisibility(View.VISIBLE);
        return self();
    }

    public I withRecyclerViewAdapter(BaseQuickAdapter adapter) {
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
        return self();
    }



    public I withConfimBtnVisiable(boolean visiable) {
        if (tvBottomConfirm != null) {
            tvBottomConfirm.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return self();
    }

    public I withTopConfimBtnVisiable(boolean visiable) {
        if (tvTopConfirm != null) {
            tvTopConfirm.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return self();
    }
    public I withTopCancelBtnVisiable(boolean visiable) {
        if (tvTopCancel != null) {
            tvTopCancel.setVisibility(visiable ? View.VISIBLE : View.GONE);
        }
        return self();
    }
    public I withBottomBtnsVisiable(boolean visiable) {
        viewInDialogView(R.id.ll_bottom_btns).setVisibility(visiable ? View.VISIBLE : View.GONE);
        dividerTopOfBottomBtns.setVisibility(visiable ? View.VISIBLE : View.GONE);
        return self();
    }

    public I withBottomBtnsTopDividerVisiable(boolean visiable) {
        dividerTopOfBottomBtns.setVisibility(visiable ? View.VISIBLE : View.GONE);
        return self();
    }
    public I withCancelText(CharSequence cancelText) {
        if (Util.isEmpty(cancelText)) {
            cancelText = getStrFromResId(R.string.cancel);
        }
        if (tvBottomCancel != null) {
            tvBottomCancel.setText(cancelText);
        }
        if (tvTopCancel != null) {
            tvTopCancel.setText(cancelText);
        }
        return self();
    }
    @Override
    public I setCancleBtnName(String cancleBtnName) {
        return withCancelText(cancleBtnName);
    }

    public I withConfirmText(CharSequence confirmText) {
        if (Util.isEmpty(confirmText)) {
            confirmText = getStrFromResId(R.string.confirm);
        }
        if (tvBottomConfirm != null) {
            tvBottomConfirm.setText(confirmText);
        }
        if (tvTopConfirm != null) {
            tvTopConfirm.setText(confirmText);
        }
        return self();
    }
    @Override
    public I setCommitBtnName(String commitBtnName) {
        return withConfirmText(commitBtnName);
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
        if (which == BUTTON_NEGATIVE) {
            dismiss();
        }
        if (dialogClickListener != null) {
            dialogClickListener.onClick(this,which);
        }
    }
    public TextView getTvBottomCancel() {
        return tvBottomCancel;
    }

    @Override
    public View getDialogTitleView() {
        return tvTitle;
    }

    public void listScrollToPosiont(int targetPos) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(targetPos);
        }
    }

    //-------------------- new added -----------------2018-03-23
    public I visibleBottomCancelBtn(boolean isVisible) {
        tvBottomCancel.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if (!isVisible) {
            dividerBetweenBtnsVisiable(isVisible);
        }
        return self();
    }

    public I visibleBottomConfirmBtn(boolean isVisible) {
        tvBottomConfirm.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        if (!isVisible) {
            dividerBetweenBtnsVisiable(isVisible);
        }
        return self();
    }
    public I withTopCancelText(CharSequence text) {
        if (tvTopCancel != null) {
            tvTopCancel.setText(text);
        }
        return self();
    }
    public TextView getTvTitle() {
        return tvTitle;
    }

    public TextView getTvTopConfirm() {
        return tvTopConfirm;
    }

    public TextView getTvTopCancel() {
        return tvTopCancel;
    }

    public TextView getTvBottomConfirm() {
        return tvBottomConfirm;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public View getDividerBelowOfTopTitle() {
        return dividerBelowOfTopTitle;
    }

    public View getDividerTopOfBottomBtns() {
        return dividerTopOfBottomBtns;
    }

    public View getDividerBetweenBottomBtns() {
        return dividerBetweenBottomBtns;
    }
}
