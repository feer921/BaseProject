package common.base.views;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import common.base.R;
import common.base.utils.CommonLog;
import common.base.utils.Util;
import common.base.utils.ViewUtil;

/**
 * ******************(^_^)***********************<br>
 * User: 11776610771@qq.com<br>
 * Date: 2017/11/14<br>
 * Time: 10:33<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */

public class OkToast {
    private Toast mToast;
    private LayoutInflater mLayoutInflater;
    private Context appContext;
    private LinearLayout llToastRootView;
    private TextView tvToast;
    private ImageView ivDefToastHint;
    private LinearLayout llToastViewContainer;

    private OkToast() {

    }

    public static OkToast with(Context appContext) {
        OkToast one = new OkToast();
        one.with2(appContext);
        return one;
    }
    private OkToast with2(Context appContext) {
        this.appContext = appContext.getApplicationContext();
        mLayoutInflater = LayoutInflater.from(this.appContext);
        llToastRootView = (LinearLayout) mLayoutInflater.inflate(R.layout.ok_toast, null);
        tvToast = ViewUtil.findViewInView(llToastRootView, R.id.toast_msg);
        ivDefToastHint = ViewUtil.findViewInView(llToastRootView, R.id.toast_icon);
        llToastViewContainer = ViewUtil.findViewInView(llToastRootView, R.id.ll_toast_container);
        return this;
    }

    public OkToast withDefToastHintIcon(boolean isShow) {
        if (ivDefToastHint != null) {
            ivDefToastHint.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
        return this;
    }
    public OkToast withToastText(CharSequence toastText) {
        if (tvToast != null) {
            tvToast.setText(toastText);
        }
        return this;
    }
    private SparseArray<View> extraViews;
    public OkToast withToastTopView(View topView) {
        return withExtraView(topView, 0);
    }

    public OkToast withExtraView(View extraView, int viewPos) {
        if (llToastRootView != null) {
            llToastRootView.addView(extraView,viewPos);
        }
        if (extraViews == null) {
            extraViews = new SparseArray<>();
        }
        extraViews.put(viewPos, extraView);
        return this;
    }

    public OkToast clearAllExtraViews() {
        if (llToastRootView != null && extraViews != null && extraViews.size() > 0) {
            for(int i = 0; i < extraViews.size(); i++) {
                View addedView = extraViews.valueAt(i);
                llToastRootView.removeView(addedView);
            }
            extraViews.clear();
        }
        return this;
    }
    public OkToast withBackground(@DrawableRes int background) {
        if (llToastRootView != null) {
            llToastRootView.setBackgroundResource(background);
        }
        return this;
    }

    public OkToast withTextSize(float spSize) {
        if (tvToast != null) {
            tvToast.setTextSize(spSize);
        }
        return this;
    }

    public OkToast withTextColor(@ColorInt int textColor) {
        if (tvToast != null) {
            tvToast.setTextColor(textColor);
        }
        return this;
    }

    public void shortShow(CharSequence toastText) {
        show(toastText, Toast.LENGTH_SHORT);
    }

    public void show(int duration) {
        show("",duration);
    }

    public void shortShow() {
        show("",Toast.LENGTH_SHORT);
    }
    public void show(CharSequence toastText, int duration) {
        show(toastText, null, duration, -1, 0, 0, 0, 0);
    }

    public void show(CharSequence toastText, int duration, int showGravity) {
        show(toastText, null, duration, showGravity, 0, 0, 0, 0);
    }
    private int defToastGravity,defXOffset,defYOffset;

    private Toast newToast(View toastView) {
        Toast mToast = new Toast(appContext);
        mToast.setView(toastView);
        if (defToastGravity == 0) {
            defToastGravity = mToast.getGravity();
            defXOffset = mToast.getXOffset();
            defYOffset = mToast.getYOffset();
        }
        return mToast;
    }
    /**
     * 默认就是Short
     * 这个参数太多，不建议使用
     * @param toastText
     * @param customToastView
     * @param duration
     * @param showGravity
     * @param xOffset
     * @param yOffset
     * @param horizontalMargin
     * @param verticalMargin
     */
    public void show(CharSequence toastText,View customToastView, int duration, int showGravity, int xOffset, int yOffset
            ,float horizontalMargin, float verticalMargin) {
        if (tvToast != null && !Util.isEmpty(toastText)) {
            tvToast.setText(toastText);
        }
        if (customToastView != null) {
            mToast = newToast(customToastView);
        }
        else{
            if (mToast != null) {
                if (mToast.getView() != llToastRootView) {
                    mToast = null;
                }
            }
        }
        if (mToast == null) {
            mToast = newToast(llToastRootView);
        }
        if (duration <= 0) {
            duration = Toast.LENGTH_SHORT;
        }
        float oldHorizontalMargin = mToast.getHorizontalMargin();
        float oldVerticalMargin = mToast.getVerticalMargin();
        if (horizontalMargin > 0) {
            oldHorizontalMargin = horizontalMargin;
        }
        if (verticalMargin > 0) {
            oldVerticalMargin = verticalMargin;
        }
        int theGravity = defToastGravity;
        if (showGravity >= 0) {
            theGravity = showGravity;
        }
        int oldGravity = mToast.getGravity();
        if (theGravity != oldGravity) {//处理Gravity不同的情况时
            mToast.cancel();//cancel之后 是show不出来的
            View toastView = mToast.getView();
            mToast = null;
            mToast = newToast(toastView);
        }
        mToast.setMargin(oldHorizontalMargin,oldVerticalMargin);
        int x = this.xOffset;
        if (xOffset > 0) {
            x = xOffset;
        }
        if (x < 0) {
            x = defXOffset;
        }
        int y = this.yOffset;
        if (yOffset > 0) {
            y = yOffset;
        }
        if (y < 0) {
            y = defYOffset;
        }
        CommonLog.e("info", "--》x= " + x + " y  = " + y);
        mToast.setGravity(theGravity,x,y);
        mToast.setDuration(duration);
        mToast.show();
    }
    private int xOffset = -1,yOffset;
    public OkToast withXYOffset(int xOffset,int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        return this;
    }

    public void topshow(CharSequence toastText) {
        topShow(toastText,0);
    }
    public void topShow(CharSequence toastText, int duration) {
        show(toastText, duration, Gravity.TOP);
    }

    public void topLongShow(CharSequence toastText) {
        topShow(toastText,Toast.LENGTH_LONG);
    }
    public void centerShow(CharSequence toastText, int duration) {
        show(toastText,duration,Gravity.CENTER);
    }

    public void centerShow(CharSequence toastText) {
        centerShow(toastText,0);
    }
    public void centerLongShow(CharSequence toastText) {
        centerShow(toastText,Toast.LENGTH_LONG);
    }
    public void bottomShow(CharSequence toastText, int duration) {
        show(toastText,duration,Gravity.BOTTOM);
    }

    public void bottomShow(CharSequence toastText) {
        bottomShow(toastText, 0);
    }
    public void bottomLongShow(CharSequence toastText) {
        bottomShow(toastText,Toast.LENGTH_LONG);
    }
    public void cancelShow() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
