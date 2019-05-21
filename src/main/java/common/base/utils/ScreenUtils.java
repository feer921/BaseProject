package common.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 工具类
 */
public class ScreenUtils {
    private static Context sContext;
    private static int windowWidth;
    private static int windowHeight;
    private static float density;
    private static int statusBarHeight = 10;
    /**
     * 手机的导航栏(虚拟)
     * 横屏时：为宽
     * 竖屏时：为高
     */
    private static int navigationBarHeight = 10;
    public static void init(Context context) {
        sContext = context.getApplicationContext();
        if (sContext == null) {
            sContext = context;
        }
        if (sContext == null) {
            return;
        }
        WindowManager windowManager = (WindowManager) sContext.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display defDisplay = windowManager.getDefaultDisplay();
        if (defDisplay == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 17) {//但是android4.0之后有了虚拟按键，有虚拟按键的系统调用上面的方法尺寸会减去虚拟按键的尺寸
            defDisplay.getRealMetrics(displayMetrics);
        }
        else {
            boolean useDefResolve = true;
            Class c;
            try {
                c = Class.forName("android.view.Display");
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(defDisplay, displayMetrics);
                useDefResolve = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (useDefResolve) {
                defDisplay.getMetrics(displayMetrics);
            }
        }
        //note here :宽、高的获取是会根据当前屏幕的方向来定义的，如果是竖屏,则宽的值为最短边，如果是横屏，宽为最长边
        //note here : 因而可能会出现初始化时是竖屏的宽，但实际在Activity(横屏)时想获取屏幕宽，却没有获取到所要的最长边的值，所以需要使用本类提供的
        //getScreenCurWidth(boolean needLandscapeScreenWidth)
        windowWidth = displayMetrics.widthPixels;
        windowHeight = displayMetrics.heightPixels;
        density = displayMetrics.density;
        int displayWidth = defDisplay.getWidth();
        int displayHeight = defDisplay.getHeight();
        int swdp = context.getResources().getConfiguration().smallestScreenWidthDp;
        CommonLog.sysErr("ScreenUtils:    " + displayMetrics + "  display width :" + displayWidth
                + " display height :" + displayHeight
                + "  swdp = " + swdp
        );
        boolean isLandscape = windowWidth > windowHeight;
        if (isLandscape) {
            navigationBarHeight = windowWidth - displayWidth;
        }
        else {
            navigationBarHeight = windowHeight - displayHeight;
        }
    }

//    public static int getScreenWidth() {
//       return windowWidth;
//    }
//
//    public static int getScreenHeight() {
//        return windowHeight;
//    }

    public static int getScreenCurWidth(boolean needLandscapeScreenWidth) {
        if (needLandscapeScreenWidth) {//需要横屏的宽,则是找最长边
            return Math.max(windowWidth, windowHeight);
        }
        return Math.min(windowWidth, windowHeight);//则是找最短的边
    }

    public static int getScreenCurHeight(boolean needLandscapeScreenHeight) {
        if (needLandscapeScreenHeight) {//如果需要横屏高，则是找最短的边
            return Math.min(windowHeight, windowWidth);
        }
        return Math.max(windowHeight, windowWidth);//竖屏则是找最长的边
    }
    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        if (statusBarHeight == 10 && sContext != null) {
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
     * 好像任何手机都有值?????
     * @return 导航栏高度
     */
    public static int getNavigationBarHeight(){
//        if (navigationBarHeight == 10 && sContext != null) {
//            int result = 0;
//            Resources res = sContext.getResources();
//            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
//            if (resourceId > 0) {
//                result = res.getDimensionPixelSize(resourceId);
//            }
//            if (result > 0) {
//                navigationBarHeight = result;
//            }
//        }
        return navigationBarHeight;
    }

    /**
     * 好像无效
     * @param activity
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean checkDeviceHasNavigationBar(Context activity) {

        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
         boolean hasMenuKey = ViewConfiguration.get(activity)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            return true;
        }
        return false;
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
        if (sContext == null) {
            return (int) spValue;
        }
        float fontScale = sContext.getResources().getDisplayMetrics().scaledDensity;
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

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPadDevice(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String screenOrientationDesc(int curScreenOr) {
        String oriDesc = curScreenOr + "";
        switch (curScreenOr) {
            case ActivityInfo.SCREEN_ORIENTATION_BEHIND:
                oriDesc = "在后面BEHIND";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                oriDesc = "竖屏";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                oriDesc = "横屏";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_USER:
                oriDesc = "跟随用户";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR:
                oriDesc = "跟随传感器";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED:
                oriDesc = "未指明的";
                break;
        }
        return oriDesc;
    }

    public static String getDevInfos() {
        return " windowWidth:" + windowWidth + " windowHeight:" + windowHeight + " density:" + density;
    }
}
