package common.base.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import common.base.R;


/**
 * 提示用PopupWindow
 * <br/>
 * 2015年8月17日-下午9:57:32
 * @author lifei
 */
public class HintPopuWindow extends PopupWindow{
    private TextView tvHintMsg;
    private View anchorView;
    private int x,y;
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
}
