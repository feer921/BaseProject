package common.base.fragments;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import common.base.views.CommonRefreshRecyclerView;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-27
 * Time: 14:26
 * DESC: 指定了通用界面视图布局(即CommonRefreshRecyclerView)的列表碎片界面，可下拉刷新、上拉加载更多，空布局(自定义),头部布局(自定义)
 */
public abstract class CommonRefreshRecyclerViewListFragment<T,TListData,VH extends BaseViewHolder> extends BaseListFragment<T,TListData,VH> implements SwipeRefreshLayout.OnRefreshListener{
    protected CommonRefreshRecyclerView commonRecyclerViewEmptyView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    private View customHeaderView;

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
            commonRecyclerViewEmptyView = new CommonRefreshRecyclerView(context);
        }
        customHeaderView = getCustomHeaderView();
        swipeRefreshLayout = commonRecyclerViewEmptyView.getSwipeRefreshLayout();
        recyclerView = commonRecyclerViewEmptyView.getRecyclerView();
        //added by fee 2016-12-16 RecyclerView的item点击事件更改交由RecyclerView来设置
        recyclerView.addOnItemTouchListener(obtainTheRecyclerItemClickListen());
        commonRecyclerViewEmptyView.addCustomHeaderView(customHeaderView);
        swipeRefreshLayout.setOnRefreshListener(this);
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
        initSwipeRefreshLayout(swipeRefreshLayout);
        recyclerView.setAdapter(adapter4RecyclerView);
        initRecyclerView(recyclerView);
        initRecyclerAdapter(adapter4RecyclerView);
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
}
