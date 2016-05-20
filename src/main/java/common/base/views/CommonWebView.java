package common.base.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-17
 * Time: 10:57
 * DESC:
 */
public class CommonWebView extends WebView{
    /**
     * Constructs a new WebView with layout parameters and a default style.
     *
     * @param context      a Context object used to access application assets
     * @param attrs        an AttributeSet passed to our parent
     * @param defStyleAttr an attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     */
    public CommonWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
