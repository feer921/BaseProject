package common.base.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

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
        CommonLog.sysErr("ScreenUtils:    " + displayMetrics + "  display width :" + defDisplay.getHeight()
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
}
