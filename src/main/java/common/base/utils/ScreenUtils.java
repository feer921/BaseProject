package common.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
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

import androidx.annotation.NonNull;

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

    private static DisplayMetrics displayMetrics;
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
        displayMetrics = new DisplayMetrics();
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
            case ActivityInfo.SCREEN_ORIENTATION_LOCKED:
                oriDesc = "locked";
                break;
        }
        return oriDesc;
    }

    public static String getDevInfos() {
        return " windowWidth:" + windowWidth + " windowHeight:" + windowHeight + " density:" + density;
    }

    public static String getDisplayMetricsDesc() {
        DisplayMetrics curDisplayMetrics = null;
        Resources res = null;
        int swdp = 0;
        if (sContext != null) {
            res = sContext.getResources();
        }
        if (res != null) {
            curDisplayMetrics = res.getDisplayMetrics();
            swdp = res.getConfiguration().smallestScreenWidthDp;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("当前显示信息:").append(curDisplayMetrics)
                .append(" --> 实际显示信息:").append(displayMetrics)
                .append(" 最短宽 dpi:").append(swdp)
        ;
        return sb.toString();
    }
    public static int[] getCurScreenWidthHeight(Context context) {
        int resultWidth = 0;
        int resultHeight = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            if (display != null) {
                int w = display.getWidth();
                int h = display.getHeight();
                CommonLog.sysErr("ScreenUtils: w = " + w + " h = " + h);
                boolean isLandscape = w > h;
                resultWidth = getScreenCurWidth(isLandscape);
                resultHeight = getScreenCurHeight(isLandscape);
            }
        }
        return new int[]{resultWidth,resultHeight};
    }

    /**
     * 最原始的
     */
    private static float theOriginalDensity = 0;
    private static float theOriginalScaleDensity = 0;

    /**
     * 基于UI设计效果图进行屏幕适配
     * 基于今日头条适配方案: https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA
     * 的改进
     * 该适配方案也有缺点:
     * 1:以UI设计效果图的单边为适配基准，单边适配较完美,但由于安卓的设备分辨率千差万别，所以可能存在另一边并不适配;
     * 2:如果项目中使用了第三方的视图、控件，而其作者又指定了尺寸，则会导致使用此方案后，第三方的出现适配问题；
     * 3：如果使用此适配方案，则不能在布局 xml文件中使用 诸如 dp适配方案的 dp资源，因为该dp资源会先适配一次得到适配后的dp值，再被本方案适配一次,
     *    而是应该直接写 50dp,或者较佳实践为 在values目录下新建一个 no_dp.xml,里面写 <dimen name="ndp_30">30dp</dimen>
     *    以便于后续放弃本适配方案切换成其他适配方案
     *
     * @param curActivity 当前的Activity
     * @param curActivityOrientation 开发者指定当前Activity明知的屏幕方向；如果为-1，则本方法内调用API获取所请求的屏蔽方向
     * @param application 当前项目的 Application实例
     * @param uiDesignDpValue 所根据的Ui设计效果图 宽、高 为适配基准的 dp数值
     * @param isSizeBaseWidthOrHeigth {uiDesignDpValue} 变量所对应的尺寸是宽还是高；
     *                                true:依据Ui设计的宽为基准；false:依据UI设计的高为基准。
     */
    public static void adaptUiDesign(@NonNull Activity curActivity,
                                     int curActivityOrientation,@NonNull final Application application,
                                     int uiDesignDpValue, boolean isSizeBaseWidthOrHeigth) {
        if (uiDesignDpValue < 1) {
            return;
        }
        DisplayMetrics displayMetricsOfApp = application.getResources().getDisplayMetrics();
        if (theOriginalDensity == 0) {
            theOriginalDensity = displayMetricsOfApp.density;
            theOriginalScaleDensity = displayMetricsOfApp.scaledDensity;
            //今日头条在这里注册 组件配置更改的监听，但我想可能可以不需要，这么做，可以交给开发者自己在相关Activity的
            //onConfigurationChanged()方法回调时来进行再调用一次本方法来适配 (用户在外部把系统字体等更改后的再适配)
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        theOriginalScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        //to do 这里是否使用本类 init()方法中得出的宽、高？ 有可能这里在取值时 取的是竖屏的宽、高，而实际上Activity却是横屏展示的
        //下面要处理
//        int appMetricsWidth = displayMetricsOfApp.widthPixels;
//        int appMetricsHeight = displayMetricsOfApp.heightPixels;
        //下面是使用 真实的屏幕宽、高
        int appMetricsWidth = windowWidth;
        int appMetricsHeight = windowHeight;

        DisplayMetrics displayMetricsOfActivity = curActivity.getResources().getDisplayMetrics();

        if (-1 == curActivityOrientation) {
            curActivityOrientation = curActivity.getRequestedOrientation();
        }
        int toCalculateScreenSize = 0;
        switch (curActivityOrientation) {
            //当前Activity需要横屏展示,一般横屏的Activity基于UI设计的长宽为基准适配，但本方法还是可选择基于UI设计的高为基准
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE://横屏
                if (isSizeBaseWidthOrHeigth) {//以UI设计图的宽为基准适配
                    //不能简单的以上面 的 [appMetricsWidth] 来当作此时屏幕的宽，有可能该值正好取成竖屏的宽
//                    appMetricsWidth/uiDesignDpValue;
                    //所以要取出最大边为横屏的宽
                    int landscapeWidth = Math.max(appMetricsWidth, appMetricsHeight);
                    toCalculateScreenSize = landscapeWidth;
                }
                else {//以UI设计图的高为基准适配
                    int landscapeHeight = Math.min(appMetricsWidth, appMetricsHeight);
                    toCalculateScreenSize = landscapeHeight;
                }

                break;
            //当前Activity以竖屏展示，一般竖屏展示的Activity基于UI设计的短宽为基准适配,但本方法还是可选择基于UI设计的高为基准
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT://竖屏
                if (isSizeBaseWidthOrHeigth) {//以UI设计图的宽为基准适配
                    //竖屏时的宽，为最短边
                    int portraitWidth = Math.min(appMetricsWidth, appMetricsHeight);
                    toCalculateScreenSize = portraitWidth;
                }
                else {//以UI设计图的高为基准适配
                    int portraitHeight = Math.max(appMetricsWidth, appMetricsHeight);
                    toCalculateScreenSize = portraitHeight;
                }
            default:
                if (isSizeBaseWidthOrHeigth) {//以UI设计图的宽为基准适配
                    toCalculateScreenSize = appMetricsWidth;
                }
                else {//以UI设计图的高为基准适配
                    toCalculateScreenSize = appMetricsHeight;
                }
                break;
        }
        //从dp和px的转换公式 ：px = dp * density
        //比如： screenWidthPx = dpOfWidth * density,而当我们希望在任何设备上都和UI设计的宽(337 dp)保持一致的情况下，
        //即要任何设备的宽算出来 都是 337 dp，则我们只能 改变 系统计算 的 density,而该 density = screenWidthPx/UI设计宽dp
        //并且把计算出的 density 替换掉系统计算的 density

        float targetDensity = toCalculateScreenSize * 1.0f / uiDesignDpValue;
        //dpi (dots per inch)(每英寸的像素点数) = (分辨率宽 *分辨率宽 + 分辨率高 *分辨率高)开根号再/屏幕尺寸(设备对角线英寸)

        int targetDensityDpi = (int) (160 * targetDensity);//屏幕像素密度
        float targetScaledDensity = targetDensity * (theOriginalScaleDensity / theOriginalDensity);
        CommonLog.sysErr("ScreenUtils -> adaptUiDesign()  targetDensity = " + targetDensity + " targetDensityDpi = " + targetDensityDpi +
                "  targetScaledDensity = " + targetScaledDensity +
                "  toCalculateScreenSize = " + toCalculateScreenSize
        );
        displayMetricsOfApp.density = targetDensity;
        displayMetricsOfApp.densityDpi = targetDensityDpi;
        displayMetricsOfApp.scaledDensity = targetScaledDensity;

        displayMetricsOfActivity.density = targetDensity;
        displayMetricsOfActivity.densityDpi = targetDensityDpi;

        displayMetricsOfActivity.scaledDensity = targetScaledDensity;

    }
}
