package common.base.animators;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import common.base.utils.CommonLog;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/7/27<br>
 * Time: 13:27<br>
 * <P>DESC:
 * 带有可控制播放动画次数的 AnimationDrawable
 * 注意：有些版本的系统 会把oneShot默认设置为true,所以可能造成以为默认为false(看SDK源码是false),会一直动画，而不想只动画了一次
 * </p>
 * ******************(^_^)***********************
 */
public class AnimationDrawableWithPlayTimes extends AnimationDrawable {

    private static final String TAG = "AnimationDrawableWithPlayTimes";
    /**
     * 将要播放要次数
     * 一次代表 动画本身的所有帧播放完
     */
    protected int willPlayTimes;

    /**
     * 当前播放的帧序
     */
    private int curFrameIndex;
    /**
     * 也可以指定每帧 都相同的播放时长
     * 单位：毫秒
     */
    private int perFrameDurationInMillisecond;
    private int totalFrameCount;
    public AnimationDrawableWithPlayTimes withPerFrameDuration(int perFrameDurationInMillisecond) {
        this.perFrameDurationInMillisecond = perFrameDurationInMillisecond;
        return this;
    }

    /**
     * 控制本次动画播放次数
     * -1时为 一直播放
     * 1：播放一次
     * @param willPlayTimes != 1时 setOneShot(false);
     * @return self
     */
    public AnimationDrawableWithPlayTimes withPlayTimes(int willPlayTimes) {
        this.willPlayTimes = willPlayTimes;
        setOneShot(willPlayTimes == 1);
        return this;
    }
    /**
     * 添加动画的 Drawable 帧
     * @param context Context
     * @param drawableFramRes 可绘制帧资源
     * @param curFrameDurationInMillisecond 当前帧动画持续时长，单位：毫秒
     */
    public AnimationDrawableWithPlayTimes withDrawableFrameRes(Context context, @DrawableRes int drawableFramRes, int curFrameDurationInMillisecond) {
        if (context != null) {
            Drawable drawableFrame = context.getResources().getDrawable(drawableFramRes);
            if (drawableFrame != null) {
                addFrame(drawableFrame, curFrameDurationInMillisecond);
            }
        }
        return this;
    }
    public AnimationDrawableWithPlayTimes withDrawableFrameRes(Context context, @DrawableRes int drawableFramRes) {
        withDrawableFrameRes(context, drawableFramRes, this.perFrameDurationInMillisecond);
        return this;
    }
    public AnimationDrawableWithPlayTimes withDrawableFrameRes(Drawable drawableFrame, int curFrameDurationInMillisecond) {
        if (drawableFrame != null) {
            if (curFrameDurationInMillisecond == 0) {
                curFrameDurationInMillisecond = 1000;
            }
            addFrame(drawableFrame, curFrameDurationInMillisecond);
        }
        return this;
    }


    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        super.scheduleDrawable(who, what, when);
        CommonLog.i(TAG,"--> scheduleDrawable() ...");
    }

    @Override
    public void run() {
        super.run();
        //注意一下： isOneShot()有些版本的系统会默认为 true
//        CommonLog.i(TAG, "-->run() willPlayTimes = " + willPlayTimes + " isOneShot = " + isOneShot());
        if (willPlayTimes > 1) {
            curFrameIndex++;
            CommonLog.i(TAG, "--> run() curFrameIndex = " + curFrameIndex);
            int judegPlayTime = curFrameIndex / totalFrameCount;
            CommonLog.i(TAG, "--> run() judegPlayTime = " + judegPlayTime);
            if (judegPlayTime >= willPlayTimes) {
//                setVisible(true, true);
                stop();
            }
        }
    }

    private boolean isActiveSetOneShot = false;
    @Override
    public void setOneShot(boolean oneShot) {
        super.setOneShot(oneShot);
        isActiveSetOneShot = true;
    }

    @Override
    public void start() {
        totalFrameCount = getNumberOfFrames();
        curFrameIndex = 0;
        CommonLog.i(TAG, "--> start() curFrameIndex = " + curFrameIndex);
        if (!isActiveSetOneShot) {
            setOneShot(willPlayTimes == 1);//解决一些版本的系统默认oneShot为true的问题??
        }
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        CommonLog.i(TAG, "--> stop() curFrameIndex = " + curFrameIndex);
    }
}
