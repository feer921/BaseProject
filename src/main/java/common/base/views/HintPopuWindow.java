package common.base.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import common.base.R;
import common.base.WeakHandler;


/**
 * 提示用PopupWindow
 * <br/>
 * 2015年8月17日-下午9:57:32
 * @author lifei
 */
public class HintPopuWindow extends PopupWindow implements WeakHandler.Handleable{
    private TextView tvHintMsg;
    private View anchorView;
    private int x,y;
//    private WeakHandler<HintPopuWindow> mWeakHandler;
    private WeakHandler mWeakHandler;
    private long popShowingLastMillsTime = 3 * 1000;
    public HintPopuWindow(Context context) {
        super(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.hint_popuwindow_layout, null);
        tvHintMsg = (TextView) contentView;
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setContentView(tvHintMsg);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B4000000")));
        setOutsideTouchable(true);
        setAnimationStyle(R.style.style_4_popwindow_translate_y);
    }
    public void setAnchorView(View anchorView,int xOffset,int yOffset){
        this.anchorView = anchorView;
        this.x = xOffset;
        this.y = yOffset;
    }
    public void hintMsg(String msg){
        if(tvHintMsg != null){
            tvHintMsg.setText(msg);
        }
        if(!isShowing()){
            if(anchorView != null){
                if (popShowingLastMillsTime > 0) {
                    if (mWeakHandler != null) {
                        mWeakHandler.removeCallbacksAndMessages(null);
                    }
                    else{
                        mWeakHandler = new WeakHandler<>(this);
                    }
                    mWeakHandler.sendEmptyMessageDelayed(0, popShowingLastMillsTime);
                }
                showAsDropDown(anchorView, x, y);
            }
        }
    }
    public void changeTheBackgroundDrawable(Drawable bkgDrawable){
        setBackgroundDrawable(bkgDrawable);
    }
    public void changeTheBackgroundDrawable(int colorHexValue){
        ColorDrawable colorDrawable = new ColorDrawable(colorHexValue);
        changeTheBackgroundDrawable(colorDrawable);
    }

    public void popHint(@StringRes int hintMsgResId) {
        if (tvHintMsg != null) {
            hintMsg(tvHintMsg.getContext().getString(hintMsgResId));
        }
    }

    /**
     * Disposes of the popup window. This method can be invoked only after
     * {@link #showAsDropDown(View)} has been executed. Failing
     * that, calling this method will have no effect.
     *
     * @see #showAsDropDown(View)
     */
    @Override
    public void dismiss() {
        if (mWeakHandler != null) {
            mWeakHandler.removeCallbacksAndMessages(null);
        }
        super.dismiss();
    }

    public void setShowLastMillsTime(long millsTime) {
        this.popShowingLastMillsTime = millsTime;
    }

    public TextView getTvHintMsg() {
        return tvHintMsg;
    }

    @Override
    public void handleMessage(Message msg) {
        dismiss();
    }
}
