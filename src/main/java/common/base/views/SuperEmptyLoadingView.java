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

    private int curHintMsgResId,curExtraOptResId;
    private ProgressBar pbDefLeftAnim,pbDefUpAnim;
    public LayoutStatus getCurStatus() {
        return curStatus;
    }

    public enum LayoutStatus{
        /**
         * 正在加载...
         */
        Loading(R.string.loading,0,0),
        /**
         * 没有数据
         */
        NoData(R.string.no_data,0,1),
        /**
         * 加载失败
         */
        LoadFailure(R.string.load_failure,R.string.click_retry,2),
        /**
         * 没有网络
         */
        NoNetWork(R.string.no_network,R.string.setting_network,3),
        /**
         * 水平方向的正在加载
         */
        HorizontalLoading(R.string.loading,0,4),
        /**
         * 提示，不带任何图片icon的
         * 需要配合上面几种布局内容使用
         */
        HintNoPic(0,0,5),
        /**
         * 提示：不显示额外操作的内容。
         * 需要配合上面4以前布局内容使用
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
                    optListener.optCallback(curStatus);
                }
            }
        };
        llHintAndLoad.setOnClickListener(onClickListener);
        tvExtraOptHint.setOnClickListener(onClickListener);
        pbDefLeftAnim = (ProgressBar) findViewById(R.id.def_left_load_anim);
        pbDefUpAnim = (ProgressBar) findViewById(R.id.def_up_load_anim);
    }

    public void showCase(LayoutStatus targetStatus) {
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
        if (curHintMsgResId <= 0) {
            tvHintMsg.setText(targetStatus.hintMsgResId);
        }
        if (curExtraOptResId <= 0) {
            tvExtraOptHint.setText(targetStatus.extraOptInfoResId);
        }
        ivShowStateIconOrAnim.setBackgroundDrawable(null);//把动画背景资源去除掉

        pbDefLeftAnim.setVisibility(GONE);
        pbDefUpAnim.setVisibility(GONE);
        switch (targetStatus) {
            case Loading:
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
                if (resetHintImageRes > 0) {
                    ivShowStateIconOrAnim.setImageResource(resetHintImageRes);
                }
                else if (noDataImageRes > 0) {
                    ivShowStateIconOrAnim.setImageResource(noDataImageRes);

                }
                tvExtraOptHint.setVisibility(VISIBLE);
                break;
            case NoNetWork:
                if (noNetworkImageRes > 0 && resetHintImageRes == 0) {
                    ivShowStateIconOrAnim.setImageResource(noNetworkImageRes);
                }
                else{
                    ivShowStateIconOrAnim.setImageResource(resetHintImageRes);
                }
                tvExtraOptHint.setVisibility(VISIBLE);
                break;
            case LoadFailure:
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
    }

    private SuperEmptyLoadingView withHintMsg(String hintMsg) {
        if (tvHintMsg != null) {
            tvHintMsg.setText(hintMsg);
        }
        return this;
    }

    public SuperEmptyLoadingView withHintMsg(@StringRes int curHintMsgResId) {
        this.curHintMsgResId = curHintMsgResId;
        return withHintMsg(getResStr(curHintMsgResId));
    }
    private SuperEmptyLoadingView withExtraHintMsg(String extraHintMsg) {
        if (tvExtraOptHint != null) {
            tvExtraOptHint.setText(extraHintMsg);
        }
        return this;
    }

    public SuperEmptyLoadingView withExtraHintMsg(@StringRes int extraHintMsgResId) {
        this.curExtraOptResId = extraHintMsgResId;
        return withExtraHintMsg(getResStr(extraHintMsgResId));
    }

    public SuperEmptyLoadingView withHintImage(@DrawableRes int hintImageResId){
        resetHintImageRes = hintImageResId;
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
         * @param curLayoutStatus 当前的布局状态参见{@link LayoutStatus}
         */
        void optCallback(LayoutStatus curLayoutStatus);
    }
}
