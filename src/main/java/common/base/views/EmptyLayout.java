package common.base.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import common.base.R;
import common.base.utils.NetHelper;
import common.base.utils.Util;


/**
 * 加载服务器内容的开始或者结果布局
 * <br/>
 * 2015年8月10日-下午5:53:10
 * @author lifei
 */
public class EmptyLayout extends LinearLayout implements View.OnClickListener {

    public static final int HIDE_LAYOUT = 4;
    public static final int NETWORK_ERROR = 1;
    public static final int NETWORK_LOADING = 2;
    public static final int NODATA = 3;
    public static final int NODATA_ENABLE_CLICK = 5;
    public static final int NO_LOGIN = 6;
    /**
     * 点击可以加载更多数据
     */
    public static final int CLICK_2_LOAD_MORE = 7;
    /**
     * 没有更多数据可加载了
     */
    public static final int NO_MORE_DATA_DISABLE_CLICK = 8;
    private ProgressBar animProgress;
    private boolean clickEnable = true;
    private final Context context;
    public ImageView img;
    private OnClickListener listener;
    private int mErrorState;
    private String strNoDataContent = "";
    private TextView tvHint;
    private LinearLayout llLoadAndHintLayout;
    /**
     * 本布局控件是否在全屏模式下，如果在全屏模式下，一些图标要用大图
     */
    private boolean isInAFullMode = false;
    public EmptyLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public EmptyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        inflate(context, R.layout.empty_error_layout, this);
        llLoadAndHintLayout = (LinearLayout)findViewById(R.id.ll_load_and_hint);
        if(!isInEditMode())
        img = (ImageView) findViewById(R.id.img_error_layout);
        tvHint = (TextView) findViewById(R.id.tv_error_layout);
        if(!isInEditMode())
        animProgress = (ProgressBar) findViewById(R.id.animProgress);
        setBackgroundColor(-1);
        setOnClickListener(this);
        if(!isInEditMode()){
            img.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    if (clickEnable) {
                        // setErrorType(NETWORK_LOADING);
                        if (listener != null)
                            listener.onClick(v);
                    }
                }
            });
        }
    }


    public void dismiss() {
        mErrorState = HIDE_LAYOUT;
        setVisibility(View.GONE);
    }

    public int getErrorState() {
        return mErrorState;
    }

    public boolean isLoadError() {
        return mErrorState == NETWORK_ERROR;
    }

    public boolean isLoading() {
        return mErrorState == NETWORK_LOADING;
    }

    @Override
    public void onClick(View v) {
        if (clickEnable) {
            // setErrorType(NETWORK_LOADING);
            if (listener != null)
                listener.onClick(v);
        }
    }

    public void setHintMsg(String msg) {
        tvHint.setText(msg);
    }
    public void setHintMsg(int msgResID){
        tvHint.setText(msgResID);
    }
    public void setErrorImag(int imgResource) {
        try {
            img.setImageResource(imgResource);
        } catch (Exception e) {
        }
    }

    public void setErrorType(int i) {
        setVisibility(View.VISIBLE);
        if(!isInAFullMode){
            img.setBackgroundResource(R.drawable.hint_info_pink_icon);
        }
        mErrorState = i;
        switch (i) {
            case NETWORK_ERROR:
                if (NetHelper.isNetworkConnected(getContext())) {
                    tvHint.setText("内容加载失败\r\n点击重新加载");
                    if(isInAFullMode){
//                        img.setBackgroundResource(R.drawable.pagefailed_bg);
                        img.setBackgroundResource(0);
                    }
                } else {
                    tvHint.setText("没有可用的网络");
                    if(isInAFullMode){
//                        img.setBackgroundResource(R.drawable.network_icon);
                        img.setBackgroundResource(0);
                    }
                }
                img.setVisibility(View.VISIBLE);
                animProgress.setVisibility(View.GONE);
                clickEnable = true;
                break;
            case NETWORK_LOADING:
                // animProgress.setBackgroundDrawable(SkinsUtil.getDrawable(context,"loadingpage_bg"));
                animProgress.setVisibility(View.VISIBLE);
                img.setVisibility(View.GONE);
                tvHint.setText("正在加载,请稍候...");
                clickEnable = false;
                break;
            case NODATA:
                if(isInAFullMode){
//                    img.setBackgroundResource(R.drawable.info_hint_icon);
                    img.setBackgroundResource(0);
                }
                img.setVisibility(View.VISIBLE);
                animProgress.setVisibility(View.GONE);
                setTvNoDataContent();
                clickEnable = true;
                break;
            case HIDE_LAYOUT:
                setVisibility(View.GONE);
                break;
            case NODATA_ENABLE_CLICK:
                if(isInAFullMode){
//                    img.setBackgroundResource(R.drawable.info_hint_icon);
                    img.setBackgroundResource(0);
                }
                img.setVisibility(View.VISIBLE);
                animProgress.setVisibility(View.GONE);
                setTvNoDataContent();
                clickEnable = true;
                break;
            case CLICK_2_LOAD_MORE:
                tvHint.setText("点击加载更多数据");
                animProgress.setVisibility(View.GONE);
                clickEnable = true;
                break;
            case NO_MORE_DATA_DISABLE_CLICK:
                llLoadAndHintLayout.setVisibility(View.GONE);
                tvHint.setText("没有更多数据了");
                clickEnable = false;
                break;
            default:
                break;
        }
    }

    public void setNoDataContent(String noDataContent) {
        strNoDataContent = noDataContent;
    }

    public void setOnLayoutClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setTvNoDataContent() {
        if (!Util.isEmpty(strNoDataContent))
            tvHint.setText(strNoDataContent);
        else
            tvHint.setText("暂无数据");
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE)
            mErrorState = HIDE_LAYOUT;
        super.setVisibility(visibility);
    }

    public boolean isInAFullMode() {
        return isInAFullMode;
    }

    public void setInAFullMode(boolean isInAFullMode) {
        this.isInAFullMode = isInAFullMode;
    }
}
