package common.base.utils;

import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.view.Choreographer;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/11/14<br>
 * Time: 21:02<br>
 * <P>DESC:
 * <a>https://blog.csdn.net/u013263323/article/details/54917285</a>
 * </p>
 * ******************(^_^)***********************
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class UIFrameCounter implements Choreographer.FrameCallback {
    private IDoFrameCallback doFrameCallback;
//    /**
//     * 系统设备刷新(绘制)一次的时间间隔16.6ms
//     * 60次/秒
//     */
//    private static final float deviceRefreshRateMs = 16.67f;

    /**
     * 延时多少毫秒来请求回调
     */
    private long callBackDelayMillSeconds = 0;
    private long delayNanosecond = 0;
    public UIFrameCounter(IDoFrameCallback frameCallback) {
        this.doFrameCallback = frameCallback;
    }
    /**
     * 上一次绘制的时间(纳秒)
     */
    private long lastFrameTimeNanos;

//    private long curFrameTimeNanos;
    public void start() {
        Choreographer.getInstance().postFrameCallback(this);
    }

    public void start(long delayToCallbackMillisecond) {
        callBackDelayMillSeconds = delayToCallbackMillisecond;
        delayNanosecond = delayToCallbackMillisecond * 1000000;//1毫秒==1000000纳秒
        Choreographer.getInstance().postFrameCallbackDelayed(this, delayToCallbackMillisecond);
    }
    /**
     * Called when a new display frame is being rendered.
     * <p>
     * This method provides the time in nanoseconds when the frame started being rendered.
     * The frame time provides a stable time base for synchronizing animations
     * and drawing.  It should be used instead of {@link SystemClock#uptimeMillis()}
     * or {@link System#nanoTime()} for animations and drawing in the UI.  Using the frame
     * time helps to reduce inter-frame jitter because the frame time is fixed at the time
     * the frame was scheduled to start, regardless of when the animations or drawing
     * callback actually runs.  All callbacks that run as part of rendering a frame will
     * observe the same frame time so using the frame time also helps to synchronize effects
     * that are performed by different callbacks.
     * </p><p>
     * Please note that the framework already takes care to process animations and
     * drawing using the frame time as a stable time base.  Most applications should
     * not need to use the frame time information directly.
     * </p>
     *
     * @param frameTimeNanos 纳秒 The time in nanoseconds when the frame started being rendered,
     *                       in the {@link System#nanoTime()} timebase.  Divide this value by {@code 1000000}
     *                       to convert it to the {@link SystemClock#uptimeMillis()} time base.
     * 回调在主线程中
     */
    @Override
    public void doFrame(long frameTimeNanos) {
        if (lastFrameTimeNanos == 0) {
            lastFrameTimeNanos = frameTimeNanos;
            if (callBackDelayMillSeconds > 0) {
                lastFrameTimeNanos += delayNanosecond;
                Choreographer.getInstance().postFrameCallbackDelayed(this, callBackDelayMillSeconds);
            }
            else{
                Choreographer.getInstance().postFrameCallback(this);
            }
            return;
        }
        //两次绘制的时间间隔(单位:ms) 为什么会有<10Ms,<0的间隔时间呢？
        float gapTimeMillSeconds = (frameTimeNanos - lastFrameTimeNanos) / 1000000.0f;

        long gapMs = Math.round(gapTimeMillSeconds);
        //设备标准的每刷新一次的毫秒时间
//        long devRefreshPerMs = 17;

        int skipFrame = 0;
        if (gapMs > 17) {//两次绘制间隔大于了16.666Ms
            skipFrame = (int) (gapMs / 17);
//            float value = gapTimeMillSeconds / deviceRefreshRateMs;
//            skipFrame = (int) value;
        }
        //计算fps
        int curFps = 60;
//        if (gapMs > 0) {
//            long value = 1000 / gapMs;
//            curFps = (int) value;
//        }
        if (gapTimeMillSeconds > 8) {
            float framePerS = 1000.00f / gapTimeMillSeconds;
            curFps = Math.round(framePerS);
        }
        lastFrameTimeNanos = frameTimeNanos;
        if (doFrameCallback != null) {
            doFrameCallback.doFrameCallback(gapTimeMillSeconds, skipFrame, curFps);
        }
        if (callBackDelayMillSeconds > 0) {//因为delay请求回调了，所以此次的回调的纳秒时间要加上延迟的纳秒时间
            lastFrameTimeNanos += delayNanosecond;
            Choreographer.getInstance().postFrameCallbackDelayed(this,callBackDelayMillSeconds);
        }
        else{
            Choreographer.getInstance().postFrameCallback(this);
        }
    }


    public interface IDoFrameCallback {
        /**
         * 帧绘制回调
         * @param doFrameGapMs 两次帧绘制间隔毫秒时间
         * @param skipFrameCount 跳过的帧数
         * @param curFps 当前的Fps,当前每秒多少帧
         */
        void doFrameCallback(float doFrameGapMs, int skipFrameCount, int curFps);
    }

    public void workOver() {
        Choreographer.getInstance().removeFrameCallback(this);
        doFrameCallback = null;
    }
}
