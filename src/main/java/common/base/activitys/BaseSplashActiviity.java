package common.base.activitys;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.TextView;
import common.base.utils.CommonLog;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-16
 * Time: 17:29
 * DESC: 闪屏基类,本来该类没有什么通用性,在此写一些简单的通用内容
 */
public abstract class BaseSplashActiviity extends BaseActivity implements Animation.AnimationListener{
    /**
     * 闪屏页显示广告时，提供倒计时并且可点击直接跳过的按钮
     */
    protected TextView tvBtnJumpOverSplash;
    /**
     * 闪屏动画
     */
    protected Animation splashAnimation;
    /**
     * 运行动画的视图
     */
    protected View toAnimView;
    /**
     * 闪屏(广告)、动画持续的时间
     * 毫秒为单位
     */
    protected int splashDuringTime;
    /**
     * 计时
     */
    private AdsTimeCountDown countDownTimer;
    /**
     * 倒计时次数，默认5次
     */
    private int countDownTimes = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //一般闪屏页是全屏展示
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }


    /**
     * 如果子类在getProvideContentViewResID()方法提供了视图资源，那么子类的初始化视图可在此方法中完成
     */
    @Override
    protected void initViews() {
        splashAnimation = getAnimation4BackgroudImsg();
        if (splashAnimation != null) {
            splashAnimation.setDuration(splashDuringTime);
            splashAnimation.setAnimationListener(this);
        }
        if (toAnimView != null) {
            toAnimView.setAnimation(splashAnimation);
        }
        if (tvBtnJumpOverSplash != null) {
            tvBtnJumpOverSplash.setOnClickListener(this);
        }
    }

    /**
     * 子类在重载本方法时调用一下super.onClick()
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        if (v == tvBtnJumpOverSplash) {//点击了直接跳过按钮
            if (splashAnimation != null && !splashAnimation.hasEnded()) {
                splashAnimation.setAnimationListener(null);//不让Animation通过 AnimationListener 在调用cancel()时仍然回调出onAnimationEnd()
                splashAnimation.cancel();
            }
            // 不需要对视图清除动画，不然界面会晃一下
//            if (toAnimView != null) {
//                toAnimView.clearAnimation();
//            }
            if (countDownTimer != null) {//倒计时取消
                countDownTimer.cancel();
            }
            jumpToTargetActivity();
        }
    }

    protected abstract void jumpToTargetActivity();

    /**
     * 本闪屏基类此方法为开始执行动画
     */
    @Override
    protected void initData() {
        if (tvBtnJumpOverSplash != null) {
            int tvBtnVisibility = tvBtnJumpOverSplash.getVisibility();
            if (tvBtnVisibility == View.VISIBLE) {
                countDownTimes = (splashDuringTime / 1000) + 1;//倒计时总比动画的时间多1秒
                countDownTimer = new AdsTimeCountDown((countDownTimes + 2) * 1000,1000);//之所以+2，因为如果设置为6，而倒计时为6,5,4,3,2; 少了1，0
            }
        }
        if (splashAnimation != null) {
            splashAnimation.startNow();
        }
    }

    /**
     * 本APP是否为第一次启动，一般来讲如果是第一次启动，在闪屏页过后，跳转进导航页(然后在导航页结束时置为false),但也可以不跳转到导航页，所以交由子类具体实现
     * @return
     */
    protected boolean isAppFirstStart() {
        return false;
    }

    protected Animation getAnimation4BackgroudImsg() {
        return null;
    }

    /**
     * <p>Notifies the start of the animation.</p>
     *
     * @param animation The started animation.
     */
    @Override
    public void onAnimationStart(Animation animation) {
        CommonLog.e("info",TAG  + " --> onAnimationStart() ");
        if (countDownTimer != null) {//如果是广告展示，动画开始时则开始倒计时
            countDownTimer.start();
        }
    }

    /**
     * <p>Notifies the end of the animation. This callback is not invoked
     * for animations with repeat count set to INFINITE.</p>
     *
     * @param animation The animation which reached its end.
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        CommonLog.e("info",TAG  + " --> onAnimationEnd() ");
        if (animation == splashAnimation) {
            if (countDownTimer == null) {//如果没有倒计时，则依据闪屏动画完成了来自动跳转，否则依据用户直接点击跳过或者倒计时完成
                //动画完成了，跳转到目标界面，子类区分实现
                jumpToTargetActivity();
            }
        }
    }

    /**
     * <p>Notifies the repetition of the animation.</p>
     *
     * @param animation The animation which was repeated.
     */
    @Override
    public void onAnimationRepeat(Animation animation) {

    }
    private class AdsTimeCountDown extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public AdsTimeCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * Callback fired on regular interval.
         *
         * @param millisUntilFinished The amount of time until finished.
         */
        @Override
        public void onTick(long millisUntilFinished) {
            int curCountDownTime = countDownTimes --;
            countDownTimes(curCountDownTime);
        }

        /**
         * Callback fired when the time is up.
         */
        @Override
        public void onFinish() {//计时结束
            onClick(tvBtnJumpOverSplash);
        }
    }

    /**
     * 倒计时
     * 注：该倒计时的初始值为闪屏动画持续时间加上1秒
     * eg.: 闪屏时间为 5000毫秒==5秒，则倒计时为6秒
     * @param decreaseSencond 当前倒计数(递减秒数)，从闪屏动画时间的秒数+1 ~ 0
     */
    protected abstract void countDownTimes(int decreaseSencond);
    @Override
    public void onBackPressed() {//即当闪屏页没有过滤完成前，用户想退出，则把一些会导致自动跳转的功能给取消，让用户退出程序
        if (splashAnimation != null && !splashAnimation.hasEnded()) {
            splashAnimation.setAnimationListener(null);
            splashAnimation.cancel();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onBackPressed();
    }
}
