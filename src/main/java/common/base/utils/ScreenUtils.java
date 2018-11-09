package common.base.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 工具类
 */
public class ScreenUtils {
    private static Context sContext;
    private static int windowWidth;
    private static int windowHeight;
    private static float density;
    private static int statusBarHeight = 10;
    private static int navigationBarHeight = 10;
    public static void init(Context context) {
        sContext = context.getApplicationContext();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display defDisplay = windowManager.getDefaultDisplay();
        defDisplay.getMetrics(displayMetrics);
        windowWidth = displayMetrics.widthPixels;
        windowHeight = displayMetrics.heightPixels;
        density = displayMetrics.density;
        int swdp = context.getResources().getConfiguration().smallestScreenWidthDp;
        CommonLog.sysErr("ScreenUtils:    " + displayMetrics + "  display width :" + defDisplay.getWidth()
                + "  swdp = " + swdp
        );
    }

    public static int getScreenWidth() {
       return windowWidth;
    }

    public static int getScreenHeight() {
        return windowHeight;
    }
    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        if (statusBarHeight == 10) {
            int result = 0;
            Resources res = sContext.getResources();
            int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
            if (result > 0) {
                statusBarHeight = result;
            }
        }
        return statusBarHeight;
    }

    /**
     * 获取系统导航栏(如底部的虚拟导航栏)
     * @return 导航栏高度
     */
    public static int getNavigationBarHeight(){
        if (navigationBarHeight == 10) {
            int result = 0;
            Resources res = sContext.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
            if (result > 0) {
                navigationBarHeight = result;
            }
        }
        return navigationBarHeight;
    }

    public static int dp2px(float dpValue) {
//        final float scale = sContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    public static int px2dp(float pxValue) {
//        final float scale = sContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = sContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    //截屏功能
    //这是webview的，利用了webview的api
    private static Bitmap captureWebView(WebView webView) {
        Picture snapShot = webView.capturePicture();
        if (snapShot == null) {
            return null;
        }
        Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(),
                snapShot.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        snapShot.draw(canvas);
        return bmp;
    }

    /**
     * 截取scrollview的屏幕
     * **/
    public static Bitmap getScrollViewBitmap(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    public static Bitmap captureViewSnapShot(View theView,String savedPath) {
        if (theView != null) {
            theView.setDrawingCacheEnabled(true);
//            theView.buildDrawingCache(true);
            theView.buildDrawingCache();
            Bitmap bitmap = null;
            bitmap = Bitmap.createBitmap(theView.getDrawingCache(false));
            FileOutputStream fos = null;
            if (bitmap != null && savedPath != null) {
                try {
                    File snapShotFile = new File(savedPath);
                    fos = new FileOutputStream(snapShotFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                bitmap.recycle();
                return null;
            }
            return bitmap;
        }
        return null;
    }

    public static Bitmap captureView(View notRootView) {
        View theView = notRootView;
        if (theView != null) {
            theView.setDrawingCacheEnabled(true);
            theView.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(theView.getDrawingCache());
            return bitmap;
        }
        return null;
    }
}
