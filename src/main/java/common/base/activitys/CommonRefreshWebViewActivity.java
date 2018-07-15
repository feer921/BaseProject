package common.base.activitys;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
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
    protected boolean isNeedGoneDefProgressBar = false;
    /**
     * 获取当前Activity需要填充、展示的内容视图，如果各子类提供，则由基类来填充，如果不提供，各子类也可自行处理
     *
     * @return 当前Activity需要展示的内容视图资源ID
     */
    @Override
    protected final int getProvideContentViewResID() {
        return 0;
    }

    /**
     * 本通用网页加载基类已指定了通用的UI布局，子类不提供
     * @return Activity需要加载的UI布局
     */
    @Override
    protected final View providedContentView() {
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
        forwardInitWebViewAndWebSettings();
        if (webViewClient == null) {
            webViewClient = new LocalWebViewClient();
        }
        webView.setWebViewClient(webViewClient);
        if (webChromClient == null) {
            webChromClient = new LocalWebChromClient();
        }
        webView.setWebChromeClient(webChromClient);
//        forwardInitWebViewAndWebSettings();
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

        // Enable Javascript
        webSettings.setJavaScriptEnabled(true);

        // Enable pinch to zoom without the zoom buttons
        webSettings.setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT > 11) {
            // Hide the zoom controls for HONEYCOMB+
            webSettings.setDisplayZoomControls(false);
        }
//        webSettings.setSupportZoom(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);

        // Allow use of Local Storage
        webSettings.setDomStorageEnabled(true);


        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);


        // AppRTC requires third party cookies to work
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(webView, true);
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
            if (LIFE_CIRCLE_DEBUG) {
                i(null,"--> onPageStarted() url = " + url);
            }
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
            if (LIFE_CIRCLE_DEBUG) {
                i(null,"--> onPageFinished() url = " + url);
            }
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
            e(null, "-->onReceivedSslError() error: " + error);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            e(null, "--> onReceivedError() request = " + request + "  error = " + error);
        }

        /**
         * 注：如果遇到404错误，可能不会回调该方法，则需要在{@link #configReceivedTitle(WebView, String)}中处理,title会包含404
         * @param view
         * @param errorCode
         * @param description
         * @param failingUrl
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            e(null, "--> onReceivedError() errorCode = " + errorCode + "  description = " + description + " failingUrl = " + failingUrl);
            onLoadPageError(view, errorCode, description);
        }

        @SuppressLint("NewApi")
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            String errRespStr = "";
            if (errorResponse != null) {
                if (Util.isCompateApi(21)) {
                    errRespStr += "status code =" + errorResponse.getStatusCode() + " | reason : " + errorResponse.getReasonPhrase();
                }
            }
            e(null, "--> onReceivedHttpError() request url = " + request.getUrl() + "  errRespStr = " + errRespStr);
        }
    }

    protected void onLoadPageError(WebView view, int errorCode, String errorDesc) {
        // TODO: 2017/12/6 后续有时间完善自定义的加载网页失败
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
            pbLoadWeb.setVisibility(isNeedGoneDefProgressBar ? View.GONE : View.INVISIBLE);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 子类可以在这个方法中按自己需要处理
     * @param view
     * @param url
     * @return
     */
    protected boolean shouldOverrideUrlLoading(WebView view, String url) {
        i(null,"-->shouldOverrideUrlLoading() url =  " + url);
        if (!Util.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                String schema = uri.getScheme();
                if (!Util.isEmpty(schema)) {
                    if ("http".equalsIgnoreCase(schema) || "https".equalsIgnoreCase(schema)) {
                        view.loadUrl(url);//这样写就和return false是一样的
                        return true;//自己处理
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
            if (LIFE_CIRCLE_DEBUG) {
                i(null,"--> onProgressChanged() newProgress = " + newProgress);
            }
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

        /**
         * 默认情况下，不能弹 js 框，需要重写 WebChromeClient 的 onJsAlert
         * 重写部分也不需要特殊处理，直接返回 super.onJsAlert(view, url, message, result);
         * @param view
         * @param url
         * @param message
         * @param result
         * @return
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {
            boolean subClassAllow = onWebChromePermissonRequest(request);
            if (!subClassAllow) {
                super.onPermissionRequest(request);
            }
        }
    }

    protected boolean onWebChromePermissonRequest(PermissionRequest permissionRequest) {
        return false;
    }
    protected void onLoadWebProgressChanged(WebView webView, int newProgress) {
        if (!hasCustomLoadWebProgressView) {
            boolean isProgressEnd = 100 == newProgress;
            pbLoadWeb.setProgress(newProgress);
            pbLoadWeb.setVisibility(isProgressEnd ? isNeedGoneDefProgressBar ? View.GONE : View.INVISIBLE : View.VISIBLE);
            if (isProgressEnd) {
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

    /**
     * <html>
        <head>
            <meta http-equiv ="Content-Type" charset="UTF-8"/>
            <title>测试Java与JS的交互</title>
            <script type = "text/javascript">
                var s = "我来自JS方法";
                function javaToJsCallback(param){
                    document.getElementById("textshow").innerHTML=(param);
                    //Js调用android的方法
                    //window.android.JsToJavaInterface(s)
                }
            </script>
        </head>
        <body>
            <h3>JS Method</h3>
            <h3 id="textshow">这里将会显示来自JAVA的信息</h3>
        </body>
     </html>
     * 使用WebView的loadUrl()方式来调用网页内Js的方法(方法内可传入java层的参数(依Js的方法参数))
     * 注：1、该种方式，如果JS的方法有返回值，也会直接显示在WebView上，这样的话就不合适
     * 2、由于loadUrl()不能从Js返回数据，可以让Js回调android的方法回传参数，如：window.android.JsToJavaInterface(s)
     * 3、原生语法为webView.loadUrl("javascript:"+"js方法('参数'));但本基类把"javascript:"已经写了，调用方只传js内的方法和参数
     * @param jsMethodInfo eg:JS内有方法： javaToJsCallback('param')
     */
    protected void loadJsMethod(String jsMethodInfo) {
        if (webView != null) {
            webView.loadUrl("javascript:" + jsMethodInfo);
        }
    }

    @SuppressLint("NewApi")
    protected void evaluateJsMethod(String jsMethodInfo,ValueCallback<String> jsValueCallback) {
        if (Util.isCompateApi(19)) {
            if (webView != null) {
                webView.evaluateJavascript("javascript:"+jsMethodInfo,jsValueCallback);
            }
        }
    }

    protected void evaluateJsMethod(String jsMethodInfo, boolean needReceiveJsValue) {
        StringValueCallback jsValueCallback = null;
        if (needReceiveJsValue) {
            jsValueCallback = new StringValueCallback(jsMethodInfo);
        }
        evaluateJsMethod(jsMethodInfo, jsValueCallback);
    }
    private class StringValueCallback implements ValueCallback<String>{
        private String curJsMethodName = "";
        StringValueCallback(String curJsMehodInfo) {
            int hasJsParamIndex = curJsMehodInfo.indexOf("(");
            if (hasJsParamIndex < 0) {
                curJsMethodName = curJsMehodInfo;
            }
            else{
                //把JS的方法名截取出来(不包含JS方法的参数内容)
                curJsMethodName = curJsMehodInfo.substring(0, hasJsParamIndex);//??
            }
        }
        @Override
        public void onReceiveValue(String value) {
            onJsValueCallback(curJsMethodName, value);
        }
    }

    /**
     * 如果java层调用了JS内的方法，并且JS有返回值的话，重这里回调出来
     * @param theJsMethodName 当前所执行的JS方法名，eg.: javaToJsMethod
     * @param theJsValue JS方法所返回的值信息
     */
    protected void onJsValueCallback(String theJsMethodName, String theJsValue) {

    }
    @Override
    public void finish() {
        webView.safeRelease(true);
        super.finish();
    }
}
