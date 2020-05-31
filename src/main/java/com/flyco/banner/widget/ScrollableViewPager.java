package com.flyco.banner.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import common.base.utils.CommonLog;
import common.base.utils.ViewUtil;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/3/6<br>
 * Time: 21:38<br>
 * <P>DESC:
 * 可设置是否滑动/滚动 的ViewPager
 * </p>
 * ******************(^_^)***********************
 */
public class ScrollableViewPager extends ViewPager {
    protected final String TAG = getClass().getSimpleName();
    /**
     * 当手指触摸时、滑动时 本视图需要 拦截/消费 触摸事件的 X轴方向上的滑动距离
     * 值越小 越灵敏,但结果到底拦不拦截还取决于 Y轴方向的移动情况
     */
    private final int needInterceptXDistance = 5;
    /**
     * 当手指触摸时、滑动时 本视图需要 拦截/消费 触摸事件的 Y轴方向上的滑动不拦截的距离
     * 值越小，判断越精细、不灵敏,但结果到底拦不拦截还取决于 X轴方向的移动情况
     * 因为本类为ViewPager 翻页的默认处理方向为水平滑动，所以应该避免手指移动时Y轴方向的绝对距离太大,该值应该尽量小
     */
    private final int notInterceptYDistance = 30;
    public ScrollableViewPager(@NonNull Context context) {
        this(context,null);
    }

    public ScrollableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * 本 ViewPager 是否可以 手指滑动滚动
     * def = true
     */
    protected boolean canScroll = true;
    /**
     * 本 ViewPager 是否需要处理事件(滑动事件) 冲突
     * def = false
     */
    protected boolean needHandleEventConflict;
    private int lastXOnIntercept,lastYOnIntercept;
    private int lastTouchX,lastTouchY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // return false;//可行,不拦截事件,
        // return true;//不行,孩子无法处理事件
        //return super.onInterceptTouchEvent(ev);//不行,会有细微移动
        if (canScroll){
            if (!needHandleEventConflict) {//如果不处理 滑动冲突，则直接返回父类的处理就行
                return super.onInterceptTouchEvent(ev);
            }
            int action = ev.getAction();
            CommonLog.i(TAG, "--> onInterceptTouchEvent() action: " + ViewUtil.motionActionToString(action, true));
            boolean needIntercept = false;
            int curX = (int) ev.getX();
            int curY = (int) ev.getY();
            switch (action) {
                case MotionEvent.ACTION_DOWN://按下
                    lastXOnIntercept = curX;
                    lastYOnIntercept = curY;
                    break;
                case MotionEvent.ACTION_MOVE://移动
                    CommonLog.i(TAG, "-->onInterceptTouchEvent() curX = " + curX + " curY = " + curY
                            + " lastXOnIntercept = " + lastXOnIntercept
                            + " lastYOnIntercept = " + lastYOnIntercept
//                            + " rawX = " + ev.getRawX()//该值为 当前 触摸处在 屏幕坐标系下的X值
                    );
                    int moveXDistance = Math.abs(lastXOnIntercept - curX);
                    int moveYDistance = Math.abs(lastYOnIntercept - curY);
                    boolean canJudgeSlideX = moveXDistance >= needInterceptXDistance;
                    boolean canJudgeSlideY = moveYDistance <= notInterceptYDistance;
                    CommonLog.i(TAG, "--> onInterceptTouchEvent() canJudgeSlideX = " + canJudgeSlideX + " canJudgeSlideY = " + canJudgeSlideY);
                    if (canJudgeSlideX && canJudgeSlideY) {
                        requestDisallowInterceptTouchEvent(true);
                        needIntercept = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_CANCEL:

                    break;
            }
            boolean isSuperIntercept = super.onInterceptTouchEvent(ev);
            CommonLog.w(TAG, "--> onInterceptTouchEvent() isSuperIntercept = " + isSuperIntercept + " needIntercept = " + needIntercept);
            return needIntercept || isSuperIntercept;
        }
        else{//当不让ViewPager 滚动时，则不拦截事件
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //return false;// 可行,不消费,传给父控件
        //return true;// 可行,消费,拦截事件
        //super.onTouchEvent(ev); //不行,
        //虽然onInterceptTouchEvent中拦截了,
        //但是如果viewpage里面子控件不是viewgroup,还是会调用这个方法.
        if (canScroll){
            if (!needHandleEventConflict) {//如果不需要处理 滑动冲突，则直接让父类处理就行
                return super.onTouchEvent(ev);
            }
            int action = ev.getAction();
            CommonLog.d(TAG, "--> onTouchEvent() action: " + ViewUtil.motionActionToString(action, true));
            boolean needConsume = false;
            int curX = (int) ev.getX();
            int curY = (int) ev.getY();
            switch (action) {
                case MotionEvent.ACTION_DOWN://按下
                    lastTouchX = curX;
                    lastTouchY = curY;
                    break;
                case MotionEvent.ACTION_MOVE://移动
                    CommonLog.i(TAG, "-->onTouchEvent() curX = " + curX + " curY = " + curY
                            + " lastTouchX = " + lastTouchX
                            + " lastTouchY = " + lastTouchY
//                            + " rawX = " + ev.getRawX()
                    );
                    int moveXDistance = Math.abs(lastTouchX - curX);
                    int moveYDistance = Math.abs(lastTouchY - curY);
                    boolean canJudgeSlideX = moveXDistance >= needInterceptXDistance;
                    boolean canJudgeSlideY = moveYDistance <= notInterceptYDistance;
                    CommonLog.i(TAG, "--> onTouchEvent() canJudgeSlideX = " + canJudgeSlideX + " canJudgeSlideY = " + canJudgeSlideY);
                    if (canJudgeSlideX && canJudgeSlideY) {
                        needConsume = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_CANCEL:

                    break;
            }
            boolean isSuperIntercept = super.onTouchEvent(ev);
            CommonLog.w(TAG, "--> onTouchEvent() isSuperIntercept = " + isSuperIntercept + " needIntercept = " + needConsume);
            return needConsume || isSuperIntercept;
        }else {
            return true;// 可行,消费,拦截事件
        }
    }
    public void setScrollable(boolean scroll) {
        canScroll = scroll;
    }

    public void setNeedHandleEventConflict(boolean needHandleEventConflict) {
        this.needHandleEventConflict = needHandleEventConflict;
    }
}
