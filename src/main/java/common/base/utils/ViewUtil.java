package common.base.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
     * 校验文本输入框当前是否输入的是空格
     * @param curEditText
     * @return true 当前输入的是空格; falase:不是
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
}
