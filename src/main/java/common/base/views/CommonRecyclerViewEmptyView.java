package common.base.views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import common.base.R;
import common.base.utils.ViewUtil;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-27
 * Time: 12:02
 * DESC: 通用的UI界面 Android官方的下拉刷新，以及RecyclerView,以及空布局视图
 */
public class CommonRecyclerViewEmptyView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    private LinearLayout llContainerView;
    /**
     * 当前其子视图序号为，0：RecyclerView；1、空布局
     * 默认是显示第一个[0序号]子视图
     */
    private ViewSwitcher vs;
    private EmptyLayout emptyLayout;
    private SwipeRefreshLayout.OnRefreshListener outsideRefreshListener;
    /**
     * 是否需要内部的空布局；默认为不需要
     * 可调用{@link #needInnerEmptyView()} 去不显示本内部的空布局
     */
    private boolean needInnerEmptyView = false;
    public CommonRecyclerViewEmptyView(Context context) {
        this(context,null);
    }

    public CommonRecyclerViewEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.common_empty_recycler_pull_refresh_layout, this);
        llContainerView = ViewUtil.findViewInContainer(this, R.id.ll_contaner_view);
        swipeRefreshLayout = ViewUtil.findViewInContainer(this, R.id.swipe_refreshlayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = ViewUtil.findViewInContainer(this,R.id.recycleview);
        emptyLayout = ViewUtil.findViewInContainer(this, R.id.empty_layout_4_loading);
        emptyLayout.setInAFullMode(true);
        emptyLayout.setErrorImag(R.drawable.pagefailed_bg);
        //让空布局默认显示无数据状态
        emptyLayout.setErrorType(EmptyLayout.NODATA);
        vs = ViewUtil.findViewInContainer(this, R.id.vs_view_switcher);
    }

    public void setOutsideRefreshListener(SwipeRefreshLayout.OnRefreshListener refreshListener) {
        this.outsideRefreshListener = refreshListener;
    }
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public void addCustomHeaderView(View customHeaderView) {
        if (customHeaderView != null && customHeaderView.getParent() == null) {
            llContainerView.addView(customHeaderView,0);
        }
    }

    @Override
    public void onRefresh() {
        //显示loading
        if (needInnerEmptyView) {
            if (vs.getCurrentView() == emptyLayout) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }
        }
        if (outsideRefreshListener != null) {
            outsideRefreshListener.onRefresh();
        }
    }

    /**
     * 不需要本类内部的空布局视图
     */
    public void needInnerEmptyView() {
        this.needInnerEmptyView = true;
        switchTargetView(1);
    }

    public void needCanClickEmptyView2ReLoad() {
        emptyLayout.setOnLayoutClickListener(this);
    }
    /**
     * 在本视图内中切换空布局{@link #emptyLayout} 与{@link #recyclerView}谁显示
     * eg.: 如果当前列表没有数据则让空布局显示，否则让列表控件RecyclerView显示
     * @param targetChildViewIndex 目标视图在父控件{@link #vs}中的序号，本类中1为空布局，0为RecyclerView布局
     * @return false:传入的视图序号不合规或者超出范围，或者当前已经是显示了要切换的目标视图; true:切换成了目标视图
     */
    public boolean switchTargetView(int targetChildViewIndex) {
        if (targetChildViewIndex < 0 || targetChildViewIndex >= vs.getChildCount()) {
            return false;
        }
        int curDisplayChildViewIndex = vs.getDisplayedChild();
        if (targetChildViewIndex == curDisplayChildViewIndex) {
            return false;
        }
        vs.showNext();
        return true;
    }

    /**
     * 切换到列表数据视图
     */
    public void showListDataView() {
        switchTargetView(0);
    }

    /**
     * 切换到空布局视图
     */
    public void showEmptyView() {
        switchTargetView(1);
    }

    public void autoLoadingData() {
        post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    public void hintNoData(String noDataCase) {
        emptyLayout.setNoDataContent(noDataCase);
        emptyLayout.setErrorType(EmptyLayout.NODATA);
        switchTargetView(1);
    }

    public EmptyLayout getEmptyLayout() {
        return emptyLayout;
    }

    /**
     * 设置没有数据时提示的图标icon的资源
     * @param noDataHintIconResId
     */
    public void setNoDataHintIconRes(int noDataHintIconResId) {
        emptyLayout.setErrorImag(noDataHintIconResId);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.img_error_layout) {
            autoLoadingData();
        }
    }
}
