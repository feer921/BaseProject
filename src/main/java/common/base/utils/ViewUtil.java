package common.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
     * 依据ListView的Chilc Views 计算所需要绘制的高度
     * @param listView
     */
    public static void resolveListViewWholeH(ListView listView) {
        if (listView == null || listView.getAdapter() == null) {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {

            View listItem = listAdapter.getView(i, null, listView);

            listItem.measure(0, 0);

            totalHeight += listItem.getMeasuredHeight();

        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        // params.height += 5;// if without this statement,the listview will be

        // a

        // little short

        // listView.getDividerHeight()获取子项间分隔符占用的高度

        // params.height最后得到整个ListView完整显示需要的高度

        listView.setLayoutParams(params);
    }

    /**
     * 获取一个Activity中所在的window中的用来填充该activity布局的容器视图
     * @param curActivity
     * @return 可能通过
     */
    public static FrameLayout getContentContainerView(Activity curActivity) {
        FrameLayout contentLayout = null;
        if (curActivity != null) {
            return (FrameLayout) curActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        }
        return contentLayout;
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
    public static int getStatusBarHeight(Context context)
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
}
