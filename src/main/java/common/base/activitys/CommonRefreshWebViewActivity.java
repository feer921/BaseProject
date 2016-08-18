package common.base.activitys;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import common.base.R;
import common.base.utils.Util;
import common.base.views.CommonWebView;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-08-17
 * Time: 10:11
 * DESC: 通用的可下拉刷新的，加载网页界面的Activity
 */
public class CommonRefreshWebViewActivity<T> extends BaseNetCallActivity<T> implements SwipeRefreshLayout.OnRefreshListener{
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected CommonWebView webView;
    protected ProgressBar pbLoadWeb;
    protected boolean hasCustomHeaderView,hasCustomLoadWebProgressView;
    protected String baseWebUrl;
    /**
     * 获取当前Activity需要填充、展示的内容视图，如果各子类提供，则由基类来填充，如果不提供，各子类也可自行处理
     *
     * @return 当前Activity需要展示的内容视图资源ID
     */
    @Override
    protected final int getProvideContentViewResID() {
        return 0;
    }

    @Override
    protected View providedContentView() {
        LinearLayout contentView = (LinearLayout) getLayoutInflater().inflate(R.layout.common_refresh_webview_layout, null);
        View customHeaderView = getProvidedCustomHeaderView();
        if (customHeaderView != null) {
            hasCustomHeaderView = true;
            contentView.addView(customHeaderView, 0);
        }
        pbLoadWeb = findAviewInContainer(contentView, R.id.pb_load_web);
        View customProgressView = getProvidedCustomLoadWebProgressView();
        if (customProgressView != null) {
            hasCustomLoadWebProgressView = true;
            contentView.addView(customProgressView, hasCustomHeaderView ? 1 : 0);
            pbLoadWeb.setVisibility(View.GONE);
        }
        swipeRefreshLayout = findAviewInContainer(contentView, R.id.swipe_refreshlayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        webView = findAviewInContainer(contentView, R.id.common_webview);
        return contentView;
    }

    protected View getProvidedCustomHeaderView() {
        //本框架可以提供一个默认的头部
        return null;
    }

    protected View getProvidedCustomLoadWebProgressView() {
        return null;
    }
    /**
     * 如果子类在getProvideContentViewResID()方法提供了视图资源，那么子类的初始化视图可在此方法中完成
     */
    @Override
    protected final void initViews() {
        if (hasCustomHeaderView) {
            initHeaderViews();
        }
        if (hasCustomLoadWebProgressView) {
            initLoadWebProgressViews();
        }
        initSwipeRefreshView();
        initWebView();
    }

    @Override
    protected void initData() {
        super.initData();
        if (!Util.isEmpty(baseWebUrl)) {
            webView.loadUrl(baseWebUrl);
        }
    }

    protected void initHeaderViews() {
    }

    protected void initLoadWebProgressViews() {

    }

    /**
     * 初始化下拉刷新的控件SwipeRefreshLayout,eg.:配置下拉刷新时的Loading样式等等
     */
    protected void initSwipeRefreshView() {
    }
    private void initWebView() {
        if (webViewClient == null) {
            webViewClient = new LocalWebViewClient();
        }
        webView.setWebViewClient(webViewClient);
        if (webChromClient == null) {
            webChromClient = new LocalWebChromClient();
        }
        webView.setWebChromeClient(webChromClient);
        forwardInitWebViewAndWebSettings();
    }

    /**
     * 子类如果对WebSetting 有自己的设置，则重写此方法
     */
    @SuppressLint("NewApi")
    protected void forwardInitWebViewAndWebSettings() {
        if (!Util.isCompateApi(16) && Util.isCompateApi(11)) {//android 4.1以下(不含4.1)
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        WebSettings webSettings = webView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        //       webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

    }

    @Override
    public void onRefresh() {
        webView.reload();
//        if (!hasCustomLoadWebProgressView) {
//            pbLoadWeb.setVisibility(View.VISIBLE);
//            pbLoadWeb.setProgress(0);
//        }
    }
    protected LocalWebViewClient webViewClient;
    private class LocalWebViewClient extends WebViewClient {
        /**
         * Give the host application a chance to take over the control when a new
         * url is about to be loaded in the current WebView. If WebViewClient is not
         * provided, by default WebView will ask Activity Manager to choose the
         * proper handler for the url. If WebViewClient is provided, return true
         * means the host application handles the url, while return false means the
         * current WebView handles the url.
         * This method is not called for requests using the POST "method".
         *
         * @param view The WebView that is initiating the callback.
         * @param url  The url to be loaded.
         * @return True if the host application wants to leave the current WebView
         * and handle the url itself, otherwise return false.
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return CommonRefreshWebViewActivity.this.shouldOverrideUrlLoading(view, url);
        }

        /**
         * Notify the host application that a page has started loading. This method
         * is called once for each main frame load so a page with iframes or
         * framesets will call onPageStarted one time for the main frame. This also
         * means that onPageStarted will not be called when the contents of an
         * embedded frame changes, i.e. clicking a link whose target is an iframe,
         * it will also not be called for fragment navigations (navigations to
         * #fragment_id).
         *
         * @param view    The WebView that is initiating the callback.
         * @param url     The url to be loaded.
         * @param favicon The favicon for this page if it already exists in the
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            i(null,"--> onPageStarted() url = " + url);
            super.onPageStarted(view, url, favicon);
            onLoadPageStarted(view, url, favicon);
        }

        /**
         * Notify the host application that a page has finished loading. This method
         * is called only for main frame. When onPageFinished() is called, the
         * rendering picture may not be updated yet. To get the notification for the
         * new Picture, use {@link WebView.PictureListener#onNewPicture}.
         *
         * @param view The WebView that is initiating the callback.
         * @param url  The url of the page.
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            i(null,"--> onPageFinished() url = " + url);
            super.onPageFinished(view, url);
            onLoadPageFinished(view,url);
        }

        /**
         * webView默认是不处理https请求的，页面显示空白，需要进行如下设置
         * @param view
         * @param handler
         * @param error
         */
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

    protected void onLoadPageStarted(WebView webView, String url, Bitmap favicon) {
        pbLoadWeb.setProgress(0);
        if (!hasCustomLoadWebProgressView) {
            pbLoadWeb.setVisibility(View.VISIBLE);
        }
    }
    protected void onLoadPageFinished(WebView webView, String url) {
        pbLoadWeb.setProgress(100);
        if (!hasCustomLoadWebProgressView) {
            pbLoadWeb.setVisibility(View.INVISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
    }
    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        i(null,"-->shouldOverrideUrlLoading() url =  " + url);
        if (!Util.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                String schema = uri.getScheme();
                if (!Util.isEmpty(schema)) {
                    if ("http".equalsIgnoreCase(schema) || "https".equalsIgnoreCase(schema)) {
                        view.loadUrl(url);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected LocalWebChromClient webChromClient;
    private class LocalWebChromClient extends WebChromeClient{
        /**
         * Tell the host application the current progress of loading a page.
         *
         * @param view        The WebView that initiated the callback.
         * @param newProgress Current page loading progress, represented by
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            i(null,"--> onProgressChanged() newProgress = " + newProgress);
            super.onProgressChanged(view, newProgress);
            onLoadWebProgressChanged(view,newProgress);
        }

        /**
         * Notify the host application of a change in the document title.
         *
         * @param view  The WebView that initiated the callback.
         * @param title A String containing the new title of the document.
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            i(null,"--> onReceivedTitle() title = " + title);
            super.onReceivedTitle(view, title);
            configReceivedTitle(view, title);
        }
    }

    protected void onLoadWebProgressChanged(WebView webView, int newProgress) {
        if (!hasCustomLoadWebProgressView) {
            boolean isProgressEnd = 100 == newProgress;
            pbLoadWeb.setVisibility(isProgressEnd ? View.INVISIBLE : View.VISIBLE);
            pbLoadWeb.setProgress(newProgress);
            if (100 == newProgress) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
    protected void configReceivedTitle(WebView webView, String title) {

    }

    /**
     * 记录一次点击回退按钮的时间
     */
    private long clickBackTime;
    protected long canBackGapMillTime = 1000;
    @Override
    public void onBackPressed() {
        boolean canCloseWindow = true;
        if (needGoBackBrowseWeb() && webView.canGoBack()) {
            canCloseWindow = false;
            if ((System.currentTimeMillis() - clickBackTime) > canBackGapMillTime) {
                webView.goBack();
            }
            clickBackTime = System.currentTimeMillis();
        }
        if (canCloseWindow) {
            super.onBackPressed();
            switchActivity(true);
        }
    }

    /**
     * 是否需要回退来浏览网页
     * eg.: 比如点击头部视图的返回键或者“返回”按钮，此时是否需要回退浏览历史网页
     * @return
     */
    protected boolean needGoBackBrowseWeb() {
        return true;
    }

    /**
     * @param object
     * @param name
     */
    @SuppressLint("JavascriptInterface")
    protected void addJsInterface(Object object, String name) {
        webView.addJavascriptInterface(object, name);
    }

    protected void runJsMethod(String jsMethodInfo) {
        webView.loadUrl("javascript:" + jsMethodInfo);
    }
    @Override
    public void finish() {
        super.finish();
        webView.safeRelease(true);
    }
}
