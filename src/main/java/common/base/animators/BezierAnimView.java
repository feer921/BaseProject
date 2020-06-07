package common.base.animators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import common.base.utils.CommonLog;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/12/25<br>
 * Time: 22:25<br>
 * <P>DESC:
 * 基于贝塞尔曲线 的动画视图
 * 本类功能为承载 进行贝塞尔曲线动画，类似舞台功能
 * </p>
 * ******************(^_^)***********************
 */
public class BezierAnimView extends FrameLayout {

    private static final String TAG = "BezierAnimView";
    /**
     * 本控件的宽
     */
    private int mWidth;
    /**
     * 本控件的高
     */
    private int mHeight;

    /**
     * 动画的图标资源宽
     */
    private int animIconWidth;

    /**
     * 动画的图标资源高
     */
    private int animIconHeight;

    /**
     * 承载动画的子View初始化添加进本容器View的布局参数
     */
    private LayoutParams flp4ChildView;

    /**
     * 下一个自动要进行动画的产生的间隔时间
     * 单位：毫秒
     */
    private long nextWillBuildAnimGapTime = 800;

    /**
     * 配置本类将承载自动循环再产生动画
     * def: false
     */
    private boolean isWillAutoBuildAnimView;

    /**
     * 当承载动画的View初始化 Show的时候是否使用本类提供的默认动画
     * def:true
     */
    private boolean isWillUseDefFirstShowAnimator = true;

    /**
     * 曲线动画总时间：
     * 单位：毫秒
     */
    private long beziaAnimatorDuration = 8000;

    /**
     * 单个动画View 首次添加进本容器View时的动画执行时间
     */
    private long durationOfAnimViewBorn = 500;

    /**
     * 提供一个画笔，来debug显示/绘制出，外部所提供的 贝塞尔曲线 的关键点
     */
    private Paint mPaint4ShowKeyPoints;

    private Paint mPaint4DrawText;
    /**
     * 默认画笔颜色
     * def:红色
     */
    private @ColorInt int thePaintColor = 0xffff0000;
    private boolean isNeedDebugDrawKeyPoings = false;

    /**
     * 贝塞尔曲线动画执行过程中是否需要回调 动画的状态
     * def: true
     */
    private boolean isNeedCallbackUpdateAnimState = true;
    /**
     * 将要动画的资源
     */
    private @DrawableRes int willAnimDrawableRes;

    public BezierAnimView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        flp4ChildView = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp4ChildView.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

//        Drawable loveHeartIcon = getResources().getDrawable(lovingHeartIconResIds[0]);

//        Drawable loveHeartIcon = getResources().getDrawable(willAnimDrawableRes);
//        iconW = loveHeartIcon.getIntrinsicWidth();
//        iconH = loveHeartIcon.getIntrinsicHeight();
    }
    private IBezierAnimControler animControler;
    private List<PointF> providedKeyPoints;

    /**
     * 在执行贝塞尔曲线动画时的View是否都遵循同一个路径
     * 如果遵循同一个路径 即表示，动画都是沿着一个相同的路径动画
     * def: true
     */
    private boolean isAllAnimViewSamePath = true;
//    /**
//     * 开始动画，支持开启重复自动动画
//     * 注：支持多次调用，每调用一次，就会产生一个动画体
//     * @param isWillRepeatAnim true: 自动重复 动画
//     */
//    public void play(boolean isWillRepeatAnim) {
//        buildAnimViewWithDrawableRes();
//
//        removeCallbacks(willRepeatBuildAnimTask);
//        if (isWillRepeatAnim) {
//        }
//    }
    private Runnable willRepeatBuildAnimTask = new Runnable() {
        @Override
        public void run() {
            buildAnimViewWithDrawableResAndStart();
            if (isWillAutoBuildAnimView) {
                postDelayed(this, nextWillBuildAnimGapTime);
            }
        }
    };

    /**
     * 设置将要动画的资源
     *
     * @param willAnimDrawableRes
     */
    public void setWillAnimDrawableRes(@DrawableRes int willAnimDrawableRes) {
        this.willAnimDrawableRes = willAnimDrawableRes;
        Drawable loveHeartIcon = getResources().getDrawable(willAnimDrawableRes);
        this.animIconWidth = loveHeartIcon.getIntrinsicWidth();
        this.animIconHeight = loveHeartIcon.getIntrinsicHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    /**
     * 开始动画
     * 注：支持多次调用，每调用一次，就会产生一个动画体
     */
    public void play() {
        buildAnimViewWithDrawableResAndStart();
        removeCallbacks(willRepeatBuildAnimTask);
        if (isWillAutoBuildAnimView) {
            postDelayed(willRepeatBuildAnimTask, nextWillBuildAnimGapTime);
        }
    }

    private void buildAnimViewWithDrawableResAndStart() {
        //构造出 动画的承载体View，并添加进本容器View
        IBezierAnimControler animControler = this.animControler;
        View theHoldAnimView = null;
        if (animControler != null) {
            theHoldAnimView = animControler.provideHoldAnimView(this);
//            if (theHoldAnimView instanceof ImageView) {
//                ((ImageView) theHoldAnimView).setImageResource(willAnimDrawableRes);
//            }
        }
        if (theHoldAnimView == null) {
            ImageView holdAnimView = new ImageView(getContext());
            holdAnimView.setImageResource(willAnimDrawableRes);
            theHoldAnimView = holdAnimView;
        }

        ViewGroup.LayoutParams vlp = theHoldAnimView.getLayoutParams();
        addView(theHoldAnimView, vlp != null ? vlp : flp4ChildView);

        //构造初始化 show出的动画，如果有的话
        Animator animatorOfAnimViewBorn = null;
        if (animControler != null) {
            animatorOfAnimViewBorn = animControler.provideFirstShowAnimator(this, theHoldAnimView);
        }

        if (animatorOfAnimViewBorn == null && isWillUseDefFirstShowAnimator) {
            animatorOfAnimViewBorn = buildDefBornViewAnimator(theHoldAnimView, durationOfAnimViewBorn);
        }
        //贝塞尔曲线动画
        ValueAnimator bezierAnimator = buildBezierAnimator(theHoldAnimView);

        //动画合集
        AnimatorSet wholeAnimatorSet = new AnimatorSet();
        AnimatorSet.Builder animsBuilder = wholeAnimatorSet.play(bezierAnimator);
        //是否有提供和贝塞尔曲线动画一起执行的动画
        Animator willPlayWithBezierAnim = null;
        if (animControler != null) {
            willPlayWithBezierAnim = animControler.provideAnimatorPlayWithBezierAnim(this, theHoldAnimView);
            if (willPlayWithBezierAnim != null) {
                //执行时间一定要和贝塞尔曲线动画 执行时间一致
                willPlayWithBezierAnim.setDuration(this.beziaAnimatorDuration);
                animsBuilder.with(willPlayWithBezierAnim);
            }
        }
        //是否要在初始展示的动画之后再执行贝塞尔曲线动画
        if (animatorOfAnimViewBorn != null) {
            animsBuilder.after(animatorOfAnimViewBorn);
        }
        wholeAnimatorSet.setTarget(theHoldAnimView);
        wholeAnimatorSet.start();
    }

    /**
     * 默认的 构造生出一个子View时的开始动画集
     * @param theBornChildView
     * @param bornAnimatorDuration
     * @return
     */
    private AnimatorSet buildDefBornViewAnimator(View theBornChildView, long bornAnimatorDuration) {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(theBornChildView, "alpha", 0.3f, 1.0f);

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(theBornChildView, "scaleX", 0.2f, 1.0f);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(theBornChildView, "scaleY", 0.2f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(bornAnimatorDuration);
        //以上动画集合作用在目标对象上
        animatorSet.setTarget(theBornChildView);

        return animatorSet;
    }

    public void clearAnimObjects() {
        removeAllViews();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllViews();
    }

    private ValueAnimator buildBezierAnimator(View theBornChildView) {
        List<PointF> keyPoints = null;
        //构建贝塞尔曲线
        if (animControler != null) {
            if (isAllAnimViewSamePath) {
                if (this.providedKeyPoints == null) {
                    providedKeyPoints = animControler.provideKeyPoints(this, animIconWidth, animIconHeight);
                    //
                    if (isNeedDebugDrawKeyPoings) {
                        if (mPaint4ShowKeyPoints == null) {
                            mPaint4ShowKeyPoints = new Paint(Paint.ANTI_ALIAS_FLAG);
                            mPaint4ShowKeyPoints.setColor(thePaintColor);
                            mPaint4ShowKeyPoints.setStyle(Paint.Style.FILL);
//                            mPaint4ShowKeyPoints.setStrokeWidth(10);

                        }
                        if (mPaint4DrawText == null) {
                            mPaint4DrawText = new Paint(Paint.ANTI_ALIAS_FLAG);
                            mPaint4DrawText.setColor(0xff000000);
                            mPaint4DrawText.setTextSize(22);
                        }

//                        invalidate();
                    }
                }
                keyPoints = providedKeyPoints;
            }
            else {
                keyPoints = animControler.provideKeyPoints(this,animIconWidth, animIconHeight);
            }
        }
        if (keyPoints == null) {//外部没有提供
            if (isAllAnimViewSamePath) {
                if (providedKeyPoints == null) {
                    providedKeyPoints = new ArrayList<>(4);
                    //使用默认的关键点
                    PointF pointF0 = getBezierPoint(0);
                    providedKeyPoints.add(pointF0);
                    PointF pointF1 = getBezierPoint(1);
                    providedKeyPoints.add(pointF1);

                    PointF pointF2 = getBezierPoint(2);
                    providedKeyPoints.add(pointF2);

                    PointF pointF3 = getBezierPoint(3);
                    providedKeyPoints.add(pointF3);
                }
                keyPoints = providedKeyPoints;
            }
            else {
                keyPoints = new ArrayList<>(4);
                //使用默认的关键点
                PointF pointF0 = getBezierPoint(0);
                keyPoints.add(pointF0);
                PointF pointF1 = getBezierPoint(1);
                keyPoints.add(pointF1);

                PointF pointF2 = getBezierPoint(2);
                keyPoints.add(pointF2);

                PointF pointF3 = getBezierPoint(3);
                keyPoints.add(pointF3);
            }
        }

        PointF thePoint1 = keyPoints.get(0);
        PointF thePoint2 = keyPoints.get(1);
        PointF thePoint3= keyPoints.get(2);
        PointF thePoint4 = keyPoints.get(3);
        ValueAnimator bezierAnimator = ValueAnimator.ofObject(new BezierPointValuer(thePoint2, thePoint3),
                thePoint1, thePoint4);

        WrappedAnimatorViewListener wrappedTarget = new WrappedAnimatorViewListener(theBornChildView);
        //设置动画开始、结束的监听
        bezierAnimator.addListener(wrappedTarget);
        //设置动画运行过程中的变化监听
        bezierAnimator.addUpdateListener(wrappedTarget);
        bezierAnimator.setTarget(theBornChildView);
        bezierAnimator.setDuration(beziaAnimatorDuration);
        return bezierAnimator;
    }

    /**
     * 这里提供默认实现
     * @param pointIndex 关键点的序号
     * @return PointF 关键点坐标
     */
    private PointF getBezierPoint(int pointIndex) {
        PointF thePoint = new PointF();
        Random random = new Random();
        thePoint.x = random.nextInt(mWidth);
        int halfMineH = mHeight / 2;
        int halfMineW = mWidth / 2;
        switch (pointIndex) {
            case 0://第一个点
                thePoint.x = mWidth / 2 - animIconWidth / 2;
                thePoint.y = mHeight - animIconHeight;
                break;
            case 1:
//                thePoint.x = random.nextInt(halfMineW);
//                thePoint.y = random.nextInt(halfMineH) + halfMineH;

                thePoint.x = halfMineW;
                thePoint.y = halfMineH;
                break;
            case 2:
//                thePoint.x = random.nextInt(halfMineW) + halfMineW;
                thePoint.y = random.nextInt(halfMineH);
                break;
            case 3://最后一个点
                thePoint.y = 0;
                break;
        }
        return thePoint;
    }

//    public void setBeziaAnimaControler(IBezierAnimControler animaControler) {
//        this.animControler = animaControler;
//    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isNeedDebugDrawKeyPoings) {
            List<PointF> theKeyPoints = this.providedKeyPoints;
            if (theKeyPoints != null && theKeyPoints.size() > 0) {
                int index = 1;
                for (PointF theKeyPoint : theKeyPoints) {
                    float x = theKeyPoint.x;
                    float y = theKeyPoint.y;
                    canvas.drawText(index + "", x + 10, y + 5, mPaint4DrawText);
                    canvas.drawCircle(x, y, 10, mPaint4ShowKeyPoints);
//                    canvas.drawPoint(x, y, mPaint4ShowKeyPoints);
                    index++;
                }
            }
        }
    }

    public void setFlp4ChildView(LayoutParams flp4ChildView) {
        this.flp4ChildView = flp4ChildView;
    }

    /**
     * 当需要本动画视图自动重复产生动画时，两次动画的间隔时间
     * @param nextWillBuildAnimGapTime 两次动画的间隔时间 时间毫秒
     */
    public void setNextWillBuildAnimGapTime(long nextWillBuildAnimGapTime) {
        this.nextWillBuildAnimGapTime = nextWillBuildAnimGapTime;
    }

    /**
     * 设置是否将要本动画视图自动、重复的产生动画
     * @param willAutoBuildAnimView 是否需要自动、重复产生动画
     */
    public void setWillAutoBuildAnimView(boolean willAutoBuildAnimView) {
        isWillAutoBuildAnimView = willAutoBuildAnimView;
    }

    /**
     * 如果外部动画控制器不提供 承载动画的View初始化展示时的动画，则是否设置使用本类提供的默认的动画
     * @param willUseDefFirstShowAnimator 是否使用本类默认的初始化动画
     */
    public void setWillUseDefFirstShowAnimator(boolean willUseDefFirstShowAnimator) {
        isWillUseDefFirstShowAnimator = willUseDefFirstShowAnimator;
    }

    /**
     * 设置贝塞尔曲线动画的执行时长
     * @param beziaAnimatorDuration 一次贝塞尔曲线动画的时长：单位：毫秒
     */
    public void setBeziaAnimatorDuration(long beziaAnimatorDuration) {
        this.beziaAnimatorDuration = beziaAnimatorDuration;
    }

    /**
     * 设置控制本动画视图的动画相关的控制器
     * @param animControler 控制器
     */
    public void setAnimControler(IBezierAnimControler animControler) {
        this.animControler = animControler;
    }

    /**
     * 设置是否每次动画 都是相同的运动路径
     * def:true
     * @param allAnimViewSamePath true:将来动画全部是相同的动画路径
     */
    public void setAllAnimViewSamePath(boolean allAnimViewSamePath) {
        isAllAnimViewSamePath = allAnimViewSamePath;
    }

    /**
     * 设置单个动画View 首次进入(出生)时的动画(如果有的话)的执行时间
     * @param durationOfAnimViewBorn 单位：毫秒
     */
    public void setDurationOfAnimViewBorn(long durationOfAnimViewBorn) {
        this.durationOfAnimViewBorn = durationOfAnimViewBorn;
    }
    private void onAnimStateChanged(View theAnimView, PointF curPointF, float curAnimatedFraction) {
        if (animControler != null) {
            animControler.onAnimStateUpdated(this,theAnimView,curPointF,curAnimatedFraction);
        }
    }

    public void setNeedDebugDrawKeyPoings(boolean isNeedDebugDrawKeyPoings) {
        this.isNeedDebugDrawKeyPoings = isNeedDebugDrawKeyPoings;
    }

    public void setNeedCallbackUpdateAnimState(boolean isNeedCallbackUpdateAnimState) {
        this.isNeedCallbackUpdateAnimState = isNeedCallbackUpdateAnimState;
    }
    public void setThePaintColor(@ColorInt int thePaintColor) {
        this.thePaintColor = thePaintColor;
    }
//    public interface IUpdateAnimStateListener{
//        void onAnimStateUpdated(boolean isAnimEnd,View theAnimView, PointF curPointF, float curAnimatedFraction);
//    }

    /**
     * 提供一个可以外部切入控制 整个贝塞尔曲线动画的接口
     */
    public interface IBezierAnimControler{
        /**
         * 甚至可以由外部来提供承载动画的View
         * @param theBezierAnimView 当前的动画视图(舞台)
         * @return 提供的承载动画的View；最佳实践为: 每次都是新对象
         */
        View provideHoldAnimView(BezierAnimView theBezierAnimView);
        /**
         * 外部提供 要精确控制本 贝塞尔曲线动画的关键点信息
         * @param theBezierAnimView  当前的动画视图(舞台)
         * @param animIconWidth 动画资源的宽，便于计算关键点坐标; 也可以不参考该参数
         * @param animIconHeight 动画资源的高、便于计算关键点坐标; 也可以不参考该参数
         * @return 关键点集合，目前本类仅支持最多4个点的关键点信息，List<0> = 初始坐标点；List<1> 整个曲线的中间控制点第1点--最关键；
         * List<2> 整个曲线的中间控制点第1点--也最关键；List<3>: 整个曲线的终点；
         */
        List<PointF> provideKeyPoints(BezierAnimView theBezierAnimView, int animIconWidth, int animIconHeight);

        /**
         * 可选项：提供一个当 承载动画的View开始显示出来时是否要有的动画
         * @param theBezierAnimView 当前的动画视图(舞台)
         * @param holdTheAnimView 承载动画的View
         * @return Animator
         */
        Animator provideFirstShowAnimator(BezierAnimView theBezierAnimView, @NonNull View holdTheAnimView);


        /**
         * 贝塞尔曲线动画 状态的回调
         * @param theBezierAnimView 当前的动画视图(舞台)
         * @param theAnimView 当前动画的Veiw
         * @param curPointF 当前的坐标
         * @param curAnimatedFraction 当前动画完成的系数： 1时为完成；
         */
        void onAnimStateUpdated(BezierAnimView theBezierAnimView, View theAnimView, PointF curPointF, float curAnimatedFraction);
        /**
         * 可选项：提供一个 和当贝塞尔曲线动画开始时一起/同时执行的动画
         * @param theBezierAnimView 当前的动画视图(舞台)
         * @param holdTheAnimView 承载动画的View
         * @return Animator
         */
        Animator provideAnimatorPlayWithBezierAnim(BezierAnimView theBezierAnimView, View holdTheAnimView);

    }

//    private IUpdateAnimStateListener animStateListener;
    private class WrappedAnimatorViewListener extends AnimatorListenerAdapter implements
            ValueAnimator
                    .AnimatorUpdateListener {
        private View theTargetChildView;

        public WrappedAnimatorViewListener(View theTargetChildView) {
            this.theTargetChildView = theTargetChildView;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            //动画结束后，把目标子View移除掉
            if (theTargetChildView != null) {
                ViewGroup targetViewParentView = (ViewGroup) theTargetChildView.getParent();
                if (targetViewParentView != null) {
                    targetViewParentView.removeView(theTargetChildView);
                }
            }
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //由贝塞尔曲线公式计算出的当前点值
            PointF curPointF = (PointF) animation.getAnimatedValue();
            //顺便设置一下透明度变化
            float curAnimatedFraction = animation.getAnimatedFraction();
            if (curPointF != null) {
                if (theTargetChildView != null) {
                    theTargetChildView.setX(curPointF.x);
                    theTargetChildView.setY(curPointF.y);
                    //顺便设置一下透明度变化
                    theTargetChildView.setAlpha(1 - curAnimatedFraction);
                }
            }
            CommonLog.d(TAG, "--> onAnimationUpdate() curPointF = " + curPointF);
            if (isNeedCallbackUpdateAnimState) {
                onAnimStateChanged(theTargetChildView, curPointF, curAnimatedFraction);
            }
        }
    }
}
