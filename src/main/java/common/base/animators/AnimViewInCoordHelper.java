package common.base.animators;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;

import java.util.List;

import common.base.utils.CommonLog;


/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/7/23<br>
 * Time: 15:44<br>
 * <P>DESC:
 * 让一个View在坐标系里动画工具
 * 此处为X、Y轴动画
 * </p>
 * ******************(^_^)***********************
 */
public class AnimViewInCoordHelper implements Animator.AnimatorListener {

    private static final String TAG = "AnimViewInCoordUtil";

    private View theAnimView;
    private float animStartX;
    private float animEndX;

    private float animStartY;
    private float animEndY;

    private int animToTargetWidth = -1;
    private int animToTargetHeight = -1;

    /** 第一阶段动画时长 */
    private long firstStageAnimDuration = 3000;
    /** 最后阶段动画时长 */
    private long lastStageAnimDuration = 3000;

    private int animStartWidth = -1;
    private int animStartHeight = -1;

    private Animator.AnimatorListener outSideAnimatorListener;

    /** 外部提供的时间插值器 */
    private TimeInterpolator outSideTimeInterpolator;

    private boolean isLastStagePlayTogether = true;

    /** 一个完整全阶段的动画集 */
    private AnimatorSet wholeAnimatorSet;

    private ObjectAnimator animX;
    private ObjectAnimator animY;
    /**
     * 对View进行X轴 绽放动画
     */
    private ObjectAnimator animScaleX;
    /**
     * 对View进行 Y 轴绽放动画
     */
    private ObjectAnimator animScaleY;

    private float animScaleXToRatio = -1;

    private float animScaleYToRatio = -1;
    private ObjectAnimator animWidth;
    private ObjectAnimator animHeight;

    /** 是否当前动画为逆向状态 */
    private boolean isReverseAnim;

    private ViewWrapper viewWrapper;

    public AnimViewInCoordHelper configAnimInfos(View willAnimView, float startXInCoord, float startYInCoord, float endXInCoord,
                                                 float endYInCoord, int targetWidth, int targetHeight) {
        this.theAnimView = willAnimView;
        this.animStartX = startXInCoord;
        this.animEndX = endXInCoord;
        this.animStartY = startYInCoord;
        this.animEndY = endYInCoord;
        this.animToTargetWidth = targetWidth;
        this.animToTargetHeight = targetHeight;
        return this;
    }
    private IAssembleOtherAnimator mAssembleOtherAnimator;


    /**
     * <p>Notifies the start of the animation.</p>
     *
     * @param animation The started animation.
     */
    @Override
    public void onAnimationStart(Animator animation) {
        CommonLog.i(TAG, "-->onAnimationStart() animation = " + animation);
        if (outSideAnimatorListener != null) {
            outSideAnimatorListener.onAnimationStart(animation);
        }
        if (animStateListener != null) {
            animStateListener.animStartState(this, true, this.isReverseAnim);
        }
    }

    /**
     * <p>Notifies the end of the animation. This callback is not invoked
     * for animations with repeat count set to INFINITE.</p>
     *
     * @param animation The animation which reached its end.
     */
    @Override
    public void onAnimationEnd(Animator animation) {
        if (outSideAnimatorListener != null) {
            outSideAnimatorListener.onAnimationEnd(animation);
        }
        if (animStateListener != null) {
            animStateListener.animEndState(this, this.isReverseAnim);
        }
        if (viewWrapper != null) {
            viewWrapper.resetXY();
        }
        CommonLog.i(TAG, "-->onAnimationEnd() animation = " + animation + " width:  " + viewWrapper.getWidth());
    }

    /**
     * <p>Notifies the cancellation of the animation. This callback is not invoked
     * for animations with repeat count set to INFINITE.</p>
     *
     * @param animation The animation which was canceled.
     */
    @Override
    public void onAnimationCancel(Animator animation) {
        if (outSideAnimatorListener != null) {
            outSideAnimatorListener.onAnimationCancel(animation);
        }
    }

    /**
     * <p>Notifies the repetition of the animation.</p>
     *
     * @param animation The animation which was repeated.
     */
    @Override
    public void onAnimationRepeat(Animator animation) {
        if (outSideAnimatorListener != null) {
            outSideAnimatorListener.onAnimationRepeat(animation);
        }
    }

    public AnimatorSet startAnimator(boolean autoStart) {
        this.animStartWidth = theAnimView.getWidth();
        this.animStartHeight = theAnimView.getHeight();
        isReverseAnim = false;
        wholeAnimatorSet = null;
        assembleAnimInfos();
        wholeAnimatorSet.setTarget(viewWrapper);
        isReverseAnim = false;
        boolean animCanStart = !wholeAnimatorSet.isRunning();
        if (autoStart && animCanStart) {
            wholeAnimatorSet.start();
        }
        if (animCanStart) {
            if (animStateListener != null) {
                animStateListener.animStartState(this, false, false);
            }
        }
        return this.wholeAnimatorSet;
    }

    /**
     * 配置动画参数(不动画View的宽、高)
     * @param willAnimView
     * @param startXInCoord
     * @param startYInCoord
     * @param endXInCoord
     * @param endYInCoord
     * @return self
     */
    public AnimViewInCoordHelper configAnimInfos(View willAnimView, float startXInCoord, float startYInCoord,
                                                 float endXInCoord, float endYInCoord) {
        return configAnimInfos(willAnimView, startXInCoord, startYInCoord, endXInCoord, endYInCoord, -1, -1);
    }

    public boolean canReverseAnim() {
        return wholeAnimatorSet != null;
    }

    /**
     * 逆向/翻转 动画
     */
    public boolean reverseAnim() {
        if (wholeAnimatorSet == null || wholeAnimatorSet.isRunning()) {
            return false;
        }

        isReverseAnim = true;
        wholeAnimatorSet = null;
        assembleAnimInfos();
        if (animStateListener != null) {
            animStateListener.animStartState(this, false, true);
        }

        wholeAnimatorSet.start();
        return true;
    }

    public AnimViewInCoordHelper setFirstStageAnimDuration(long firstStageAnimDuration) {
        this.firstStageAnimDuration = firstStageAnimDuration;
        return this;
    }

    public AnimViewInCoordHelper setLastStageAnimDuration(long lastStageAnimDuration) {
        this.lastStageAnimDuration = lastStageAnimDuration;
        return this;
    }

    public AnimViewInCoordHelper setOutSideAnimatorListener(Animator.AnimatorListener outSideAnimatorListener) {
        this.outSideAnimatorListener = outSideAnimatorListener;
        return this;
    }

    public AnimViewInCoordHelper setLastStagePlayTogether(boolean lastStagePlayTogether) {
        isLastStagePlayTogether = lastStagePlayTogether;
        return this;
    }

    public AnimViewInCoordHelper setOutSideTimeInterpolator(TimeInterpolator timeInterpolator) {
        this.outSideTimeInterpolator = timeInterpolator;
        if (wholeAnimatorSet != null) {
            wholeAnimatorSet.setInterpolator(timeInterpolator);
        }
        return this;
    }

    public AnimViewInCoordHelper setAnimStateListener(IAnimListener l) {
        this.animStateListener = l;
        return this;
    }

    /**
     * 装载动画的配置信息
     */
    private void assembleAnimInfos() {
        if (wholeAnimatorSet == null) {
            wholeAnimatorSet = new AnimatorSet();
            if (theAnimView != null) {
                viewWrapper = new ViewWrapper(theAnimView);
                //X坐标位置平移
                if (!isReverseAnim) {
                    animX = ObjectAnimator.ofFloat(viewWrapper, "coordX", animStartX, animEndX);
                } else {
                    animX = ObjectAnimator.ofFloat(viewWrapper, "coordX", animEndX, animStartX);
                }
                animX.setDuration(this.firstStageAnimDuration);
                //Y坐标位置平移
                if (!isReverseAnim) {
                    animY = ObjectAnimator.ofFloat(viewWrapper, "coordY", animStartY, animEndY);

                } else {
                    animY = ObjectAnimator.ofFloat(viewWrapper, "coordY", animEndY, animStartY);
                }
                animY.setDuration(this.firstStageAnimDuration);
                //X坐标轴缩放
                if (animScaleXToRatio != -1) {
                    if (!isReverseAnim) {
                        animScaleX = ObjectAnimator.ofFloat(viewWrapper, "scaleX", 1, animScaleXToRatio);
                    }
                    else {
                        animScaleX = ObjectAnimator.ofFloat(viewWrapper, "scaleX", animScaleXToRatio, 1);
                    }
                }

                if (animScaleX != null) {
                    animScaleX.setDuration(this.firstStageAnimDuration);
                }

                //Y 坐标轴缩放
                if (animScaleYToRatio != -1) {
                    if (!isReverseAnim) {
                        animScaleY = ObjectAnimator.ofFloat(viewWrapper, "scaleY", 1, animScaleYToRatio);
                    }
                    else {
                        animScaleY = ObjectAnimator.ofFloat(viewWrapper, "scaleY", animScaleYToRatio, 1);
                    }
                }

                if (animScaleY != null) {
                    animScaleY.setDuration(this.firstStageAnimDuration);
                }

                AnimatorSet lastStageAnimatorSet = null;
                if (animToTargetWidth > 0) {
                    if (!isReverseAnim) {
                        animWidth = ObjectAnimator.ofInt(viewWrapper, "width", theAnimView.getWidth(), animToTargetWidth);
                    } else {
                        animWidth = ObjectAnimator.ofInt(viewWrapper, "width", animToTargetWidth, animStartWidth);
                    }
                    animWidth.setDuration(lastStageAnimDuration);
                }

                if (animToTargetHeight > 0) {
                    if (!isReverseAnim) {
                        animHeight = ObjectAnimator.ofInt(viewWrapper, "height", theAnimView.getHeight(), animToTargetHeight);
                    } else {
                        animHeight = ObjectAnimator.ofInt(viewWrapper, "height", animToTargetHeight, animStartHeight);
                    }
                    animHeight.setDuration(lastStageAnimDuration);

                }
                if (animWidth != null || animHeight != null) {
                    lastStageAnimatorSet = new AnimatorSet();
                    AnimatorSet.Builder builder = null;
                    if (animWidth != null) {
                        builder = lastStageAnimatorSet.play(animWidth);
                    }
                    if (animHeight != null) {
                        if (builder != null) {
                            builder.with(animHeight);
                        } else {
                            lastStageAnimatorSet.play(animHeight);
                        }
                    }
                }

                AnimatorSet.Builder wholeAnimsBuilder = wholeAnimatorSet.play(animX).with(animY);
                if (animScaleX != null) {
                    wholeAnimsBuilder.with(animScaleX);
                }
                if (animScaleY != null) {
                    wholeAnimsBuilder.with(animScaleY);
                }
                //增加可额外配置其他的可支持的动画
                IAssembleOtherAnimator otherAnimator = this.mAssembleOtherAnimator;
                if (otherAnimator != null) {
                    List<ObjectAnimator> otherAnimators = otherAnimator.assembleOtherAnimator(this,viewWrapper, isReverseAnim);
                    if (otherAnimators != null && otherAnimators.size() > 0) {
                        for (ObjectAnimator theOtherAnimator : otherAnimators) {
                            theOtherAnimator.setDuration(this.firstStageAnimDuration);
                            wholeAnimsBuilder.with(theOtherAnimator);
                        }
                    }
                }

                if (lastStageAnimatorSet != null) {
                    if (isLastStagePlayTogether) {
                        wholeAnimsBuilder.with(lastStageAnimatorSet);
                    } else {
                        wholeAnimsBuilder.before(lastStageAnimatorSet);
                    }
                }
                wholeAnimatorSet.setTarget(viewWrapper);
            }
            if (outSideTimeInterpolator != null) {
                wholeAnimatorSet.setInterpolator(outSideTimeInterpolator);
            }
            wholeAnimatorSet.addListener(this);
        }
    }

    public AnimViewInCoordHelper setScaleX(float scaleXRatio) {
        this.animScaleXToRatio = scaleXRatio;
        return this;
    }

    private IAnimListener animStateListener;

    public interface IAnimListener {
        /**
         * 动画开始状态回调
         *
         * @param isStarted     是否真正开始了，true:动画已经开始了；false:预备开始
         * @param isReverseAnim 是否为逆向动画， true:现在为逆向动画
         */
        void animStartState(AnimViewInCoordHelper whoInvoke, boolean isStarted, boolean isReverseAnim/*,Animator curAnimator*/);

        /**
         * 动画end状态回调
         *
         * @param isReverseAnim 是否为逆向动画， true:现在为逆向动画
         */
        void animEndState(AnimViewInCoordHelper whoInvoke, boolean isReverseAnim/*,Animator curAnimator*/);

    }

    public AnimViewInCoordHelper setScaleY(float scaleYRatio){
        this.animScaleYToRatio = scaleYRatio;
        return this;
    }

    public AnimViewInCoordHelper setAssembleOtherAnimator(IAssembleOtherAnimator assembleOtherAnimator) {
        this.mAssembleOtherAnimator = assembleOtherAnimator;
        return this;
    }

    public void free() {
        if (wholeAnimatorSet != null) {
            wholeAnimatorSet.cancel();
        }
        outSideAnimatorListener = null;
        outSideTimeInterpolator = null;
        animStateListener = null;
        mAssembleOtherAnimator = null;
    }
    public interface IAssembleOtherAnimator{
        List<ObjectAnimator> assembleOtherAnimator(AnimViewInCoordHelper curAnimHellper, ViewWrapper curViewWrapper, boolean isReverseAnim);

    }

}
