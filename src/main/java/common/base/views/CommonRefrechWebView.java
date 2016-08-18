package common.base.views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import common.base.R;
import common.base.utils.ViewUtil;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-08-18
 * Time: 14:20
 * DESC: 组合下拉刷新以及加载网页的WebView
 */
public class CommonRefrechWebView extends FrameLayout{
    private LinearLayout llContainerLayout;
    private ProgressBar pbLoadWeb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommonWebView webView;
    private int customHeaderViewIndex = 0;
    public CommonRefrechWebView(Context context) {
        this(context,null);
    }

    public CommonRefrechWebView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CommonRefrechWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.common_refresh_webview_layout, this);
        llContainerLayout = (LinearLayout) getChildAt(0);
        pbLoadWeb = ViewUtil.findViewInContainer(this, R.id.pb_load_web);
        swipeRefreshLayout = ViewUtil.findViewInContainer(this, R.id.swipe_refreshlayout);
        webView = ViewUtil.findViewInContainer(this, R.id.common_webview);
    }

    public void addCustomHeaderLayout(View customHeaderView) {
        if (customHeaderView != null && customHeaderView.getParent() == null) {
            if (llContainerLayout != null) {
                llContainerLayout.addView(customHeaderView, customHeaderViewIndex);
                customHeaderViewIndex ++;
            }
        }
    }

    public void addCustomLoadWebProgressView(View customProgressView) {
        if (customProgressView != null && customProgressView.getParent() == null) {
            if (llContainerLayout != null) {
                llContainerLayout.addView(customProgressView, customHeaderViewIndex);
                if (pbLoadWeb != null) {
                    pbLoadWeb.setVisibility(GONE);
                }
            }
        }
    }

    public CommonWebView getWebView() {
        return webView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public ProgressBar getPbLoadWeb() {
        return pbLoadWeb;
    }
}
