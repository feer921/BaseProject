package common.base.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持垂直下拉刷新的Layout
 * Created by leelay on 2016/12/3.
 * modified by fee
 */

public class VRefreshLayout extends ViewGroup {

    public final static int STATUS_INIT = 0;//原始状态
    public final static int STATUS_DRAGGING = 1;//正在下拉
    public final static int STATUS_RELEASE_PREPARE = 2;//松手将要刷新
    public final static int STATUS_REFRESHING = 3;//正在刷新
    public final static int STATUS_RELEASE_CANCEL = 4;//松手取消
    public final static int STATUS_COMPLETE = 5;//刷新完成
    private static final String TAG = "VRefreshLayout";

    /**
     * 本控件归属在哪的tag
     */
    private String belongTag;
    private int mStatus;

    private View mHeaderView;
    private SparseArray<String> stateDesc;
    {
        stateDesc = new SparseArray<>(6);
        stateDesc.put(STATUS_INIT, "状态：初始化");
        stateDesc.put(STATUS_DRAGGING, "状态：正在下拉");
        stateDesc.put(STATUS_RELEASE_PREPARE, "状态：松手将要刷新");
        stateDesc.put(STATUS_REFRESHING, "状态：正在刷新");
        stateDesc.put(STATUS_RELEASE_CANCEL, "状态：松手取消");
        stateDesc.put(STATUS_COMPLETE, "状态：刷新完成");
    }

    public String theStateDesc(int refreshState) {
        if (stateDesc != null) {
            return stateDesc.get(refreshState);
        }
        return "Unknow";
    }

    /**
     * 本容器视图的直接子视图，原来这里只兼容直接的子View：RecyclerView、ListView、AbsListView，
     * 如果RecyclerView在mContentView的里面则无效，需兼容之
     */
    private View mContentView;
    /**
     * 本容器内嵌套了的可滚动的视图控件，
     */
    private View canScrolableView;
    private int mHeaderOrginTop;

    private int mMaxDragDistance = -1;
    private float mDragRate = .5f;
    private int mHeaderCurrentTop;
    private int mHeaderLayoutIndex = -1;
    private boolean mIsInitMesure = true;
    private boolean mIsBeingDragged;
    private float mInitDownY;
    private float mInitMotionY;
    private boolean mIsRefreshing;
    private int mActivePointerId = -1;
    private float mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    private Progress mProgress = new Progress();
    private int mRefreshDistance;
    private int mToStartDuration = 200;
    private int mToRetainDuration = 200;
    private int mAutoRefreshDuration = 800;
    private int mCompleteStickDuration = 400;
    private float ratioOfHeaderHeightToRefresh = 1.0f;
    private float ratioOfHeaderHeightToReach = 1.6f;
    private List<OnRefreshListener> mOnRefreshListeners;
    private UpdateHandler mUpdateHandler;
    private DefaultHeaderView mDefaultHeaderView;

    public VRefreshLayout(Context context) {
        this(context, null);
    }

    public VRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultHeaderView();
        setChildrenDrawingOrderEnabled(true);
    }

    public void setDragRate(float dragRate) {
        mDragRate = dragRate;
    }


    public int getStatus() {
        return mStatus;
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        ratio = Math.max(ratio, 1.0f);
        this.ratioOfHeaderHeightToRefresh = ratio;
        this.ratioOfHeaderHeightToReach = Math.max(ratioOfHeaderHeightToRefresh, ratioOfHeaderHeightToReach);
        if (mHeaderView.getMeasuredHeight() > 0) {
            setRefreshDistance((int) (mHeaderView.getMeasuredHeight() * ratioOfHeaderHeightToRefresh));
            setMaxDragDistance((int) (mHeaderView.getMeasuredHeight() * ratioOfHeaderHeightToReach));
        }
    }

    public void setRatioOfHeaderHeightToReach(float ratio) {
        ratio = Math.max(Math.max(ratio, 1.0f), ratioOfHeaderHeightToRefresh);
        this.ratioOfHeaderHeightToReach = ratio;
        if (mHeaderView.getMeasuredHeight() > 0) {
            setMaxDragDistance((int) (mHeaderView.getMeasuredHeight() * ratioOfHeaderHeightToReach));
        }
    }

    public void setToStartDuration(int toStartDuration) {
        mToStartDuration = toStartDuration;
    }

    public void setToRetainDuration(int toRetainDuration) {
        mToRetainDuration = toRetainDuration;
    }

    public void setAutoRefreshDuration(int autoRefreshDuration) {
        mAutoRefreshDuration = autoRefreshDuration;
    }

    public void setCompleteStickDuration(int completeStickDuration) {
        mCompleteStickDuration = completeStickDuration;
    }

    private void setMaxDragDistance(int distance) {
        mMaxDragDistance = distance;
        mProgress.totalY = distance;
    }

    private void setRefreshDistance(int distance) {
        mRefreshDistance = distance;
        mProgress.refreshY = mRefreshDistance;
    }

    public boolean isDefaultHeaderView() {
        return mHeaderView == mDefaultHeaderView;
    }

    public View getDefaultHeaderView() {
        if (mDefaultHeaderView == null) {
            setDefaultHeaderView();
        }
        return mDefaultHeaderView;
    }

    private void setDefaultHeaderView() {
        mDefaultHeaderView = new DefaultHeaderView(getContext());
        mDefaultHeaderView.setPadding(0, dp2px(10), 0, dp2px(10));
        mDefaultHeaderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dp2px(64)));
        setHeaderView(mDefaultHeaderView);
    }
    boolean isLogMeasure = false;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isLogMeasure) {
            Log.d(TAG, "onMeasure: ");
        }
        ensureContent();
        //measure contentView
        if (mContentView != null) {
            int contentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            int contentHeight = getMeasuredHeight() - getPaddingTop() + getPaddingBottom();
            mContentView.measure(MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY));
        }

        //measure headerView
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            if (mIsInitMesure) {
                int measuredHeight = mHeaderView.getMeasuredHeight();
                mHeaderOrginTop = mHeaderCurrentTop = -measuredHeight;
                setMaxDragDistance((int) (measuredHeight * ratioOfHeaderHeightToReach));
                setRefreshDistance((int) (measuredHeight * ratioOfHeaderHeightToRefresh));
                mIsInitMesure = false;
            }
        }
        mHeaderLayoutIndex = -1;
        for (int i = 0; i < getChildCount(); i++) {
            if (mHeaderView == getChildAt(i)) {
                mHeaderLayoutIndex = i;
            }
        }
    }

    private void ensureContent() {
        if (mContentView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                if (childAt != mHeaderView) {
                    mContentView = childAt;
                    break;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (mContentView == null) {
            ensureContent();
        }

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        if (isLogMeasure) {
            Log.d(TAG, "onLayout: mHeaderCurrentTop" + mHeaderCurrentTop);
        }
        //layout headerView
        if (mHeaderView != null) {
            mHeaderView.layout(paddingLeft, mHeaderCurrentTop, paddingLeft + mHeaderView.getMeasuredWidth(), mHeaderCurrentTop + mHeaderView.getMeasuredHeight());
        }

        //layout contentView
        if (mContentView != null) {
            int distance = mHeaderCurrentTop - mHeaderOrginTop;
            int contentHeight = mContentView.getMeasuredHeight();
            int contentWidth = mContentView.getMeasuredWidth();
            int left = paddingLeft;
            int top = paddingTop + distance;
            int right = left + contentWidth;
            int bottom = top + contentHeight;
            mContentView.layout(left, top, right, bottom);
        }
        if (isLogMeasure) {
            Log.d(TAG, "onLayout: ");
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mHeaderLayoutIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            return mHeaderLayoutIndex;
        } else if (i >= mHeaderLayoutIndex) {
            return i + 1;
        } else {
            return i;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureContent();
        int action = MotionEventCompat.getActionMasked(ev);
        if (!isEnabled() || canChildScrollUp() || mIsRefreshing) {
            return false;
        }
        int pointerIndex;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if(isLogMeasure)
                Log.i(TAG, "-->onInterceptTouchEvent: ACTION_DOWN");
                mIsBeingDragged = false;
                notifyStatus(STATUS_INIT);
                mActivePointerId = ev.getPointerId(0);
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                if(isLogMeasure)
                Log.i(TAG, "-->onInterceptTouchEvent: ACTION_MOVE");
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                float evY = ev.getY(pointerIndex);
                checkDragging(evY);
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                if(isLogMeasure)
                Log.i(TAG, "-->onInterceptTouchEvent: ACTION_POINTER_UP");
                checkOtherPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(isLogMeasure)
                Log.i(TAG, "-->onInterceptTouchEvent: ACTION_OVER");
                mIsBeingDragged = false;
                mActivePointerId = -1;
                break;
        }


        return mIsBeingDragged;
    }

    private void checkDragging(float y) {
        float dy = y - mInitDownY;
        mInitMotionY = mInitDownY + mTouchSlop;
        if (dy > mTouchSlop && !mIsBeingDragged) {
            mIsBeingDragged = true;
        }
    }

    private void checkOtherPointerUp(MotionEvent ev) {
        int pointIndex = MotionEventCompat.getActionIndex(ev);
        int pointerId = ev.getPointerId(pointIndex);
        if (pointerId == mActivePointerId) {
            int newPointIndex = pointIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointIndex);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled() || canChildScrollUp() || mIsRefreshing) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if(isLogMeasure)
                Log.e(TAG, "-->onTouchEvent: ACTION_DOWN");
                mIsBeingDragged = false;
                notifyStatus(STATUS_INIT);
                mActivePointerId = ev.getPointerId(0);
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                if(isLogMeasure)
                Log.e(TAG, "-->onTouchEvent: ACTION_MOVE");
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                float evY = ev.getY(pointerIndex);
                checkDragging(evY);
                if (mIsBeingDragged) {
                    float dy = (evY - mInitMotionY) * mDragRate;
                    if (dy > 0) {
                        actionMoving(dy);
                    }
                    notifyStatus(STATUS_DRAGGING);
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if(isLogMeasure)
                Log.e(TAG, "-->onTouchEvent: ACTION_POINTER_DOWN");
                pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (pointerIndex < 0) {
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                if(isLogMeasure)
                Log.e(TAG, "-->onTouchEvent: ACTION_POINTER_UP");
                checkOtherPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
                if(isLogMeasure)
                Log.e(TAG, "-->onTouchEvent: ACTION_UP");
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                float upY = ev.getY(pointerIndex);
                if (mIsBeingDragged) {
                    float upDy = (upY - mInitMotionY) * mDragRate;
                    actionUp(upDy);
                    mIsBeingDragged = false;
                }
                mActivePointerId = -1;

                break;
            case MotionEvent.ACTION_CANCEL:
                if(isLogMeasure)
                Log.e(TAG, "-->onTouchEvent: ACTION_CANCEL");
                return false;
        }
        return true;

    }

    /**
     * 当手指拿起时Action_UP时
     * 根据下拉离开的距离来判断是取消操作还是要释放刷新
     * @param dy 手指Action_UP时，距离开始的位置的垂直间距
     */
    private void actionUp(float dy) {
        Log.d(TAG, "actionUp: " + dy);
        if (dy < mRefreshDistance) {
            //cancel
            animOffsetToStartPos();
            mIsRefreshing = false;
            notifyStatus(STATUS_RELEASE_CANCEL);
        } else {
            animOffsetToRetainPos();
            mIsRefreshing = true;
            notifyStatus(STATUS_RELEASE_PREPARE);
        }
    }

    private void moveAnimation(int star, int end, int duration, Animator.AnimatorListener animatorListener) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(star, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                moveTo(value);
            }
        });
        if (animatorListener != null) {
            valueAnimator.addListener(animatorListener);
        }
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    private void animOffsetToRetainPos() {
        final int from = mHeaderCurrentTop = mHeaderView.getTop();
        int to = getPaddingTop() + mRefreshDistance - mHeaderView.getMeasuredHeight();
        moveAnimation(from, to, mToRetainDuration, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                notifyRefreshListeners();
                notifyStatus(STATUS_REFRESHING);
            }
        });
    }

    private void animOffsetAutoRefresh() {
        mHeaderCurrentTop = mHeaderView.getTop();
        final int from = mHeaderCurrentTop;
        int to = mMaxDragDistance + mHeaderOrginTop;
        moveAnimation(from, to, mAutoRefreshDuration, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                notifyStatus(STATUS_RELEASE_PREPARE);
                animOffsetToRetainPos();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                notifyStatus(STATUS_DRAGGING);
            }
        });
    }

    /**
     * 动画移到初始的位置
     */
    private void animOffsetToStartPos() {
        final int from = mHeaderCurrentTop = mHeaderView.getTop();
        int to = mHeaderOrginTop;
        moveAnimation(from, to, mToStartDuration, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsRefreshing = false;
                notifyStatus(STATUS_INIT);
            }
        });
    }

    private void actionMoving(float y) {
        y = Math.min(y, mMaxDragDistance);
        if (y <= mMaxDragDistance) {
            int targetY = (int) (mHeaderOrginTop + y);
            moveTo(targetY);
        }
    }

    public void setHeaderView(View view) {
        if (view == null || view == mHeaderView) {
            return;
        }
        LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
        }
        removeView(mHeaderView);
        mHeaderView = view;
        mIsInitMesure = true;
        this.addView(mHeaderView);
        if (view instanceof UpdateHandler) {
            setUpdateHandler((UpdateHandler) view);
        }
    }

    public void setHeaderView(@LayoutRes int redId) {
        View view = LayoutInflater.from(getContext()).inflate(redId, this, false);
        setHeaderView(view);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * 本容器内的子视图是否能向上滑动(即下拉时[手指向下滑动]，mContentView是否能向下滑动)
     * @return
     */
    public boolean canChildScrollUp() {
//        if (android.os.Build.VERSION.SDK_INT < 14) {//android 4.0以下
//            if (mContentView instanceof AbsListView) {
//                final AbsListView absListView = (AbsListView) mContentView;
//                return absListView.getChildCount() > 0
//                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
//                        .getTop() < absListView.getPaddingTop());
//            }
//            else {
//                return ViewCompat.canScrollVertically(mContentView, -1) || mContentView.getScrollY() > 0;
//            }
//        } else {
//            return ViewCompat.canScrollVertically(mContentView, -1);
//        }
        return canChildViewScrollDown();
    }

    private boolean canChildViewScrollDown() {
        if (mContentView == null && canScrolableView == null) {
            return false;
        }
        if (canScrolableView != null) {
            boolean isCanScrolableViewCan = isTheViewCanScrollDown(canScrolableView);
//            CommonLog.e("info", TAG + "-->  isCanScrolableViewCan = " + isCanScrolableViewCan);
            return isCanScrolableViewCan;
        }

        return isTheViewCanScrollDown(mContentView);
    }

    private boolean isTheViewCanScrollDown(View canScrolableView) {
        if (canScrolableView != null) {
            if (android.os.Build.VERSION.SDK_INT < 14) {//android 4.0以下
                if (canScrolableView instanceof AbsListView) {
                    final AbsListView absListView = (AbsListView) canScrolableView;
                    return absListView.getChildCount() > 0
                            && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                            .getTop() < absListView.getPaddingTop());
                }
                else {
                    return ViewCompat.canScrollVertically(canScrolableView, -1) || canScrolableView.getScrollY() > 0;
                }
            } else {
                return ViewCompat.canScrollVertically(canScrolableView, -1);
            }
        }
        return false;
    }

    //    private boolean compatScroableViewInContentView() {
//        if (mContentView == null) {
//            return false;
//        }
//        findOutRecyclerView();
//    }
    public void refreshComplete() {
        if (mIsRefreshing) {
            notifyStatus(STATUS_COMPLETE);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    animOffsetToStartPos();
                }
            }, mCompleteStickDuration);
        }
    }

    public void autoRefresh() {
        if (!mIsRefreshing) {
            mIsRefreshing = true;
            animOffsetAutoRefresh();
        }
    }

    private void moveTo(int y) {
        int dy = y - mHeaderCurrentTop;
        ViewCompat.offsetTopAndBottom(mHeaderView, dy);
        //直接调用autoRefresh()时，这里mContentView 会空指针
        ViewCompat.offsetTopAndBottom(mContentView, dy);
        mHeaderCurrentTop = mHeaderView.getTop();
        mProgress.currentY = mHeaderCurrentTop - mHeaderOrginTop;
        notifyProgress();
    }

    private void notifyProgress() {
        if (mUpdateHandler != null) {
            mUpdateHandler.onProgressUpdate(this, mProgress, mStatus);
        }
    }

    private void notifyStatus(int status) {
        mStatus = status;
        if (mUpdateHandler != null) {
            mUpdateHandler.onProgressUpdate(this, mProgress, mStatus);
        }
    }

    public void addOnRefreshListener(OnRefreshListener onRefreshListener) {
        if (mOnRefreshListeners == null) {
            mOnRefreshListeners = new ArrayList<>();
        }
        mOnRefreshListeners.add(onRefreshListener);
    }

    private void notifyRefreshListeners() {
        if (mOnRefreshListeners == null || mOnRefreshListeners.isEmpty()) {
            return;
        }
        for (OnRefreshListener onRefreshListener : mOnRefreshListeners) {
            onRefreshListener.onRefresh();
        }
    }

    public void setUpdateHandler(UpdateHandler updateHandler) {
        mUpdateHandler = updateHandler;
    }

    public void setNestedCanScrolableView(View canScrolableView) {
        this.canScrolableView = canScrolableView;
    }

    public void setBelongTag(String meBelongTheTag) {
        this.belongTag = meBelongTheTag;
    }
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
//        CommonLog.d(TAG, belongTag + " : -->onWindowFocusChanged() hasWindowFocus= " + hasWindowFocus +" mStatus = " + theStateDesc(mStatus));
        if (!hasWindowFocus) {
            //失去焦点了,但是却仍然在拖动状态
            if (STATUS_DRAGGING == mStatus) {
                animOffsetToStartPos();
            }
        }
    }

    public interface OnRefreshListener {
        void onRefresh();
    }


    public interface UpdateHandler {
        void onProgressUpdate(VRefreshLayout layout, Progress progress, int status);
    }

    public static class Progress {
        private int totalY;
        private int currentY;
        private int refreshY;

        public int getRefreshY() {
            return refreshY;
        }

        public int getTotalY() {
            return totalY;
        }

        public int getCurrentY() {
            return currentY;
        }
    }


}
