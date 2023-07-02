package common.base.utils;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.android.material.tabs.TabLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import common.base.R;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-14
 * Time: 15:19
 * DESC:
 */
public class ViewUtil{
    /**
     * 使用{@link #findViewInView(View, int)}即可
     * @deprecated
     * @param containerView
     * @param toFindViewResId
     * @param <T>
     * @return
     */
    public static <T> T findViewInContainer(ViewGroup containerView, int toFindViewResId) {
        if (toFindViewResId < 1 || containerView == null) {
            return null;
        }
        return (T) containerView.findViewById(toFindViewResId);
    }

    public static <T> T findAViewById(Activity curActivity, int toFindViewResId) {
        if (curActivity == null || toFindViewResId < 1) {
            return null;
        }
        return (T) curActivity.findViewById(toFindViewResId);
    }
    public static <T> T findViewInView(View containerView, int toFindViewResId) {
        if (toFindViewResId < 1 || containerView == null) {
            return null;
        }
        return (T) containerView.findViewById(toFindViewResId);
    }
    // 在TypedValue类中
//    /**
//     * px、dp、sp、pt、in、mm单位转换
//     * @param unit  转换类型
//     * @param value 转换值(float)
//     * @param metrics 当前设备显示密度
//     * @return 转换单位后的值
//     */
//    public static float applyDimension(int unit, float value, DisplayMetrics metrics) {
//        switch (unit) {
//            case COMPLEX_UNIT_PX: // 转换为px(像素)值
//                return value;
//            case COMPLEX_UNIT_DIP: // 转换为dp(密度)值
//                return value * metrics.density;
//            case COMPLEX_UNIT_SP: // 转换为sp(与刻度无关的像素)值
//                return value * metrics.scaledDensity;
//            case COMPLEX_UNIT_PT: // 转换为pt(磅)值
//                return value * metrics.xdpi * (1.0f / 72);
//            case COMPLEX_UNIT_IN: // 转换为in(英寸)值
//                return value * metrics.xdpi;
//            case COMPLEX_UNIT_MM: // 转换为mm(毫米)值
//                return value * metrics.xdpi * (1.0f / 25.4f);
//        }
//        return 0;
//    }
    /**
     * Convert Dp to Pixel
     */
    public static int dpToPx(float dp, Resources resources){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static int getRelativeTop(View myView) {
//	    if (myView.getParent() == myView.getRootView())
        if(myView.getId() == android.R.id.content)
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    public static int getRelativeLeft(View myView) {
//	    if (myView.getParent() == myView.getRootView())
        if(myView.getId() == android.R.id.content)
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    /**
     * 改变一个View背景的资源的颜色
     * @param view
     * @param argbColor
     */
    public static void changeViewBgDrawableColor(View view, int argbColor) {
        if (view != null) {
            GradientDrawable gradientDrawable;
            try {
                gradientDrawable = (GradientDrawable) view.getBackground();
                gradientDrawable.setColor(argbColor);
            } catch (Exception e) {

            }
        }
    }

    /**
     * 给图像着色
     * @param originDrawable 源图像，可以是View的背景图
     * @param colors
     * @return 着色后的Drawable
     * 参考：http://www.race604.com/tint-drawable/
     */
    public static Drawable tintDrawable(Drawable originDrawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(originDrawable);
        if (wrappedDrawable != null) {//avoid nullpointer exception
            DrawableCompat.setTintList(wrappedDrawable, colors);
        }
        return wrappedDrawable;
    }

    /**
     * 用来改变ImageView的饱和度的
     * @param target
     * @param saturationValue
     */
    public static void changeImageViewSaturation(ImageView target, int saturationValue) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(saturationValue);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        target.setColorFilter(colorMatrixColorFilter);
    }

    /**
     * 修改一张源位图的饱和度来生成一个新位图
     * @param sourceBitmap 源位图
     * @param expectantSaturation 期望的饱和度值
     * @return 更改饱和度后的新位图
     */
    public static Bitmap changeSrcBitmapSaturation(Bitmap sourceBitmap,int expectantSaturation) {
        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();
        Bitmap compoundResultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(compoundResultBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(expectantSaturation);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(sourceBitmap, 0, 0, paint);
        return compoundResultBitmap;
    }
    /**
     * 给输入框控件View的光标着色
     * Android 3.1 (API 12) 开始就支持了 textCursorDrawable，
     * 也就是可以自定义光标的 Drawable。遗憾的是，这个方法只能在 xml 中使用
     * 所以通过反射来设置
     * @param curEditText
     * @param expectedCursorColor 所期望的光标颜色
     * 参考：http://www.race604.com/tint-drawable/
     */
    public static void tintCursorDrawable(EditText curEditText, int expectedCursorColor) {
        Field fCursorDrawableRes = ReflectUtil.getFidelOfClass(TextView.class, "mCursorDrawableRes");
        if (fCursorDrawableRes == null) {
            return;
        }
        try {
            //拿到TextView类中所定义的mCursorDrawableRes 当前的值
            int mCursorDrawableRes = fCursorDrawableRes.getInt(curEditText);
            if (mCursorDrawableRes <= 0) {
                return;
            }
            //拿到cursorDrawable 的资源图像对象
            Drawable cursorDrawable = curEditText.getContext().getResources().getDrawable(mCursorDrawableRes);
            if (cursorDrawable == null) {
                return;
            }
            //拿到TextView中Editor属性对象,Editor类被Android系统隐藏了@hide
            Field fieldMEditor = ReflectUtil.getFidelOfClass(TextView.class, "mEditor");
            Object mEditor = fieldMEditor.get(curEditText);
            //从Editro类中获取mCursorDrawable属性
            Field mCursorDrawableField = ReflectUtil.getFieldOfObject(mEditor, "mCursorDrawable");
            Drawable tintedCursorDrawable = tintDrawable(cursorDrawable, ColorStateList.valueOf(expectedCursorColor));
            Drawable[] newCursorDrawables = new Drawable[]{tintedCursorDrawable, tintedCursorDrawable};
            //更改mEditor中mCursorDrawable属性对象的值
            mCursorDrawableField.set(curEditText, newCursorDrawables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验文本输入框当前是否输入的是空格
     * @param curEditText
     * @return true 当前输入的是空格; false:不是
     */
    public static boolean checkEditTextInputedNull(EditText curEditText) {
        boolean curInputedNull = false;
        if (curEditText == null) {
            return curInputedNull;
        }
        String textAfterInputed = curEditText.getText().toString();
        int newLen = textAfterInputed.length();
        if (newLen > 0) {
            char[] chars = textAfterInputed.toCharArray();
            curInputedNull = chars[newLen - 1] == ' ';
            if (curInputedNull) {
                if (newLen == 1) {
                    curEditText.setText("");//替换成空字符串，此空字符串的Len = 0;
                }
                else{//输入后的文本不只有1个字符的情况，把空字符给去除掉
                    String trimedStr = textAfterInputed.trim();
                    curEditText.setText(trimedStr);
                    curEditText.setSelection(trimedStr.length());//并将光标移到文本末尾
                }
            }
        }
        return curInputedNull;
    }

    /**
     * 阻止快速点击视图
     * @param clickedView 被点击的View控件
     * @param needGapMillTimes 两次点击需要间隔的毫秒时间
     * @return true:需要阻止此次点击；false:不需要，即为有效点击
     */
    public static boolean preventFastClickView(View clickedView, long needGapMillTimes) {
        int tagKey = R.id.view_double_click_tag_id;
        String lastClickTimeStr = (String) clickedView.getTag(tagKey);
        boolean need2PreVent = false;
        long lastClickMillTime = 0;
        if (!Util.isEmpty(lastClickTimeStr)) {
            try {
                lastClickMillTime = Long.parseLong(lastClickTimeStr);
            } catch (Exception e) {
            }
        }
        long curClickMillTime = System.currentTimeMillis();
        if ((curClickMillTime - lastClickMillTime) < needGapMillTimes) {
            need2PreVent = true;
        }
        clickedView.setTag(tagKey, curClickMillTime + "");
        return need2PreVent;
    }

    /**
     * 获取一个Activity中所在的window中的用来填充该activity布局的容器视图
     * @param curActivity
     * @return 可能通过
     */
    public static FrameLayout getContentContainerView(Activity curActivity) {
        if (curActivity != null) {
            return (FrameLayout) curActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        }
        return null;
    }

    /**
     * 往一个Activity的窗口布局里增加一个View
     * @param toAddView 要添加的任意view
     * @param curActivity 当前Activity界面
     * @return FrameLayout.LayoutParams 便于能继续设置其他布局参数
     */
    public static FrameLayout.LayoutParams addView2ContainerView(View toAddView, Activity curActivity) {
        if (toAddView == null || toAddView.getParent() != null) {
            return null;
        }
        FrameLayout containerView = getContentContainerView(curActivity);
        if (containerView != null) {
            ViewGroup.LayoutParams vlp = toAddView.getLayoutParams();
            if (vlp == null) {//要添加的View无LayoutParams
                vlp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                containerView.addView(toAddView, vlp);
            }
            else{
                containerView.addView(toAddView);//这样直接添加好象会使toAddView 的宽高如果是wrap_content的会全部占据containerView的高
            }
        }
        return (FrameLayout.LayoutParams) toAddView.getLayoutParams();
    }
    private static final int INVALID_COLOR_VALUE = -1;
    private static final int COLOR_DEFAULT = Color.parseColor("#20000000");

    /**
     * 引用来自：https://github.com/hongyangAndroid/ColorfulStatusBar
     * @param activity
     * @param statusBarColor
     */
    @SuppressLint("NewApi")
    public static void changeStatusBarColor(Activity activity, int statusBarColor) {
        if(Util.isCompateApi(21)){
            //android 5.0+系统
            if(statusBarColor != INVALID_COLOR_VALUE){
                activity.getWindow().setStatusBarColor(statusBarColor);//need api 21
            }
        }
        else if(Util.isCompateApi(19)){
            //android 4.4 ~ android 5.0（不含）
            int color = COLOR_DEFAULT;
            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            if (statusBarColor != INVALID_COLOR_VALUE)
            {
                color = statusBarColor;
            }
            View statusBarView = contentView.getChildAt(0);
            //改变颜色时避免重复添加statusBarView
            if (statusBarView != null && statusBarView.getMeasuredHeight() == getStatusBarHeight(activity))
            {
                statusBarView.setBackgroundColor(color);
                return;
            }
            statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(activity));
            statusBarView.setBackgroundColor(color);
            contentView.addView(statusBarView, lp);
        }
    }

    /**
     * android 5.0+ 时，什么都没做；
     * android 4.4+时，给statusbar设置了默认的背景颜色
     * @param activity
     */
    public static void compatStatusColor(Activity activity) {
        changeStatusBarColor(activity, INVALID_COLOR_VALUE);
    }
    private static int getStatusBarHeight(Context context)
    {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 用来判断当前Touch事件是否在某个view上
     * @param toJudgedView
     * @param touchX
     * @param touchY
     * @return
     */
    public static boolean isTouchIngTheView(View toJudgedView, int touchX, int touchY) {
        if (toJudgedView == null) {
            return false;
        }
        int[] location = new int[2];
        toJudgedView.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + toJudgedView.getMeasuredWidth();
        int bottom = top + toJudgedView.getMeasuredHeight();
        //view.isClickable() &&
        if (touchY >= top && touchY <= bottom && touchX >= left
                && touchX <= right) {
            return true;
        }
        return false;
    }

    public static View trackOutCurTouchingView(View viewGroup, int curTouchX, int curTouchY) {
        View targetTouchingView = null;
        ArrayList<View> allCanTouchedViews = viewGroup.getTouchables();
        for (View oneView : allCanTouchedViews) {
            if (isTouchIngTheView(oneView, curTouchX, curTouchY)) {
                targetTouchingView = oneView;
                break;
            }
        }
        return targetTouchingView;
    }

    public static void imageViewRounded(ImageView iv, int imageResId, int roundedRadius) {
        Resources res = iv.getResources();
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(res, BitmapFactory.decodeResource(res, imageResId));
        roundedBitmapDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, roundedRadius, res.getDisplayMetrics()));
        iv.setImageDrawable(roundedBitmapDrawable);
    }

    /**
     * 改变TabLayout中item的左右margin来改变indicator的长度的方案
     * @param context
     * @param tabs
     * @param leftMarginDpValue 左边距
     * @param rightMarginDpValue 右边距
     */
    public static void setTabLayoutIndicatorW(Context context, TabLayout tabs, int leftMarginDpValue, int rightMarginDpValue) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (tabStrip == null) {
            return;
        }

        tabStrip.setAccessible(true);
        LinearLayout ll_tab = null;
        try {
            ll_tab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (ll_tab == null) {
            return;
        }
        int left = dpToPx(leftMarginDpValue, context.getResources());
        int right = dpToPx(rightMarginDpValue, context.getResources());
        for (int i = 0; i < ll_tab.getChildCount(); i++) {
            View child = ll_tab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT
                    , 1
            );
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }

    public static void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= 19 && view != null) {//Android 4.4
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }

    @RequiresApi(api = 19)
    public static boolean isWindowTranslucentStatus(Window window) {
        if (window != null) {
            if (Util.isCompateApi(19)) {
                if (window.getAttributes() != null) {
                    //API 19
                    int translucentStatusFlag = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS & window.getAttributes().flags;
                    return translucentStatusFlag == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                }
            }
        }
        return false;
    }

    public static void hideNavigation(Window curWindow) {
        if (curWindow != null && Build.VERSION.SDK_INT >= 19) {
            //隐藏Navigation bar
            WindowManager.LayoutParams layoutParams = curWindow.getAttributes();
            layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            ;
//        layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
            curWindow.setAttributes(layoutParams);
//            View decorView = curWindow.getDecorView();
//            if (decorView != null) {
//                int oldUiFlags = decorView.getSystemUiVisibility();
//                int uiFlags =
//                        oldUiFlags |
//                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                                | View.SYSTEM_UI_FLAG_IMMERSIVE
//                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
////                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
////                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//                decorView.setSystemUiVisibility(uiFlags);
//            }
        }
    }

//    @Deprecated
//    public static void hideNavigationBar(Window w) {
//        if (w == null) {
//            return;
//        }
//        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
//
//        if (android.os.Build.VERSION.SDK_INT >= 19) {
//            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
//        } else {
//            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
//        }
//
//        w.getDecorView().setSystemUiVisibility(uiFlags);
//    }

    /**
     * 使用像素来设置TextView的文本大小
     * @param tv TextView
     * @param pixelValue 像素单位的文本大小
     */
    public static void setTextViewTextSizeWithPixelValue(TextView tv, int pixelValue) {
        if (tv != null) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,pixelValue);
        }
    }

    public static ObjectAnimator animView(View toAnimView, String propertyName,int...values) {
        ObjectAnimator animator = ObjectAnimator.ofInt(toAnimView, propertyName, values);
        return animator;
    }

    public static void animProgressBar(ProgressBar pb, int fromValue, int toValue) {
        if (pb != null) {
            if (fromValue == -1) {
                fromValue = pb.getProgress();
            }
            int maxProgress = pb.getMax();
            if (toValue == -1 || toValue > maxProgress) {
                toValue = maxProgress;
            }
            animView(pb, "progress", fromValue, toValue)
                    .setDuration(2000)
                    .start()
            ;
        }
    }

    /**
     * 代码的方式来设置沉浸式界面
     * @param curActivity 当前Activity
     * @param needFullScreen 是否需要全屏：一般为false
     * @param translucentNavBar 是否需要透明导航栏：一般为true
     * @param hideNavigation 是否要隐藏导航栏： 一般为true
     * @param drawStatusBarBg 是否需要在Android 5.0及以上 接管状态栏背景绘制，用于指定 状态栏背景颜色
     * @param statusBarColor 想要的状态栏颜色
     * @param isLightStatusBar 是否需要在Android 6.0及以上 启用状态栏的灰色文字和图标
     */
    public static void immersiveScreen(Activity curActivity,boolean needFullScreen,
                                       boolean translucentNavBar,boolean hideNavigation,
                                       boolean drawStatusBarBg, int statusBarColor,boolean isLightStatusBar) {
        if (curActivity != null) {
            Window window = curActivity.getWindow();
            if (window != null) {
                WindowManager.LayoutParams windowParams = window.getAttributes();
                if (windowParams != null) {
                    //已经存在的 可见性参数
                    int mayExistVisibility = windowParams.systemUiVisibility;
//                    int mayExistFlags = windowParams.flags;
                    int curSdkInt = Build.VERSION.SDK_INT;
                    if (needFullScreen) {
//                        mayExistFlags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    }

                    if (curSdkInt >= 19) {//Android 4.4后才有沉浸式
                        if (translucentNavBar) {
//                            mayExistFlags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                        }
                        //默认的实现沉浸式flag
                        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                        if (curSdkInt >= 21) {
                            if (drawStatusBarBg) {//如果需要 代替绘制状态栏的背景,则需要清除 FLAG_TRANSLUCENT_STATUS
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            }
                            if (statusBarColor != -1) {
                                window.setStatusBarColor(statusBarColor);
                            }
                            if (curSdkInt >= 23) {//Android 6.0以上 实现状态栏字色和图标浅黑色
                                if (isLightStatusBar) {
                                    mayExistVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                                }
                            }
                        }
                        if (hideNavigation) {//隐藏导航栏
                            mayExistVisibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            ;
                            windowParams.systemUiVisibility = mayExistVisibility;
                            window.setAttributes(windowParams);
                        }
                    }//curSdkInt >= 19 end
                }
            }
        }
    }

    public static String motionActionToString(int action,boolean forChineseStr) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return forChineseStr ? "按下" : "ACTION_DOWN";
            case MotionEvent.ACTION_UP:
                return forChineseStr ? "抬起" : "ACTION_UP";
            case MotionEvent.ACTION_CANCEL:
                return forChineseStr ? "取消" : "ACTION_CANCEL";
            case MotionEvent.ACTION_OUTSIDE:
                return forChineseStr ? "外部" : "ACTION_OUTSIDE";
            case MotionEvent.ACTION_MOVE:
                return forChineseStr ? "移动" : "ACTION_MOVE";
            case MotionEvent.ACTION_HOVER_MOVE:
                return forChineseStr ? "悬浮移动(一般是鼠标)" : "ACTION_HOVER_MOVE";
            case MotionEvent.ACTION_SCROLL:
                return forChineseStr ? "滚动" : "ACTION_SCROLL";
            case MotionEvent.ACTION_HOVER_ENTER:
                return "ACTION_HOVER_ENTER";
            case MotionEvent.ACTION_HOVER_EXIT:
                return "ACTION_HOVER_EXIT";
            case MotionEvent.ACTION_BUTTON_PRESS:
                return "ACTION_BUTTON_PRESS";
            case MotionEvent.ACTION_BUTTON_RELEASE:
                return "ACTION_BUTTON_RELEASE";
        }
        int index = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                return "ACTION_POINTER_DOWN(" + index + ")";
            case MotionEvent.ACTION_POINTER_UP:
                return "ACTION_POINTER_UP(" + index + ")";
            default:
                return Integer.toString(action);
        }
    }
}
