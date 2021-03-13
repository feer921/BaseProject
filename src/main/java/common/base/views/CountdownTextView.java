package common.base.views;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import common.base.utils.CheckUtil;
import common.base.utils.CommonLog;
import common.base.utils.CountdownTimerImpl;
import common.base.utils.TimeUtil;
import common.base.utils.Util;

/**
 * *****************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2020/2/12<br>
 * Time: 10:11<br>
 * <P>DESC:
 *  主动倒计时(按相应间隔时间递减)TextView
 * </p>
 * ******************(^_^)***********************
 */
public class CountdownTextView extends CustomTextView implements CountdownTimerImpl.ICountdown{
    private final String TAG = "CountdownTextView";
    private CountdownTimerImpl countDownTimer;

    /**
     * 倒计时 前缀 文本
     * eg.: 剩余时间：
     */
    private CharSequence countdownPrefixWords;

    /**
     * 时、分、秒、背景颜色
     * 如果有的话，需要处理
     * def:0,透明，透明时不处理
     */
    private @ColorInt int hourMinuteSecondBgColor;

    /**
     * 时、分、秒、背景颜色 圆角的大小
     * def = 0;如果为0，则不使用圆角矩形背景
     */
    private int bgColorSpanRadiusPxValue;
    /**
     * 时、分、秒、背景颜色 SPAN
     * 如果有的话，需要处理
     */
    private RadiusCornerBackgroundColorSpan colorSpan4HourText;
    private RadiusCornerBackgroundColorSpan colorSpan4MinuteText;
    private RadiusCornerBackgroundColorSpan colorSpan4SecondText;
    /**
     * 总时间,单位：毫秒
     */
    private long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     * 单位：毫秒
     * 倒数间隔：即以多长的时间间隔倒数一次，eg.: 1000(即以1秒的时间间隔倒数一次)
     */
    private long mCountdownInterval;

    protected boolean isDetachFromWindow = true;

    private CountdownTimerImpl.ICountdown outsideCountdownListener;

    /**
     * 是否使用 Handler的方案来倒计时
     */
    private boolean isUseHandlerCase = false;

    private Runnable countDownTask = null;

    private long mLeftTimeMills = 0;
    public CountdownTextView(Context context) {
        this(context,null);
    }

    public CountdownTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //自定义属性

    }

    /**
     * 开始倒数
     * @param theMillisInFuture 总倒数的时间，单位：毫秒
     * @param theCountdownInterval 每次倒数时间间隔，单位：毫秒
     */
    public void start(long theMillisInFuture, long theCountdownInterval) {
        boolean willRestart = false;
        if (theCountdownInterval <= 0 || theCountdownInterval > theMillisInFuture) {//该条件即包含了
            // mMillisInFuture <= 0了
            //这些个条件时，都不能执行任务
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            if (this.mMillisInFuture != theMillisInFuture) {
                onFinish();
            }
            countDownTimer = null;
            if (countDownTask != null) {
                removeCallbacks(countDownTask);
            }
            return;
        }
        if (this.mMillisInFuture != theMillisInFuture || this.mCountdownInterval != theCountdownInterval) {
            willRestart = true;
        }
        this.mLeftTimeMills = theMillisInFuture;
        this.mMillisInFuture = theMillisInFuture;
        this.mCountdownInterval = theCountdownInterval;
        if (willRestart) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            countDownTimer = null;

            if (isUseHandlerCase) {
                if (countDownTask != null) {
                    removeCallbacks(countDownTask);
                }
                onTick(mMillisInFuture);
                if (countDownTask == null) {
                    countDownTask = new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 2021/2/28 ???
                            mLeftTimeMills -= mCountdownInterval;

                            if (mLeftTimeMills < mCountdownInterval) {
                                onFinish();
                            }
                            else {
                                onTick(mLeftTimeMills);
                                postDelayed(this, mCountdownInterval);
                            }
                        }
                    };
                }
                postDelayed(countDownTask, mCountdownInterval);
            }
        }
        if (countDownTimer == null && !isUseHandlerCase) {
            countDownTimer = new CountdownTimerImpl(mMillisInFuture, mCountdownInterval);
            countDownTimer.setCountdown(this);
            onTick(mMillisInFuture);
            countDownTimer.start();
        } else {
            //如果不 为 null(可能时间参数一致的情况下),不需要 再start()
        }
    }

    public void reset() {
        mMillisInFuture = 0;
    }
    @Override
    protected void onDetachedFromWindow() {
        isDetachFromWindow = true;
        super.onDetachedFromWindow();
        log("onDetachedFromWindow()");
        this.outsideCountdownListener = null;
        if (countDownTask != null) {
            removeCallbacks(countDownTask);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        isDetachFromWindow = false;
        super.onAttachedToWindow();
        log("onAttachedToWindow()");
    }

    /**
     * 倒数结束
     */
    @Override
    public void onFinish() {
        log("onFinish()");
        if (outsideCountdownListener != null) {
            outsideCountdownListener.onFinish();
        }
        showCountdownResultInfo(0);
    }

    /**
     * 一次倒数回调
     * @param leftMillisUntilFinished 倒数后剩下的毫秒数
     */
    @Override
    public void onTick(long leftMillisUntilFinished) {
//        log("onTick() leftMillisUntilFinished = " + leftMillisUntilFinished + " isDetachFromWindow = " + isDetachFromWindow);
        if (isDetachFromWindow && outsideCountdownListener == null) {
            return;
        }
        showCountdownResultInfo(leftMillisUntilFinished);
    }

    /**
     * 显示倒数一次后的信息
     * @param leftMillisUntilFinished 剩余的 时间，单位：毫秒
     */
    protected void showCountdownResultInfo(long leftMillisUntilFinished) {
        if (outsideCountdownListener != null) {
            outsideCountdownListener.onTick(leftMillisUntilFinished);
            return;
        }
        // TODO: 2020/2/13 ???是否要交给外部来决定怎么显示
        int[] hms = TimeUtil.convertATotalMillisTime2Hms(leftMillisUntilFinished);
        //格式化时、分、秒
        String formatHmsStr = Util.formatStr("%02d:%02d:%02d", hms[0], hms[1], hms[2]);

        String wholeShowText = formatHmsStr;
        if (!CheckUtil.isEmpty(countdownPrefixWords)) {
            wholeShowText = countdownPrefixWords + formatHmsStr;
        }
        if (hourMinuteSecondBgColor != 0) {
            //有背景颜色要处理
            int prefixWordsLen = 0;
            if (countdownPrefixWords != null) {
                prefixWordsLen = countdownPrefixWords.length();
            }
            int hourTextStartIndex = prefixWordsLen;
            int minuteTextStartIndex = hourTextStartIndex + 3;
            int secondTextStartIndex = minuteTextStartIndex + 3;
            SpannableString ss = new SpannableString(wholeShowText);
            ss.setSpan(colorSpan4HourText, hourTextStartIndex,
                    hourTextStartIndex + 2,
                    SpannableString.SPAN_INCLUSIVE_INCLUSIVE);

            ss.setSpan(colorSpan4MinuteText, minuteTextStartIndex,
                    minuteTextStartIndex + 2,
                    SpannableString.SPAN_INCLUSIVE_INCLUSIVE);

            ss.setSpan(colorSpan4SecondText, secondTextStartIndex,
                    secondTextStartIndex + 2,
                    SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
            setText(ss);
        }
        else {
            setText(wholeShowText);
        }

    }

    private void log(String msg) {
        CommonLog.d(TAG, "---> " + msg);
    }

    public CharSequence getCountdownPrefixWords() {
        return countdownPrefixWords;
    }

    public void setCountdownPrefixWords(CharSequence countdownPrefixWords) {
        this.countdownPrefixWords = countdownPrefixWords;
    }


    public void setHourMinuteSecondBgColor(String hourMinuteSecondBgColor) {
        int theBgColor = 0;
        if (!CheckUtil.isEmpty(hourMinuteSecondBgColor)) {
            try {
                theBgColor = Color.parseColor(hourMinuteSecondBgColor);
            } catch (Exception ignore) {
            }
        }
        if (theBgColor != this.hourMinuteSecondBgColor) {
            this.hourMinuteSecondBgColor = theBgColor;
            buildColorSpans();
        }
    }

    public void setHourMinuteSecondBgColor(@ColorInt int hourMinuteSecondBgColor) {
        if (this.hourMinuteSecondBgColor != hourMinuteSecondBgColor) {
            this.hourMinuteSecondBgColor = hourMinuteSecondBgColor;
            buildColorSpans();
        }
    }

    public void setColorBgCornerRadius(int radius) {
        this.bgColorSpanRadiusPxValue = radius;
    }
    private void buildColorSpans() {
        if (this.hourMinuteSecondBgColor != 0) {
            colorSpan4HourText = new RadiusCornerBackgroundColorSpan(hourMinuteSecondBgColor,bgColorSpanRadiusPxValue);
            colorSpan4MinuteText = new RadiusCornerBackgroundColorSpan(hourMinuteSecondBgColor,bgColorSpanRadiusPxValue);
            colorSpan4SecondText = new RadiusCornerBackgroundColorSpan(hourMinuteSecondBgColor,bgColorSpanRadiusPxValue);
        }
    }

    public void setOutsideCountdownListener(CountdownTimerImpl.ICountdown outsideCountdownListener) {
        this.outsideCountdownListener = outsideCountdownListener;
    }

    public void setUseHandlerCase(boolean isToUseHandlerCase) {
        this.isUseHandlerCase = isToUseHandlerCase;
    }
}
