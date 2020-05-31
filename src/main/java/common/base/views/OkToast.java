package common.base.views;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import common.base.R;
import common.base.WeakHandler;
import common.base.dialogs.HintDialog;
import common.base.utils.CommonLog;
import common.base.utils.MrNotification;
import common.base.utils.Util;
import common.base.utils.ViewUtil;

/**
 * ******************(^_^)***********************<br>
 * User: 11776610771@qq.com<br>
 * Date: 2017/11/14<br>
 * Time: 10:33<br>
 * <P>DESC:
 * Toast的通用类，链式调用的方式可以非常方便的自定义各种布局的Toast
 * 本类中默认的Toast布局为左图标(默认不显示)右Toast文本的方式，如果再要扩展自定义，则通过{@link #withExtraView(View, int)}的方式扩展
 * </p>
 * ******************(^_^)***********************
 */

public class OkToast {
    private Toast mToast;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Context mAppContext;
    private LinearLayout llToastRootView;
    private TextView tvToast;
    private ImageView ivDefToastHint;
    private LinearLayout llToastViewContainer;
    /**
     * 保存上一次的toast文案
     */
    private CharSequence lastToastText;

    private final String TAG = "OkToast";
    /**
     * 是否上一个 Toast的视图脱离了Window
     */
    private boolean lastToastViewDetached = false;
    /**
     * 兼容方案二：采用windown addView的方式；
     * 有两个注意点：
     * 1)context应为Activity，并且在没有授权悬浮窗权限时只能在当前Activity上显示
     * 2)需要Activity销毁时，及时移除
     */
    private WindowManager windowManager;

    private WeakHandler msgHandler;
    private View customToastView;
    /**
     * 为了兼容app被用户禁止了通知权限而toast无法显示，本次使用Dialog来兼容
     */
    private HintDialog hintDialogCompatToast;
    private OkToast() {

    }

    public static OkToast with(Context appContext) {
        OkToast one = new OkToast();
        one.innerWith(appContext);
        return one;
    }
    private OkToast innerWith(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.mContext = context;
        mAppContext = context.getApplicationContext();
        mLayoutInflater = LayoutInflater.from(context);
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

    /**
     * 该方法目前只接收大于0的正数padding值
     * 注：单位为像素
     * @param leftPadding 内部左边距
     * @param topPadding 内部上边距
     * @param rightPadding 内部右边距
     * @param bottomPadding 内部底边距
     * @return self
     */
    public OkToast withTextPadding(int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        if (tvToast != null) {
            int newLeftPadding = leftPadding > 0 ? leftPadding : tvToast.getPaddingLeft();
            int newTopPadding = topPadding > 0 ? topPadding : tvToast.getPaddingTop();
            int newRightPadding = rightPadding > 0 ? rightPadding : tvToast.getPaddingRight();
            int newBottomPadding = bottomPadding > 0 ? bottomPadding : tvToast.getPaddingBottom();
            tvToast.setPadding(newLeftPadding, newTopPadding, rightPadding, newBottomPadding);
        }
        return this;
    }

    public TextView peekToastTextView() {
        return tvToast;
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

    public LinearLayout.LayoutParams withExtraViewResultLLP(View extraView, int viewPos) {
        withExtraView(extraView, viewPos);
        return (LinearLayout.LayoutParams) extraView.getLayoutParams();
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

    /**
     * 直接使用像素值来设置TextView的文本大小
     * @param pixelValue 像素值
     * @return self
     */
    public OkToast withTextSizePixelValue(float pixelValue) {
        if (tvToast != null) {
            tvToast.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelValue);
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

    public void shortShow(CharSequence toastText, int showGravity, int xOffset, int yOffset) {
        show(toastText, Toast.LENGTH_SHORT, showGravity, xOffset, yOffset);
    }
    public void show(CharSequence toastText, int duration, int showGravity, int xOffset, int yOffset) {
        show(toastText,null,duration,showGravity,xOffset,yOffset,0,0);
    }
    private int defToastGravity,defXOffset,defYOffset;
    private View.OnAttachStateChangeListener onAttachStateChangeListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {

        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            View curToastView = null;
            if (mToast != null) {
                curToastView = mToast.getView();
            }
            boolean isCurToastView = v == curToastView;
            CommonLog.e("OkToast", "--> onViewDetachedFromWindow() v = " + v + " isCurToastView = " + isCurToastView);
            if (isCurToastView ) {
                lastToastViewDetached = true;
            }
        }
    };

    public void showCompatToast(CharSequence toastText, View customToastView, int duration, int showGravity, int xOffset, int yOffset, float horizontalMargin, float verticalMargin) {
//        if (tvToast != null && !Util.isEmpty(toastText)) {
//            tvToast.setText(toastText);
//        }
//        if (customToastView != null) {
//           this.customToastView = customToastView;
//        }
//        if (duration <= 0) {
//            duration = 500;
//        }
//        WindowManager.LayoutParams wlp = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        wlp.gravity = showGravity;
        if (hintDialogCompatToast == null) {
            hintDialogCompatToast = new HintDialog(mContext);
            int tvToastPaddingLeft = tvToast.getPaddingLeft();
            int tvToastPaddingRight = tvToast.getPaddingRight();
            int tvToastPaddingTop = tvToast.getPaddingTop();
            int tvToastPaddingBottom = tvToast.getPaddingBottom();
            hintDialogCompatToast.withTextPadding(tvToastPaddingLeft,tvToastPaddingTop,tvToastPaddingRight,tvToastPaddingBottom)
                .withTextSizePixelValue(tvToast.getTextSize())
                .withBackground(llToastRootView.getBackground())
//            .setCanceledOnTouchOut(true)
            .showHoldTime(2000)
            ;
        }
        hintDialogCompatToast.withToastText(toastText)
                .show();
    }

    private Toast newToast(View toastView,String dueToWhat) {
        Toast newToast = new Toast(mAppContext);
        if (toastView != null) {
//            View rootView = toastView.getRootView();//root view就是
            ViewParent viewParent = toastView.getParent();
            boolean isParentViewGroup = false;
            if (viewParent != null) {
                isParentViewGroup = viewParent instanceof ViewGroup;
            }
            CommonLog.d("OkToast", "-->newToast() toastView = " + toastView + " dueToWhat = " + dueToWhat + " viewParent = " + viewParent
//                    + " rootView = " + rootView.getTag()
                    + " isParentViewGroup = " + isParentViewGroup
            );
            lastToastViewDetached = false;
            toastView.removeOnAttachStateChangeListener(onAttachStateChangeListener);
            toastView.addOnAttachStateChangeListener(onAttachStateChangeListener);
            newToast.setView(toastView);//changed by fee 2019-06-04:21:05 != null时才设置
        }
//        if (toastView != null) {//changed by fee 2019-06-04:21:05
//            mToast.setView(toastView);
//        }
//        mToast.setView(toastView);
        if (defToastGravity == 0) {// 这里表示拿到系统 Toast的 默认 Gravity等信息
            defToastGravity = newToast.getGravity();
            defXOffset = newToast.getXOffset();
            defYOffset = newToast.getYOffset();
            CommonLog.d(TAG, "--> newToast() defToastGravity = " + defToastGravity + " defXOffset = " + defXOffset + " defYOffset = " + defYOffset + " dueToWhat = " + dueToWhat);
        }
        return newToast;
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

//    public void cancelShow() {
//        if (mToast != null) {
//            mToast.cancel();
//        }
//    }

    public void cancelShow(boolean cancelToast) {
        if (mToast != null) {
            if (cancelToast) {
                mToast.cancel();
            }
        }
        if (hintDialogCompatToast != null && hintDialogCompatToast.isShowing()) {
            hintDialogCompatToast.cancel();
        }
    }

    /**
     * 默认就是Short
     * 这个参数太多，不建议使用
     * @param toastText
     * @param customToastView
     * @param duration
     * @param willShowGravity
     * @param xOffset
     * @param yOffset
     * @param horizontalMargin
     * @param verticalMargin
     */
    public void show(CharSequence toastText,View customToastView, int duration, int willShowGravity, int xOffset, int yOffset
            ,float horizontalMargin, float verticalMargin) {
        //added by fee 2019-01-08:兼容无通知栏权限时的提示
        if (Util.isEmpty(toastText)) {//added by fee 2020-05-29: 当Toast的文案为空时，不必要进行下面的流程
            lastToastText = "";
            return;
        }
        if (!MrNotification.isNotifyPermissionAllowed(mAppContext)) {
            showCompatToast(toastText, customToastView, duration, willShowGravity, xOffset, yOffset, horizontalMargin, verticalMargin);
            return;
        }
//        if (tvToast != null /*&& !Util.isEmpty(toastText)*/) {
//            tvToast.setText(toastText);
//        }
        if (defToastGravity == 0) {
            newToast(null, "just to solve defToastGravity");
        }
        String newToastReason = null;
        int theFinalGravity = defToastGravity;
        if (willShowGravity >= 0) {
            theFinalGravity = willShowGravity;
        }
        if (customToastView != null) {
            mToast = newToast(customToastView," customToastView != null");
            //todo ?? 这里有疑问,是否需要对于 new 出来的 Toast先设置 gravity
            mToast.setGravity(theFinalGravity,0,0);
        }
        else{//没有指定 Toast自定义的View时
            if (mToast != null) {
                if (!lastToastViewDetached) {
                    newToastReason = "上一个Toast还在show";
                    mToast.cancel();
                    mToast = null;
                }
                if (mToast != null) {


                boolean isToastViewSame = mToast.getView() == llToastRootView;
                boolean isSameWithLastToastText = toastText.equals(lastToastText);
                                                                 //上一次的Toast还未消失 并且  和上一次的Toast文案相同
                boolean isWillSameToastTextAndLastToastShowing = !lastToastViewDetached && isSameWithLastToastText;
                if (isWillSameToastTextAndLastToastShowing) {
                    toastViewRemoveListener();
                    lastToastViewDetached = true;
//                    mToast.cancel();
                }
                if (!isToastViewSame/**上一次的Toast的视图和本次的默认llToastRootView不同**/ || isWillSameToastTextAndLastToastShowing) {
                    mToast = null;
                    if (!isToastViewSame) {
                        newToastReason = "本次Toast的视图和上次的不同";
                    }
                    else {
                        newToastReason = "相同文案并且上一个Toast在显示";
                    }
                    CommonLog.d(TAG, "--> show() isToastViewSame = " + isToastViewSame + " isWillSameToastTextAndLastToastShowing = " + isWillSameToastTextAndLastToastShowing);
                }
                }
            }
        }
        lastToastText = toastText;
        if (mToast == null) {
            mToast = newToast(llToastRootView, newToastReason);
            //todo ?? 这里有疑问,是否需要对于 new 出来的 Toast先设置 gravity
            mToast.setGravity(theFinalGravity,0,0);
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
        int oldGravity = mToast.getGravity();
        int a = Gravity.TOP;
        CommonLog.i(TAG, "-->show() oldGravity = " + oldGravity + " theFinalGravity = " + theFinalGravity + " a = " + a);
        if (theFinalGravity != oldGravity) {//处理Gravity不同的情况时
            mToast.cancel();//cancel之后 是show不出来的
            View toastView = mToast.getView();
            mToast = null;
            mToast = newToast(toastView, " theGravity != oldGravity " + "? theFinalGravity = " + theFinalGravity + " oldGravity = " + oldGravity);
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
        if (tvToast != null /*&& !Util.isEmpty(toastText)*/) {
            tvToast.setText(toastText);
        }
//        CommonLog.e("info", "--》x= " + x + " y  = " + y);
        mToast.setGravity(theFinalGravity, x, y);
        mToast.setDuration(duration);
        lastToastViewDetached = false;
        mToast.show();
    }

    private void defToastViewRemoveListener() {
        if (llToastRootView != null) {
            llToastRootView.removeOnAttachStateChangeListener(onAttachStateChangeListener);
        }
    }

    private void toastViewRemoveListener() {
        View curToastViw = null;
        if (mToast != null) {
            curToastViw = mToast.getView();
        }
        if (curToastViw != null) {
            curToastViw.removeOnAttachStateChangeListener(onAttachStateChangeListener);
        }
    }
}
