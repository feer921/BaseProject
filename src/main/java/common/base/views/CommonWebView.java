package common.base.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-17
 * Time: 10:57
 * DESC:
 */
public class CommonWebView extends WebView{
    private static final String TAG = "CommonWebView";
    private WebViewClient outSideWebViewClient;
    private WebChromeClient outSideWebChromeClient;
    /**
     * Constructs a new WebView with a Context object.
     *
     * @param context a Context object used to access application assets
     */
    public CommonWebView(Context context) {
        this(context,null);
    }

    /**
     * Constructs a new WebView with layout parameters.
     *
     * @param context a Context object used to access application assets
     * @param attrs   an AttributeSet passed to our parent
     */
    public CommonWebView(Context context, AttributeSet attrs) {
        super(context,attrs);
        initSelf();
    }

    private void initSelf() {

    }

    /**
     * 安全退出webView
     * @param clearCacheInDisk
     */
    public void safeRelease(boolean clearCacheInDisk){
        clearCache(clearCacheInDisk);
        if(getSettings().supportZoom()){
            Handler delayDestroyHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    destroy();
                }
            };
            long timeOut = ViewConfiguration.getZoomControlsTimeout();
            delayDestroyHandler.sendEmptyMessageDelayed(0, timeOut);
            //            ZoomButtonsController

        }
        else{
            destroy();
        }
    }
}
