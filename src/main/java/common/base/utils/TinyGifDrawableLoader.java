package common.base.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.graphics.drawable.Animatable2Compat;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import common.base.WeakHandler;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/1/28<br>
 * Time: 17:17<br>
 * <P>DESC:
 * 简小的Gif资源加载
 * 包括控制播放次数(默认为循环播放)
 * 重新播放
 * 回调播放完成
 * [对于非循环播放有意义]自定义播放总时长(如果本类默认计算不准确的话)
 * </p>
 * ******************(^_^)***********************
 */
public class TinyGifDrawableLoader extends Animatable2Compat.AnimationCallback implements RequestListener<GifDrawable>,WeakHandler.Handleable{
    private final String TAG = "TinyGifDrawableLoader";
    /**
     * 播放次数
     */
    protected int playTimes;

    /**
     * Glide加载出来的gif drawable
     */
    protected GifDrawable curGifDrawable;
    /**
     * gif 播放的总时长
     * 表示外部可以指定要播放gif动画多长的时间
     * 注：根据实测不一定准确
     * 单位：毫秒
     */
    private int playTotalDuration = 0;
    /**
     * 是否需要使用指定的播放时长来主观判断是否播放完成
     * def:false;
     */
    private boolean isToJudgePlayEndWithPlayDuration;
    private WeakReference<ImageView> theDisPlayImageView;
    /**
     * 是否将自动隐藏ImageView控件当gif动画播放完成
     * def: false;
     */
    private boolean isWillHideViewWhenPlayEnd;
    public void loadGifDrawable(Context context, @RawRes @DrawableRes int gifDrawableResId, ImageView iv) {
        loadGifDrawable(context, gifDrawableResId, iv, playTimes);
    }

    public void loadGifDrawable(Context context, @RawRes @DrawableRes int gifDrawableResId, ImageView iv, int playTimes) {
        if (context == null || iv == null) {
            return;
        }
        iv.setVisibility(View.VISIBLE);
        theDisPlayImageView = new WeakReference<>(iv);//added by fee 2019-07-08: 将当前要显示的ImageView控件引用起来，但不适用本类用于给不同的ImageView加载
        this.playTimes = playTimes;
        //注：如果不是gif资源，则在asGif()时会抛异常
        RequestBuilder<GifDrawable> requestBuilder =
                Glide.with(context.getApplicationContext())
                        .asGif()
//                        .load(gifDrawableResId)
                ;
        if (
                playTimes >= 1 ||
                loadCallback != null) {//指定了播放次数，则需要监听动画执行的结束
            requestBuilder.listener(this)
            ;
        }
        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        requestBuilder.apply(options)
//        listener(this)
                .load(gifDrawableResId)
                .into(iv)
        ;
    }

    /**
     * 加载不一定是本地图片资源
     * (即也可以是网络资源)
     * 并且也可以不是gif图片
     * @param context
     * @param model
     * @param iv
     * @param playTimes
     */
    public void loadMaybeGifDrawable(Context context,Object model,ImageView iv,int playTimes) {
        if (context == null || iv == null) {
            return;
        }
        iv.setVisibility(View.VISIBLE);
        theDisPlayImageView = new WeakReference<>(iv);
        this.playTimes = playTimes;
        RequestBuilder builder = Glide.with(context.getApplicationContext())
                .asGif()
                ;
        if (loadCallback != null || playTimes >=1) {//指定了播放次数，则需要监听动画执行的结束
            builder.listener(this);
        }
        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        builder.apply(options)
                .load(model)
                .into(iv)
        ;
    }
    private WeakHandler mHandler;
    public void startPlay(boolean rePlay) {
        if (playTotalDuration > 0 && isToJudgePlayEndWithPlayDuration) {
            if (mHandler == null) {
                mHandler = new WeakHandler<>(this);
            }
            mHandler.removeMessages(WHAT_NOTIFY_PLAY_FINISH);
            mHandler.sendEmptyMessageDelayed(WHAT_NOTIFY_PLAY_FINISH, playTotalDuration);
        }
        if (rePlay) {
            if (curGifDrawable != null && !curGifDrawable.isRunning()) {
                curGifDrawable.startFromFirstFrame();
            }
        }
    }
    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
        CommonLog.e(TAG, "-->onLoadFailed() occur: " + e);
        String exceptionInfo = "load fail";
        if (e != null) {
            exceptionInfo = e.getMessage();
        }
        loadCallback(false, false,null, 0, exceptionInfo);
        return false;
    }

    @Override
    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
        GifDrawable loadedDrawable = resource;
        if (loadedDrawable != null) {
            if (loadCallback != null || playTimes >= 1) {//指定了播放次数，则需要监听动画执行的结束
                loadedDrawable.registerAnimationCallback(this);
            }
            //默认是循环播放的
            if (playTimes >= 1) {
                loadedDrawable.setLoopCount(playTimes);
                if (isToJudgePlayEndWithPlayDuration) {
                    if (playTotalDuration == 0) {
                        //计算出播放的总时长
                        try {
                            Field gifStateField = GifDrawable.class.getDeclaredField("state");
                            gifStateField.setAccessible(true);
                            Class gifStateClass = Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable$GifState");
                            Field gifFrameLoaderField = gifStateClass.getDeclaredField("frameLoader");
                            gifFrameLoaderField.setAccessible(true);
                            Class gifFrameLoaderClass = Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader");
                            Field gifDecoderField = gifFrameLoaderClass.getDeclaredField("gifDecoder");
                            gifDecoderField.setAccessible(true);
                            Class gifDecoderClass = Class.forName("com.bumptech.glide.gifdecoder.GifDecoder");
                            Object gifDecoder = gifDecoderField.get(gifFrameLoaderField.get(gifStateField.get(loadedDrawable)));
                            Method getDelayMethod = gifDecoderClass.getDeclaredMethod("getDelay", int.class);
                            getDelayMethod.setAccessible(true);
                            int frameCount = loadedDrawable.getFrameCount();
                            int framePlayDelay = 0;
                            for (int frame = 0; frame < frameCount; frame++) {
                                framePlayDelay += (int) getDelayMethod.invoke(gifDecoder, frame);
                            }
                            playTotalDuration = this.playTimes * framePlayDelay;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                //计时
                if (playTotalDuration > 0 && isToJudgePlayEndWithPlayDuration) {
                    startPlay(false);
                }
            }
        }
        loadCallback(true, false, loadedDrawable, playTotalDuration, null);
        this.curGifDrawable = loadedDrawable;
        return false;
    }

    private void loadCallback(boolean isLoadSuc,boolean isPlayFinish, Drawable loadedDrawable, long playDuration, String extraInfo) {
        CommonLog.i(TAG, "-->loadCallback() isLoadSuc = " + isLoadSuc + " isPlayFinish = " + isPlayFinish + " playDuration = " + playDuration);
        if (loadCallback != null) {
            loadCallback.onLoadCallback(isLoadSuc,isPlayFinish, loadedDrawable, playDuration, extraInfo);
        }
        //added by fee 2019-07-08: 播放完成后来隐藏当前显示的ImageView控件
        if (isPlayFinish && isWillHideViewWhenPlayEnd) {
            if (theDisPlayImageView != null) {
                ImageView iv = theDisPlayImageView.get();
                if (iv != null) {
                    iv.setVisibility(View.GONE);
                }
            }
        }
    }

    protected GifLoadCallback loadCallback;
    private final int WHAT_NOTIFY_PLAY_FINISH = 10;

    public TinyGifDrawableLoader withPlayTimes(int willPlayTimes) {
        this.playTimes = willPlayTimes;
        return this;
    }

    public TinyGifDrawableLoader withLoadCallback(GifLoadCallback loadCallback) {
        this.loadCallback = loadCallback;
        return this;
    }

    public TinyGifDrawableLoader withPlayDuration(int willPlayDuration) {
        this.playTotalDuration = willPlayDuration;
        isToJudgePlayEndWithPlayDuration = willPlayDuration > 0;
        return this;
    }

    public TinyGifDrawableLoader withWillHideViewWhenPlayEnd(boolean willHideViewWhenPlayEnd) {
        this.isWillHideViewWhenPlayEnd = willHideViewWhenPlayEnd;
        return this;
    }
    @Override
    public int handleMessage(Message msg) {
        int msgWhat = msg.what;
        switch (msgWhat) {
            case WHAT_NOTIFY_PLAY_FINISH:
                loadCallback(true, true, curGifDrawable, playTotalDuration, null);
                break;
        }
        return 0;
    }

    public void release(ImageView theLoadGifImageView) {
        if (mHandler != null) {
            mHandler.release();
        }
        if (theLoadGifImageView != null) {
            Glide.with(theLoadGifImageView).clear(theLoadGifImageView);
        }
        if (curGifDrawable != null) {
            curGifDrawable.unregisterAnimationCallback(this);
//            curGifDrawable.recycle();
        }
        this.loadCallback = null;
    }

    /**
     * Called when the animation starts.
     *
     * @param drawable The drawable started the animation.
     */
    @Override
    public void onAnimationStart(Drawable drawable) {
    }

    /**
     * Called when the animation ends.
     *
     * @param drawable The drawable finished the animation.
     */
    @Override
    public void onAnimationEnd(Drawable drawable) {
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_NOTIFY_PLAY_FINISH);
        }
        loadCallback(true,true,drawable,playTotalDuration,null);
    }

    public boolean isGifPlaing() {
        return curGifDrawable != null && curGifDrawable.isRunning();
    }
    public interface GifLoadCallback {
        /**
         * @param isLoadSuc      是否加载成功
         * @param isPlayFinish   针对控制了gif播放指定次数的时候，回调本次是否播放完成
         * @param loadedDrawable 加载成功的Drawable
         * @param playDuration   如果是gif则播放的总时长
         * @param extraInfo      额外信息，如果是加载失败，则为异常信息
         */
        void onLoadCallback(boolean isLoadSuc,boolean isPlayFinish, Drawable loadedDrawable, long playDuration, String extraInfo);
    }
}
