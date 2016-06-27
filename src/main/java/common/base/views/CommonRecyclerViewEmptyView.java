package common.base.views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import common.base.R;
import common.base.utils.ViewUtil;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-27
 * Time: 12:02
 * DESC: 通用的UI界面 Android官方的下拉刷新，以及RecyclerView
 */
public class CommonRecyclerViewEmptyView extends FrameLayout{
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    private LinearLayout llContainerView;
    public CommonRecyclerViewEmptyView(Context context) {
        this(context,null);
    }

    public CommonRecyclerViewEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.common_recycler_pull_refresh_layout, this);
        llContainerView = ViewUtil.findViewInContainer(this, R.id.ll_contaner_view);
        swipeRefreshLayout = ViewUtil.findViewInContainer(this, R.id.swipe_refreshlayout);
        recyclerView = ViewUtil.findViewInContainer(this,R.id.recycleview);
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
}
