package common.base.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import common.base.R;

/**
 * ******************(^_^)***********************<br>
 * User: 11776610771@qq.com<br>
 * Date: 2017/6/8<br>
 * Time: 14:03<br>
 * <P>DESC:无内容提示布局(空布局)、加载数据中提示布局、其他提示布局
 * </p>
 * ******************(^_^)***********************
 */

public class SuperEmptyLoadingView extends LinearLayout {
    private LayoutStatus curStatus = LayoutStatus.Loading;
    /**
     * 提示文本，如：正在加载...
     */
    private TextView tvHintMsg;
    /**
     * 额外的操作提示文本
     */
    private TextView tvExtraOptHint;
    /**
     * 正在加载或者提示性的图标显示
     */
    private ImageView ivShowStateIconOrAnim;
    /**
     * 水平方向的
     */
    private ImageView ivHorizontalLoad;
    private AnimationDrawable mAnimBgDrawable;
    private AnimationDrawable mAnimDrawableAtLeft;
    private int animResAtHintIcon;

    /**
     * 整个布局是否可点击
     */
    private boolean isWholeLayoutClickable = false;

    private ProgressBar pbDefLeftAnim,pbDefUpAnim;

    /**
    * 加载中时的提示文案
    */
    private int loadingHintStr;
    /**
     * 加载完后没有数据的提示文案
      */
    private int noDataHintStr;
    /**
     * 加载失败时的提示文案
      */
    private int loadFailureHintStr;
    /**
     * 没有网络时的提示文案
      */
    private int noNetHintStr;
    /**
     * 没有数据时的下面额外的提示文案
     */
    private int extraNoDataStr;
    /**
     * 加载失败时额外的提示文案
      */
    private int extraLoadFailureStr;
    /**
     * 没有网络时额外的提示文案
      */
    private int extraNoNetStr;
    public LayoutStatus getCurStatus() {
        return curStatus;
    }

    public enum LayoutStatus{
        /**
         * 正在加载...<br/>
         * hint msg: 正在加载...<br/>
         * extraOpt msg: null
         */
        Loading(R.string.loading,0,0),
        /**
         * 没有数据<br/>
         * hint msg: 暂时没有任何数据<br/>
         * extraOpt msg: 点击重试
         */
        NoData(R.string.no_data,R.string.click_retry,1),
        /**
         * 加载失败<br/>
         * hint msg: 抱歉,加载失败！<br/>
         * extraOpt msg: 点击重试
         */
        LoadFailure(R.string.load_failure,R.string.click_retry,2),
        /**
         * 没有网络<br/>
         * hint msg: 网络好象出问题了<br/>
         * extraOpt msg: 设置网络
         */
        NoNetWork(R.string.no_network,R.string.setting_network,3),
        /**
         * 水平方向的正在加载<br/>
         * hint msg: 正在加载,请稍候...<br/>
         * extraOpt msg: null
         */
        HorizontalLoading(R.string.loading,0,4),
        /**
         * 提示，不带任何图片icon的
         * 需要配合上面几种布局内容使用<br/>
         * hint msg: null<br/>
         * extraOpt msg: null
         */
        HintNoPic(0,0,5),
        /**
         * 提示：不显示额外操作的内容。
         * 需要配合上面4以前布局内容使用<br/>
         * hint msg: null<br/>
         * extraOpt msg: null
         */
        HintNoExtraOpt(0,0,6);
        int statusCode;
        int hintMsgResId,extraOptInfoResId;
        LayoutStatus(@StringRes int hintMsgResId, @StringRes int extraOptInfoResId, int statusCode){
            this.hintMsgResId = hintMsgResId;
            this.extraOptInfoResId = extraOptInfoResId;
            this.statusCode = statusCode;
        }
    }
    public SuperEmptyLoadingView(Context context) {
        this(context,null);
    }

    public SuperEmptyLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.super_empty_load_layout, this);
        tvHintMsg = (TextView) findViewById(R.id.tv_hint_msg);
        tvExtraOptHint = (TextView) findViewById(R.id.tv_extra_desc_or_click);
        ivShowStateIconOrAnim = (ImageView) findViewById(R.id.iv_load_or_hint_image);
        ivHorizontalLoad = (ImageView) findViewById(R.id.iv_horizontal_load_anim);
        LinearLayout llHintAndLoad = (LinearLayout) findViewById(R.id.ll_load_and_hint);
        View.OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWholeLayoutClickable) {
                    //不能点击操作的时候，则点击无效
                    return;
                }
                //回调出去
                if (optListener != null) {
                    optListener.optCallback(SuperEmptyLoadingView.this,curStatus);
                }
            }
        };
        llHintAndLoad.setOnClickListener(onClickListener);
        tvExtraOptHint.setOnClickListener(onClickListener);
        pbDefLeftAnim = (ProgressBar) findViewById(R.id.def_left_load_anim);
        pbDefUpAnim = (ProgressBar) findViewById(R.id.def_up_load_anim);
    }

    public SuperEmptyLoadingView showCase(LayoutStatus targetStatus) {
        //???
        if (targetStatus == curStatus) {
            return this;
        }
        curStatus = targetStatus;
        ivHorizontalLoad.setVisibility(GONE);//水平方向的默认为隐藏
        isWholeLayoutClickable = true;
        tvExtraOptHint.setVisibility(GONE);//额外操作控件默认隐藏
        ivShowStateIconOrAnim.setVisibility(VISIBLE);//垂直方向上的图标默认显示

        if (mAnimDrawableAtLeft != null) {
            mAnimDrawableAtLeft.stop();
        }
        if (mAnimBgDrawable != null) {
            mAnimBgDrawable.stop();
        }

        //在该showCase下，如果没有提供hint提示性文案，则使用LayoutStatus自备的文案
        int attachHintMsgResId = targetStatus.hintMsgResId;
        int attachExtraMsgResId = targetStatus.extraOptInfoResId;

        ivShowStateIconOrAnim.setBackgroundDrawable(null);//把动画背景资源去除掉

        pbDefLeftAnim.setVisibility(GONE);
        pbDefUpAnim.setVisibility(GONE);
        switch (targetStatus) {
            case Loading:
                tvHintMsg.setText(loadingHintStr > 0 ? loadingHintStr : attachHintMsgResId);
                isWholeLayoutClickable = false;
                if (mAnimBgDrawable == null) {
                    ivShowStateIconOrAnim.setVisibility(GONE);
                    pbDefUpAnim.setVisibility(VISIBLE);
                }
                else{
                    ivShowStateIconOrAnim.setImageDrawable(null);//前景去除掉
                    ivShowStateIconOrAnim.setBackgroundDrawable(mAnimBgDrawable);
                    if (mAnimBgDrawable != null) {
                        mAnimBgDrawable.start();
                    }
                }
                break;
            case HorizontalLoading://水平方向的正在加载...
                tvHintMsg.setText(loadingHintStr > 0 ? loadingHintStr : attachHintMsgResId);
                isWholeLayoutClickable = false;
                ivShowStateIconOrAnim.setVisibility(GONE);//垂直方向的隐藏
                if (mAnimDrawableAtLeft == null) {
                    pbDefLeftAnim.setVisibility(VISIBLE);
                }
                else{
                    ivHorizontalLoad.setVisibility(VISIBLE);
                    if (mAnimDrawableAtLeft != null) {
                        mAnimDrawableAtLeft.start();
                    }
                }
                break;
            case NoData:
                tvHintMsg.setText(noDataHintStr > 0 ? noDataHintStr : attachHintMsgResId);
                tvExtraOptHint.setText(extraNoDataStr > 0 ? extraNoDataStr : attachExtraMsgResId);
                if (resetHintImageRes > 0) {
                    ivShowStateIconOrAnim.setImageResource(resetHintImageRes);
                }
                else if (noDataImageRes > 0) {
                    ivShowStateIconOrAnim.setImageResource(noDataImageRes);

                }
                tvExtraOptHint.setVisibility(VISIBLE);
                break;
            case NoNetWork:
                tvHintMsg.setText(noNetHintStr > 0 ? noNetHintStr : attachHintMsgResId);
                tvExtraOptHint.setText(extraNoNetStr > 0 ? extraNoNetStr : attachExtraMsgResId);
                if (noNetworkImageRes > 0 && resetHintImageRes == 0) {
                    ivShowStateIconOrAnim.setImageResource(noNetworkImageRes);
                }
                else{
                    ivShowStateIconOrAnim.setImageResource(resetHintImageRes);
                }
                tvExtraOptHint.setVisibility(VISIBLE);
                break;
            case LoadFailure:
                tvHintMsg.setText(loadFailureHintStr > 0 ? loadFailureHintStr : attachHintMsgResId);
                tvExtraOptHint.setText(extraLoadFailureStr > 0 ? extraLoadFailureStr : attachExtraMsgResId);
                if (loadFailureImageRes > 0 && resetHintImageRes == 0) {
                    ivShowStateIconOrAnim.setImageResource(loadFailureImageRes);
                }
                else{
                    ivShowStateIconOrAnim.setImageResource(resetHintImageRes);
                }
                tvExtraOptHint.setVisibility(VISIBLE);
                break;
            case HintNoPic://加载后结果的提示，连图片提示也不需要
                tvExtraOptHint.setVisibility(VISIBLE);
                ivShowStateIconOrAnim.setVisibility(GONE);
                break;
            case HintNoExtraOpt:
                if (resetHintImageRes > 0) {
                    ivShowStateIconOrAnim.setImageResource(resetHintImageRes);
                }
//                tvExtraOptHint.setVisibility(GONE);
                break;
        }
        resetHintImageRes = 0;
        return this;
    }

    /**
     * 提供在showCase之后直接更改提示性文案的方法
     * 注：在{@link #showCase(LayoutStatus)}之后调用
     * @param afterShowCaseHintMsg
     * @return self
     */
    public SuperEmptyLoadingView andHintMsg(String afterShowCaseHintMsg) {
        return withHintMsg(afterShowCaseHintMsg);
    }

    /**
     * 提供在showCase之后直接更改Extra提示文案的方法
     * 注：在{@link #showCase(LayoutStatus)}之后调用
     * @param afterShowCaseExtraHintMsg
     * @return self
     */
    public SuperEmptyLoadingView andExtraHintMsg(String afterShowCaseExtraHintMsg) {
        return withExtraHintMsg(afterShowCaseExtraHintMsg);
    }

    /**
     * 提供在{@link #showCase(LayoutStatus)}之后动态更改提示性图片的方法
     * 注：在{@link #showCase(LayoutStatus)}之后调用
     * @param afterShowCaseHintImage
     * @return self
     */
    public SuperEmptyLoadingView andHintImagePic(@DrawableRes int afterShowCaseHintImage){
        return withHintImage(afterShowCaseHintImage);
    }
    private SuperEmptyLoadingView withHintMsg(String hintMsg) {
        if (tvHintMsg != null) {
            tvHintMsg.setText(hintMsg);
        }
        return this;
    }

    /**
     * 在{@link #showCase(LayoutStatus)}之前设置好各case下的提示性文案
     * 每种case下设置一次，如果在{@link #showCase(LayoutStatus)}之后仍然想更改提示性文案则调用{@link #andHintMsg(String)}
     * @param hintInCase 相应的case
     * @param curHintMsgResId 当前case下的提示性文案
     * @return self
     */
    public SuperEmptyLoadingView withHintMsg(LayoutStatus hintInCase,@StringRes int curHintMsgResId) {
        switch (hintInCase) {
            case Loading:
            case HorizontalLoading:
                loadingHintStr = curHintMsgResId;
                break;
            case NoData:
                noDataHintStr = curHintMsgResId;
                break;
            case NoNetWork:
                noNetHintStr = curHintMsgResId;
                break;
            case LoadFailure:
                loadFailureHintStr = curHintMsgResId;
                break;
        }
        return this;
    }
    private SuperEmptyLoadingView withExtraHintMsg(String extraHintMsg) {
        if (tvExtraOptHint != null) {
            tvExtraOptHint.setText(extraHintMsg);
        }
        return this;
    }

    /**
     * 在{@link #showCase(LayoutStatus)}之前设置好各case下的额外提示性(在提示性文案下面)文案
     * 每种case下设置一次，如果在{@link #showCase(LayoutStatus)}之后仍然想更改额外提示性文案则调用{@link #andExtraHintMsg(String)}
     * @param hintInCase 相应的case 只需要设置:NoData NoNetWork LoadFailure三种情况
     * @param extraHintMsgResId 当前case下的额外提示性文案
     * @return self
     */
    public SuperEmptyLoadingView withExtraHintMsg(LayoutStatus hintInCase,@StringRes int extraHintMsgResId) {
        switch (hintInCase) {
            case Loading:
            case HorizontalLoading:
                //no need extra hint msg
                break;
            case NoData:
                extraNoDataStr = extraHintMsgResId;
                break;
            case NoNetWork:
                extraNoNetStr = extraHintMsgResId;
                break;
            case LoadFailure:
                extraLoadFailureStr = extraHintMsgResId;
                break;
        }
        return this;
    }

    private SuperEmptyLoadingView withHintImage(@DrawableRes int hintImageResId){
//        resetHintImageRes = hintImageResId;
        if (ivShowStateIconOrAnim != null) {
            ivShowStateIconOrAnim.setImageResource(hintImageResId);
        }
        return this;
    }

    /**
     * 加载数据失败提示的图片/icon
     */
    private int loadFailureImageRes;
    /**
     * 没有网络提示的图片/icon
     */
    private int noNetworkImageRes;
    /**
     * 没有数据提示的图片/icon
     */
    private int noDataImageRes;
    /**
     * 用户可调用{@link #withHintImage(int)}动态随机配置的图片资源
     */
    private int resetHintImageRes;
    public SuperEmptyLoadingView withLoadFailureImage(@DrawableRes int loadFailureImageRes) {
        this.loadFailureImageRes = loadFailureImageRes;
        return this;
    }
    public SuperEmptyLoadingView withNoDataImage(@DrawableRes int noDataImageRes) {
        this.noDataImageRes = noDataImageRes;
        return this;
    }
    public SuperEmptyLoadingView withNoNetWorkImage(@DrawableRes int noNetWorkImageRes) {
        this.noNetworkImageRes = noNetWorkImageRes;
        return this;
    }
    /**
     * 给垂直方向的“正在加载..."设置帧动画资源
     * @param loadingAnimRes
     * @return
     */
    public SuperEmptyLoadingView withLoadingAnim(@DrawableRes int loadingAnimRes) {
        if (this.animResAtHintIcon != loadingAnimRes) {
            if (ivShowStateIconOrAnim != null) {
                ivShowStateIconOrAnim.setBackgroundResource(loadingAnimRes);
                mAnimBgDrawable = (AnimationDrawable) ivShowStateIconOrAnim.getBackground();
            }
            this.animResAtHintIcon = loadingAnimRes;
        }
        return this;
    }
    private int animResAtHorizontalIv;

    /**
     * 水平方向的“正在加载...”设置帧动画资源
     * @param loadingAnimRes
     * @return
     */
    public SuperEmptyLoadingView withHorizontalLoadAnim(@DrawableRes int loadingAnimRes){
        if (animResAtHorizontalIv != loadingAnimRes) {
            if (ivHorizontalLoad != null) {
                ivHorizontalLoad.setBackgroundResource(loadingAnimRes);
                mAnimDrawableAtLeft = (AnimationDrawable) ivHorizontalLoad.getBackground();
            }
            this.animResAtHorizontalIv = loadingAnimRes;
        }
        return this;
    }
    private IoptCallback optListener;
    public SuperEmptyLoadingView withOptCallback(IoptCallback callback) {
        optListener = callback;
        return this;
    }
    /**
     * 获取显示 提示内容的控件 以便于设置字体、颜色等
     * @return
     */
    public TextView getTvHintMsg() {
        return tvHintMsg;
    }

    /**
     * 获取显示 额外提示内容的控件 以便于设置字体、颜色等
     * @return
     */
    public TextView getTvExtraOptHint() {
        return tvExtraOptHint;
    }

    private String getResStr(@StringRes int strResId) {
        return getContext().getString(strResId);
    }


    public interface IoptCallback{
        /**
         * 主要为本SuperEmptyLoadingView 在Loading之后的点击操作的回调，供外部作具体的处理
         * @param curLayoutStatus 当前的布局状态参见{@link SuperEmptyLoadingView.LayoutStatus}
         */
        void optCallback(SuperEmptyLoadingView theEmptyLoadingView, LayoutStatus curLayoutStatus);
    }
}
