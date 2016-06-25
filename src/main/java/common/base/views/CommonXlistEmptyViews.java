package common.base.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ViewSwitcher;
import common.base.R;

/**
 * 包含头部视图、ViewSwitcher{EmptyLayout、XListView} 的公用组件视图
 * 可以通用到有相同UI视图的Activity、FragMent等中
 * <br/>
 * 2015年10月22日-上午11:88:88
 * @author lifei
 */
public class CommonXlistEmptyViews extends FrameLayout implements XListView.IXListViewListener {
    private XListView xListView;
    private EmptyLayout emptyLayout;
    private ViewSwitcher vsLayout;
    private LinearLayout llContainerView;
    public static final byte EMPTY_LAYOUT_INDEX = 0;
    public static final byte CONTENT_LAYOUT_INDEX = 1;
    public CommonXlistEmptyViews(Context context) {
        this(context, null);
    }

    public CommonXlistEmptyViews(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    private void initViews(Context context) {
        //与梦想列表同布局
        inflate(context, R.layout.common_empty_xlistview_layout, this);
        llContainerView = (LinearLayout)findViewById(R.id.ll_contaner_view);
        vsLayout = (ViewSwitcher)findViewById(R.id.vs_view_switcher);
        emptyLayout = (EmptyLayout)findViewById(R.id.empty_layout_4_loading);
        emptyLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                onRefresh();
            }
        });
        xListView = (XListView) findViewById(R.id.activity_searchfriends_data_listview);
        xListView.setXListViewListener(this);
    }
    public void simpleConfigViews(boolean needPullRefresh,boolean needLoadMore){
         xListView.setPullRefreshEnable(needPullRefresh);
         xListView.setPullLoadEnable(needLoadMore);
    }
    public boolean switchLayout(byte targetLayoutIndex){
        if(vsLayout != null){
             if(targetLayoutIndex == vsLayout.getDisplayedChild()){
                 return false;
             }
             //TODO 验证应该没问题
             vsLayout.showNext();
             return true;
        }
        return false;
    }

    public XListView getTheListView(){
        return xListView;
    }
    public void setEmptyLayoutType(int showEmptyType){
        emptyLayout.setErrorType(showEmptyType);
    }
    public void setAdapterForListView(ListAdapter adapter){
        xListView.setAdapter(adapter);
    }
    public void setOnItemClickListenerForListView(OnItemClickListener l) {
        xListView.setOnItemClickListener(l);
    }
    /**
     * 结束刷新或者加载更多并记录时间
     * @param markFinishTime
     */
    public void finishRefreshOrLoad(String markFinishTime){
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(markFinishTime);
    }
    @Override
    public void onRefresh() {
        
    }

    @Override
    public void onLoadMore() {
        
    }

    /**
     * 添加自定义的头部视图
     * @param customHeaderView
     */
    public void addCustomHeaderView(View customHeaderView) {
        if (customHeaderView != null) {
            llContainerView.addView(customHeaderView,0);
        }
    }
}
