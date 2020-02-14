package common.base.utils;

import android.os.CountDownTimer;

/**
 * *****************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2020/2/12<br>
 * Time: 15:24<br>
 * <P>DESC:
 *  倒数实现者
 * </p>
 * ******************(^_^)***********************
 */
public class CountdownTimerImpl extends CountDownTimer {
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountdownTimerImpl(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    private ICountdown countdown;

    public void setCountdown(ICountdown countdown) {
        this.countdown = countdown;
    }
    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    @Override
    public void onTick(long millisUntilFinished) {
        if (countdown != null) {
            countdown.onTick(millisUntilFinished);
        }
    }

    /**
     * Callback fired when the time is up.
     */
    @Override
    public void onFinish() {
        if (countdown != null) {
            countdown.onFinish();
        }
    }

    public interface ICountdown{
        void onFinish();

        /**
         * 一次倒数回调
         * @param leftMillisUntilFinished 倒数后剩下的毫秒数
         */
        void onTick(long leftMillisUntilFinished);
    }
}
