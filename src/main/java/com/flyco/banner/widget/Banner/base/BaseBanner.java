package com.flyco.banner.widget.Banner.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.banner.widget.LoopViewPager.FixedSpeedScroller;
import com.flyco.banner.widget.LoopViewPager.LoopViewPager;
import com.flyco.banner.widget.ScrollableViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import common.base.R;
import common.base.utils.CommonLog;

/**
 * 注：默认BaseBanner中指示器布局需要居中显示，所以Title View不添加进来
 * @param <D> 数据源的数据类型
 * @param <I> 返回BaseBanner的子类自身，方便链式调用
 */
public abstract class BaseBanner<D, I extends BaseBanner<D, I>> extends RelativeLayout{
    /** 日志 */
    protected final String TAG = getClass().getSimpleName();
//    /** 单线程池定时任务 */
//    private ScheduledExecutorService scheduledTaskService;
    /** 上下文 */
    protected Context mContext;
    /** 设备密度 */
    protected DisplayMetrics mDisplayMetrics;
    /** ViewPager */
    protected ViewPager mViewPager;
    /** 数据源 */
    protected List<D> mDatas = new ArrayList<>();
    /** 当前position */
    protected int mCurrentPositon;
    /** 上一个position */
    protected int mLastPositon;
    /** 多久后开始滚动 */
    private long mDelay;
    /** 滚动间隔 */
    private long mPeriod;
    /** 是否开启了自动滚动 */
    private boolean mIsAutoScrollEnable;

    /**
     * 是否可循环滚动
     * 注：如果可以自动滚动则一定要能loop
     * 能loop但不一定要自动滚动
     */
    private boolean isLoopEnable;

    /**
     * 是否需要创建指示器点布局
     */
    private boolean isNeedCreateIndicators = true;

    /**
     * 当数据数量小于2个时，是否需要显示指示器？
     * def:true
     */
    private boolean isNeedShowIndicatorsLessThan2Datas = true;
    /** 是否正在自动滚动中 */
    private boolean mIsAutoScrolling;
    /** 滚动速度 */
    private int mScrollSpeed = 450;
    /** 切换动画 */
    private Class<? extends ViewPager.PageTransformer> mTransformerClass;

    /** 显示器(小点)的最顶层父容器 */
    private RelativeLayout mRlBottomBarParent;
    protected int mItemWidth;
    protected int mItemHeight;

    /** 显示器和标题的直接父容器 */
    private LinearLayout mLlBottomBar;
    /** 最后一条item是否显示背景条 */
    private boolean mIsBarShowWhenLast;

    /** 指示器的的直接容器 */
    private LinearLayout mLlIndicatorContainer;

    /** 标题 */
    private TextView mTvTitle;

    private static final int MSG_WHAT_PAUSE_SCROLL = 10;
    private static final int MSG_WHAT_LOOP_SCROLL = 11;
    /**
     * 是否经历了onDetachFromWindow
     */
    private boolean resumedOnDetachFromWindow;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            int msgWhat = msg.what;
            switch (msgWhat) {
                case MSG_WHAT_PAUSE_SCROLL:
                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                    break;
                case MSG_WHAT_LOOP_SCROLL:
                    scrollToNextItem(mCurrentPositon);
                    sendEmptyMessageDelayed(msgWhat,mPeriod  * 1000);
                    break;
            }
        }
    };
    public BaseBanner(Context context) {
        this(context, null, 0);//这样一调用会报空指针,需作屏蔽处理
    }

    public BaseBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    private LoopViewPager loopViewPager;
    private ViewPager commonViewPager;
    public BaseBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        mDisplayMetrics = context.getResources().getDisplayMetrics();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseBanner,0,defStyle);
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BaseBanner);
        float scale = ta.getFloat(R.styleable.BaseBanner_bb_scale, -1);

        isLoopEnable = ta.getBoolean(R.styleable.BaseBanner_bb_isLoopEnable, true);
        boolean isScrollable = ta.getBoolean(R.styleable.BaseBanner_bb_isScrollable, true);
        mDelay = ta.getInt(R.styleable.BaseBanner_bb_delay, 5);
        mPeriod = ta.getInt(R.styleable.BaseBanner_bb_period, 5);
        mIsAutoScrollEnable = ta.getBoolean(R.styleable.BaseBanner_bb_isAutoScrollEnable, true);
        int barColor = ta.getColor(R.styleable.BaseBanner_bb_barColor, Color.TRANSPARENT);
        mIsBarShowWhenLast = ta.getBoolean(R.styleable.BaseBanner_bb_isBarShowWhenLast, true);
        int indicatorGravity = ta.getInt(R.styleable.BaseBanner_bb_indicatorGravity, Gravity.CENTER);
        float barPaddingLeft = ta.getDimension(R.styleable.BaseBanner_bb_barPaddingLeft, dp2px(10));
        float barPaddingTop = ta.getDimension(R.styleable.BaseBanner_bb_barPaddingTop, dp2px(indicatorGravity == Gravity.CENTER ? 6 : 2));
        float barPaddingRight = ta.getDimension(R.styleable.BaseBanner_bb_barPaddingRight, dp2px(10));
        float barPaddingBottom = ta.getDimension(R.styleable.BaseBanner_bb_barPaddingBottom, dp2px(indicatorGravity == Gravity.CENTER ? 6 : 2));
        int textColor = ta.getColor(R.styleable.BaseBanner_bb_textColor, Color.parseColor("#ffffff"));
        float textSize = ta.getDimension(R.styleable.BaseBanner_bb_textSize, sp2px(12.5f));
        boolean isTitleShow = ta.getBoolean(R.styleable.BaseBanner_bb_isTitleShow, true);
        boolean isIndicatorShow = ta.getBoolean(R.styleable.BaseBanner_bb_isIndicatorShow, true);
        ta.recycle();
        String androidNameSpace = "http://schemas.android.com/apk/res/android";
        //get layout_height
        String width = "", height = "", toMatch_P = LayoutParams.MATCH_PARENT + "", toWrap_content = LayoutParams.WRAP_CONTENT + "", toFill_P = LayoutParams.FILL_PARENT + "";
        if(attrs != null){
            height = attrs.getAttributeValue(androidNameSpace, "layout_height");
            width = attrs.getAttributeValue(androidNameSpace, "layout_width");
        }
        if (toMatch_P.equals(width)|| toFill_P.equals(width)) {
            mItemWidth = LayoutParams.MATCH_PARENT;
        } else if (toWrap_content.equals(width)) {
            mItemWidth = LayoutParams.WRAP_CONTENT;
        }
        else{//表示本控件被设置了layout_width="xdp[xp]"，即设置了精确的宽
            TypedArray widthArray = context.obtainStyledAttributes(attrs,new int[]{android.R.attr.layout_width});
            mItemWidth = widthArray.getDimensionPixelSize(0, -1);
            if (mItemWidth == 0) {//这里是防止本控件在xml布局文件中的android:layout_width="0dp" android:weight="x";,这样以比重的形式定义时，得到的就是0了
                mItemWidth = LayoutParams.MATCH_PARENT;
            }
        }

        if (toMatch_P.equals(height) || toFill_P.equals(height)) {
            mItemHeight = LayoutParams.MATCH_PARENT;
        } else if (toWrap_content.equals(height)) {
            mItemHeight = LayoutParams.WRAP_CONTENT;
        }
        else {
            TypedArray heightArray = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.layout_height});
            mItemHeight = heightArray.getDimensionPixelSize(0, -1);
            if (mItemHeight == 0) {//这里是防止本控件在xml布局文件中的android:layout_height="0dp" android:weight="x";,这样以比重的形式定义时，得到的就是0了
                mItemHeight = LayoutParams.MATCH_PARENT;
            }
        }
        if (mItemWidth > 0 && scale > 0) {
            if (scale > 1) {
                scale = 1;
            }
            mItemHeight = (int) (mItemWidth * scale);
        }
        CommonLog.e("info", TAG + "  ----------------mItemHeight = ----- " + mItemHeight + " mItemWidth = " + mItemWidth);
        //create ViewPager
//        mViewPager = isLoopEnable ? new LoopViewPager(context) : new ViewPager(context);
        //added by fee 2017-04-29: 由于需要随时切换两种ViewPager，而分别构造出来
        loopViewPager = new LoopViewPager(context);
//        commonViewPager = new ViewPager(context);
        commonViewPager = new ScrollableViewPager(context);
        mViewPager = isLoopEnable ? loopViewPager : commonViewPager;
        LayoutParams lp = new LayoutParams(mItemWidth, mItemHeight);
        addView(mViewPager, lp);

        //top parent of indicators
        mRlBottomBarParent = new RelativeLayout(context);
        addView(mRlBottomBarParent, lp);

        //container of indicators and title
        mLlBottomBar = new LinearLayout(context);
        //mLlBottomBar 的布局参数，宽匹配父控件宽，高自适应
        LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        mRlBottomBarParent.addView(mLlBottomBar, lp2);

        mLlBottomBar.setBackgroundColor(barColor);
        mLlBottomBar.setPadding((int) barPaddingLeft, (int) barPaddingTop, (int) barPaddingRight, (int) barPaddingBottom);
        mLlBottomBar.setClipChildren(false);
        mLlBottomBar.setClipToPadding(false);

        //container of indicators
        mLlIndicatorContainer = new LinearLayout(context);
        mLlIndicatorContainer.setGravity(Gravity.CENTER);
        mLlIndicatorContainer.setVisibility(isIndicatorShow ? VISIBLE : INVISIBLE);
        mLlIndicatorContainer.setClipChildren(false);
        mLlIndicatorContainer.setClipToPadding(false);

        // title
        mTvTitle = new TextView(context);
        mTvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0F));
        mTvTitle.setSingleLine(true);
        mTvTitle.setTextColor(textColor);
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mTvTitle.setVisibility(isTitleShow ? VISIBLE : INVISIBLE);
        //如果如果页面指示器需要居中显示，则只添加指示器的布局，不添加Title 的View
        if (indicatorGravity == Gravity.CENTER) {
            mLlBottomBar.setGravity(Gravity.CENTER);
            mLlBottomBar.addView(mLlIndicatorContainer);
        }
        else {
            if (indicatorGravity == Gravity.RIGHT) {//指示器布局要放在右边
                mLlBottomBar.setGravity(Gravity.CENTER_VERTICAL);
                mLlBottomBar.addView(mTvTitle);
                mLlBottomBar.addView(mLlIndicatorContainer);

                mTvTitle.setPadding(0, 0, dp2px(7), 0);
                mTvTitle.setEllipsize(TextUtils.TruncateAt.END);
                mTvTitle.setGravity(Gravity.START);
            }
            else if (indicatorGravity == Gravity.LEFT) {//指示器布局要放在左边
                mLlBottomBar.setGravity(Gravity.CENTER_VERTICAL);
                mLlBottomBar.addView(mLlIndicatorContainer);
                mLlBottomBar.addView(mTvTitle);

                mTvTitle.setPadding(dp2px(7), 0, 0, 0);
                mTvTitle.setEllipsize(TextUtils.TruncateAt.END);
                mTvTitle.setGravity(Gravity.END);
            }
        }
    }

    /** 创建ViewPager的Item布局 */
    public abstract View onCreateItemView(int position);

    /** 创建显示器 */
    public abstract View onCreateIndicator();

    /** 设置当前显示器的状态,选中或者未选中 */
    public abstract void setCurrentIndicator(int position);

    /** 覆写这个方法设置标题 */
    public void onTitleSlect(TextView tv, int position) {
    }

    /** 设置数据源 */
    public I setSource(List<D> list) {
        this.mDatas = list;
        return self();
    }

    //added by fee 2017-04-06
    public I addItemData(D itemData){
        this.mDatas.add(itemData);
        return self();
    }
    public D getItemData(int itemPos) {
        if (mDatas == null || itemPos < 0) {
            return null;
        }
        return mDatas.get(itemPos);
    }

    public int getDataCountInBanner() {
        return mDatas == null ? 0 : mDatas.size();
    }
    /** 滚动延时,默认5秒 */
    public I setDelay(long delay) {
        this.mDelay = delay;
        return self();
    }

    /** 滚动间隔,默认5秒
     * 注：单位秒
     * */
    public I setAutoScrollPeriod(long periodSeconds) {
        this.mPeriod = periodSeconds;
        setAutoScrollEnable(periodSeconds > 0);
        return self();
    }

    /** 设置是否支持自动滚动,默认true.仅对LoopViewPager有效 */
    public I setAutoScrollEnable(boolean isAutoScrollEnable) {
        this.mIsAutoScrollEnable = isAutoScrollEnable;
        if (isAutoScrollEnable) {
            isLoopEnable = true;
        }
        //add by fee 2017-04-29:主动调用这个停止自动滚动时，如果之前已经在自动滚动了，则先停止
        if (!mIsAutoScrollEnable && mIsAutoScrolling) {
            pauseScroll();
        }
        return self();
    }

    /**
     * 设置是否可以循环滑动
     * 注：showBanner前提前设置
     * @param loopEnable
     * @return
     */
    public I setLoopEnable(boolean loopEnable) {
        if (loopEnable != isLoopEnable) {
        }
        this.isLoopEnable = loopEnable;
        return self();
    }

    protected I self() {
        return (I) this;
    }
    /**
     * 设置ViewPager是否可以手动滑动
     * 注：一般是不需要该功能
     * @param scrollable
     * @return
     */
    public I setViewPagerScrollable(boolean scrollable) {
        if (mViewPager != null) {
            if (mViewPager instanceof ScrollableViewPager) {
                ((ScrollableViewPager) mViewPager).setScrollable(scrollable);
            }
            else if (mViewPager instanceof LoopViewPager) {
                ((LoopViewPager) mViewPager).setScrollable(scrollable);
            }
        }
        return self();
    }
    /** 设置页面切换动画 */
    public I setTransformerClass(Class<? extends ViewPager.PageTransformer> transformerClass) {
        this.mTransformerClass = transformerClass;
        return self();
    }

    /** 设置底部背景条颜色,默认透明 */
    public I setBarColor(int barColor) {
        mLlBottomBar.setBackgroundColor(barColor);
        return self();
    }

    /** 设置最后一条item是否显示背景条,默认true */
    public I setBarShowWhenLast(boolean isBarShowWhenLast) {
        this.mIsBarShowWhenLast = isBarShowWhenLast;
        return self();
    }

    /** 设置底部背景条padding,单位dp */
    public I barPadding(float left, float top, float right, float bottom) {
        mLlBottomBar.setPadding(dp2px(left), dp2px(top), dp2px(right), dp2px(bottom));
        return self();
    }

    /** 设置标题文字颜色,默认"#ffffff" */
    public I setTextColor(int textColor) {
        mTvTitle.setTextColor(textColor);
        return self();
    }

    /** 设置标题文字大小,单位sp,默认14sp */
    public I setTextSize(float textSize) {
        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        return self();
    }

    /** 设置是否显示标题,默认true */
    public I setTitleShow(boolean isTitleShow) {
        mTvTitle.setVisibility(isTitleShow ? VISIBLE : INVISIBLE);
        return self();
    }

    /** 设置是否显示显示器,默认true */
    public I setIndicatorShow(boolean isIndicatorShow) {
        mLlIndicatorContainer.setVisibility(isIndicatorShow ? VISIBLE : INVISIBLE);
        return self();
    }

    public I setShowIndicatorsWhenLessThan2Datas(boolean isNeedShowIndicatorsLessThan2Datas) {
        this.isNeedShowIndicatorsLessThan2Datas = isNeedShowIndicatorsLessThan2Datas;
        return self();
    }

    public I setNeedCreateIndicatorViews(boolean isNeedCreateIndicators) {
        this.isNeedCreateIndicators = isNeedCreateIndicators;
        return self();
    }
    /** 滚动到下一个item */
    public void scrollToNextItem(int position) {
        position++;
        mViewPager.setCurrentItem(position);
    }
    private InnerBannerAdapter dataAdapter;

    private void resetViewPager(boolean canLoopScroll) {
        CommonLog.e(TAG, "-->resetViewPager() canLoopScroll = " + canLoopScroll);
        if (mViewPager == null) {
            return;
        }
        LayoutParams lp = (LayoutParams) mViewPager.getLayoutParams();
        if (canLoopScroll) {
            if(mViewPager != loopViewPager){
                Log.e("info", TAG + "--> CommonViewPager ==> loopViewPager");
                removeView(mViewPager);
                mViewPager = loopViewPager;
                addView(mViewPager,0,lp);
            }
        }
        else{//不能循环翻页
            if (mViewPager != commonViewPager) {
                Log.e("info", TAG + "--> loopViewPager ==> CommonViewPager");
                removeView(mViewPager);
                mViewPager = commonViewPager;
                addView(mViewPager,0,lp);
            }
        }
    }
    /** 设置viewpager */
    private void setViewPager() {
        // added by fee 2017-04-29:在这里来根据当前的mDatas的数量进行重置mViewPager为可循环的还是不能循环的
        if (mDatas.size() < 2) {//只有一个Banner数据的情况下，不需要可以自动滚动以及手动可循环切换页面了
            setIndicatorShow(isNeedShowIndicatorsLessThan2Datas);
            resetViewPager(false);
        }
        else{//mDatas 数量 >= 2 需要自动滚动时才去切换成LoopViewPager
            //modified by fee 2019-11-05: 当需要自动滚动或者主动设置为可循环 即重置ViewPager类型
            setIndicatorShow(true);
            resetViewPager(mIsAutoScrollEnable || isLoopEnable);
//            if (mIsAutoScrollEnable) {
//                resetViewPager(true);
//            }
        }

        if (dataAdapter == null) {
            dataAdapter = new InnerBannerAdapter();
        }
        else{
            dataAdapter.notifyDataSetChanged();
        }
        mViewPager.setAdapter(dataAdapter);
        mViewPager.setOffscreenPageLimit(mDatas.size());
        try {
            if (mTransformerClass != null) {
                mViewPager.setPageTransformer(true, mTransformerClass.newInstance());
                if (isLoopViewPager()) {
                    mScrollSpeed = 550;
                    setScrollSpeed();
                }
            } else {
                if (isLoopViewPager()) {
                    mScrollSpeed = 450;
                    setScrollSpeed();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mInternalPageListener != null) {
            mViewPager.removeOnPageChangeListener(mInternalPageListener);
        }
        mViewPager.addOnPageChangeListener(mInternalPageListener);
    }

    private ViewPager.OnPageChangeListener mInternalPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            mCurrentPositon = position % mDatas.size();
            if (isNeedCreateIndicators) {
                setCurrentIndicator(mCurrentPositon);
            }
            onTitleSlect(mTvTitle, mCurrentPositon);
            mLlBottomBar.setVisibility(mCurrentPositon == mDatas.size() - 1 && !mIsBarShowWhenLast ? GONE : VISIBLE);

            mLastPositon = mCurrentPositon;
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    public void showBanner() {
        pauseScroll();
        setUpVIews();
        goOnScroll();
    }
    public void setUpVIews() {
        if (mDatas == null) {
            throw new IllegalStateException("Data source is empty,you must setSource() before startScroll()");
        }
        int dataSize = mDatas.size();
        if (dataSize > 0 && mCurrentPositon > dataSize - 1) {
            mCurrentPositon = 0;
        }
        onTitleSlect(mTvTitle, mCurrentPositon);
        setViewPager();
        //create indicator
        if (isNeedCreateIndicators) {
            View indicatorViews = onCreateIndicator();
            if (indicatorViews != null) {
                mLlIndicatorContainer.removeAllViews();
                mLlIndicatorContainer.addView(indicatorViews);
            }
        }
    }
    /** 继续滚动(for LoopViewPager) */
    public void goOnScroll() {
        if (!isValid()) {
            return;
        }
        if (mIsAutoScrolling) {//正在自动滚动
            return;
        }
        if (isLoopViewPager() && mIsAutoScrollEnable) {//
            pauseScroll();
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(MSG_WHAT_LOOP_SCROLL,mDelay * 1000);
            }
            mIsAutoScrolling = true;
            Log.i(TAG, "--->goOnScroll()");
        }
        else {
            mIsAutoScrolling = false;
        }
    }

    /** 停止滚动(for LoopViewPager) */
    public void pauseScroll() {
        Log.d(TAG,"--->pauseScroll()");
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mIsAutoScrolling = false;
    }

    /** 获取ViewPager对象 */
    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pauseScroll();
                break;
            case MotionEvent.ACTION_UP:
                goOnScroll();
                break;
            case MotionEvent.ACTION_CANCEL:
                goOnScroll();
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

//    @Override
//    protected void onWindowVisibilityChanged(int visibility) {
//        super.onWindowVisibilityChanged(visibility);
//        if (mIsSmart) {
//            if (visibility != VISIBLE) {
//                pauseScroll();
//            } else {
//                goOnScroll();
//            }
//        }
//    }

    private class InnerBannerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = onCreateItemView(position);
            if (itemView != null) {
                itemView.setOnClickListener(new PositonClickListener(position));
                container.addView(itemView);
            }
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    /** 设置滚动速率 */
    private void setScrollSpeed() {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
            FixedSpeedScroller myScroller = new FixedSpeedScroller(mContext, interpolator, mScrollSpeed);
            mScroller.set(mViewPager, myScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int dp2px(float dp) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    private float sp2px(float sp) {
        final float scale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    /**
     * mViewPager instanceof LoopViewPager
     * return true
     * @return
     */
    protected boolean isLoopViewPager() {
        return mViewPager instanceof LoopViewPager;
    }

    @Override
    protected void onDetachedFromWindow() {
        resumedOnDetachFromWindow = true;
        super.onDetachedFromWindow();
        Log.e(TAG ,"--> onDetachedFromWindow()");
//        if (mHandler != null) {
//            mHandler.removeCallbacksAndMessages(null);
//        }

        //modified by fee 2017-10-10 如果把本控件放在RecyclerView的头部、脚部位置，当滑动列表时，本
        //控件在离开可见区域时，会回调该方法，如果置空mHandler，其他使用到mHandler的地方会报NPE(空指针异常)
        //在窗口中看不见了，则暂停自动滚动功能
        //        mHandler = null;
        pauseScroll();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.e(TAG ,"--> onAttachedToWindow()");
        if (resumedOnDetachFromWindow) {
            goOnScroll();
        }
        resumedOnDetachFromWindow = false;
    }

    /**
     * 这里只判断了mViewPager是否为null以及mDatas是否为Null
     * @return
     */
    protected boolean isValid() {
        if (mViewPager == null) {
            Log.e(TAG, "ViewPager is not exist!");
            return false;
        }

        if (mDatas == null || mDatas.size() < 2) {//modified by fee 2017-04-29 change "mDatas.size()==0" to mDatas.size() < 2
            Log.e(TAG, "due to mDatas == null or data size less than 2,so can't to auto scroll...");
            return false;
        }
        return true;
    }

    //listener
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }
    private OnItemClickListener<D> onItemClickListener;

    public interface OnItemClickListener<D>{
        void onBannerItemClick(View theClickView,D itemData,int clickPosition);
    }

    public void setOnBannerItemClickListener(OnItemClickListener<D> l) {
        this.onItemClickListener = l;
    }

    protected PositonClickListener makeItemClickListener(int itemPosition) {
        return new PositonClickListener(itemPosition);
    }
    private class PositonClickListener implements OnClickListener{
       int curClickPosition;
       PositonClickListener(int curPos) {
            this.curClickPosition = curPos;
       }
       /**
        * Called when a view has been clicked.
        *
        * @param v The view that was clicked.
        */
       @Override
       public void onClick(View v) {
           if (onItemClickListener != null) {
               onItemClickListener.onBannerItemClick(v,getItemData(curClickPosition),curClickPosition);
           }
       }
    }
}
