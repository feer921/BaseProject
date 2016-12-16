package common.base.fragments;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import common.base.utils.NetHelper;
import common.base.utils.Util;
import common.base.views.CommonRecyclerViewEmptyView;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-27
 * Time: 14:26
 * DESC: 指定了通用界面视图布局(即CommonRecyclerViewEmptyView[带有空布局])的列表碎片界面，可下拉刷新、上拉加载更多，空布局(自定义),头部布局(自定义)
 */
public abstract class CommonRefreshRecyclerEmptyViewListFragment<T,TListData,VH extends BaseViewHolder> extends BaseListFragment<T,TListData,VH> implements SwipeRefreshLayout.OnRefreshListener{
    protected CommonRecyclerViewEmptyView commonRecyclerViewEmptyView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    private View customHeaderView;

    /***
     * 子类是否需要使用通用控件CommonRecyclerViewEmptyView 内部的空布局视图
     * 注：默认为需要使用
     */
    protected boolean needUseInnerEmptyView = true;

    /**
     * final此方法，因为本基类已经提供了通用的界面布局CommonRecyclerViewEmptyView
     * 提供当前碎片的内容视图布局的资源ID
     * @return
     */
    @Override
    protected final int providedFragmentViewResId() {
        return 0;
    }

    /**
     * final此方法，因为本基类已经提供了通用的界面布局CommonRecyclerViewEmptyView
     * 提供当前碎片的内容视图View
     * @return
     */
    @Override
    protected final View providedFragmentView() {
        if (commonRecyclerViewEmptyView == null) {
            commonRecyclerViewEmptyView = new CommonRecyclerViewEmptyView(context);
        }
        customHeaderView = getCustomHeaderView();
        swipeRefreshLayout = commonRecyclerViewEmptyView.getSwipeRefreshLayout();
        recyclerView = commonRecyclerViewEmptyView.getRecyclerView();
        //added by fee 2016-12-16,RecyclerView的item点击事件更改为交由RecyclerView来设置
        recyclerView.addOnItemTouchListener(obtainTheRecyclerItemClickListen());
        commonRecyclerViewEmptyView.addCustomHeaderView(customHeaderView);
        if (needUseInnerEmptyView) {
            commonRecyclerViewEmptyView.needInnerEmptyView();
        }
        commonRecyclerViewEmptyView.setOutsideRefreshListener(this);
        return commonRecyclerViewEmptyView;
    }
    /**
     * 对所加载的视图进行初始化工作
     *
     * @param contentView
     */
    @Override
    protected final void initViews(View contentView) {
        if (customHeaderView != null) {
            initCustomHeaderView(customHeaderView);
        }
        initCommonRecyclerEmptyView();
        initSwipeRefreshLayout(swipeRefreshLayout);
        recyclerView.setAdapter(adapter4RecyclerView);
        initRecyclerView(recyclerView);
        initRecyclerAdapter(adapter4RecyclerView);
    }

    /**
     * 初始化CommonRecyclerViewEmptyView
     * 一般子类可以不需要
     */
    protected void initCommonRecyclerEmptyView() {
    }

    /**
     * 获取子类提供的自定义的头部布局视图
     * @return
     */
    protected abstract View getCustomHeaderView();

    /**
     * 初始化BaseQuickAdapter 比如item动画,自定义的空布局等
     * @param adapter4RecyclerView
     */
    protected abstract void initRecyclerAdapter(BaseQuickAdapter<TListData,VH> adapter4RecyclerView);

    /**
     * 初始化RecyclerView
     * @param recyclerView
     */
    protected abstract void initRecyclerView(RecyclerView recyclerView);

    /**
     * 初始化SwipeRefreshLayout
     * 比如设置Loading的颜色等
     * @param swipeRefreshLayout
     */
    protected abstract void initSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout);

    /**
     * 初始化自定义的头部视图布局
     * @param customHeaderView
     */
    protected abstract void initCustomHeaderView(View customHeaderView);
    /**
     * 各子类不实现本方法的话，即按本通用界面的通用处理
     * @param hasListDataResult 一次请求完成后加上上一次的数据最终是否有列表数据,true表示有数据，false表示没有
     * @param errorInfoIfRequestFail 如果是请求失败时的错误信息
     */
    @Override
    public void listDataRequestFinish(boolean hasListDataResult, String errorInfoIfRequestFail) {
        swipeRefreshLayout.setRefreshing(false);
        if (hasListDataResult) {
            commonRecyclerViewEmptyView.showListDataView();
        }
        else{
            String hintNoDataDesc = providedNoDataDesc(true);
            if (!Util.isEmpty(errorInfoIfRequestFail)) {//网络连接异常
                if (!NetHelper.isNetworkConnected(context)) {
                    hintNoDataDesc = providedNoDataDesc(false);
                }
            }
            commonRecyclerViewEmptyView.hintNoData(hintNoDataDesc);
        }
    }
    /**
     * 从网络加载数据但结果没有任何数据时的提示性描述信息
     * 各子类可重新实现此方法以提供不同的/针对性的描述信息
     * @param isNetValid 当前系统网络是否有效. true:网络有效；false：网络无效
     * @return
     */
    protected String providedNoDataDesc(boolean isNetValid) {
        if (isNetValid) {
            return "未获取到相关数据,请稍候下拉刷新以重新加载.";
        }
        return "当前网络连接不可用,请检查网络设置,并重新加载.";
    }
}
