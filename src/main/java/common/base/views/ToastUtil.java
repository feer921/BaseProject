package common.base.views;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import common.base.R;

/**
 * 让Toast加载自定义布局并显示消息的工具
 */
public class ToastUtil {
	static LayoutInflater layoutInflater = null;
	static Context mContext;
	static int marginBottomH = 0;
	private static int marginTopH = 20;
	public static void setContext(Context context) {
		mContext = context;
		//marginBottomH = (int) mContext.getResources().getDimension(R.dimen.tabwidget_height);
		if (layoutInflater == null) {
			layoutInflater = LayoutInflater.from(mContext);
		}
//		marginTopH = (int) mContext.getResources().getDimension(R.dimen.common_head_layout_def_height);
	}

	public static void showToast(String str) {
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.layout_toast, null);
		//TextView messageInfo = (TextView) v.findViewById(R.id.toast_info);
		//messageInfo.setText(str);
		mToast.setView(v);
		mToast.setGravity(Gravity.BOTTOM, 0, 8);
		mToast.setDuration(300);
		mToast.show();
	}

	public static void showToast(int strID) {
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.layout_toast, null);
		TextView messageInfo = (TextView) v.findViewById(R.id.toast_info);
		messageInfo.setText(strID);
		mToast.setView(v);
		mToast.setGravity(Gravity.BOTTOM, 0, 8);
		mToast.setDuration(300);
		mToast.show();
	}

	public static void showToast(int strID, int gravity) {
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.toast_base, null);
		TextView messageInfo = (TextView) v.findViewById(R.id.toast_msg);
		messageInfo.setText(strID);
		mToast.setView(v);
		if(gravity==Gravity.TOP){
			mToast.setGravity(gravity, -100, 0);
		}else{
			mToast.setGravity(gravity, 0, marginBottomH + 20);
		}
		mToast.setDuration(300);
		mToast.show();
	}
	public static void showToast(String message, int gravity) {
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.layout_toast, null);
		TextView messageInfo = (TextView) v.findViewById(R.id.toast_info);
		messageInfo.setText(message);
		mToast.setView(v);
		if(gravity==Gravity.TOP){
			mToast.setGravity(gravity, -100, 0);
		}else{
			mToast.setGravity(gravity, 0, marginBottomH + 20);
		}
		mToast.setDuration(300);
		mToast.show();
	}
	public static void show(String msg,int gravity){
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.toast_base, null);
		TextView messageInfo = (TextView) v.findViewById(R.id.toast_msg);
		messageInfo.setText(msg);
		mToast.setView(v);
		mToast.setGravity(gravity, 0, 20);
		mToast.setDuration(300);
		mToast.show();
	}
	public static void topShow(int  msgId ){
        topShow(mContext.getString(msgId));
	}
	public static void topShow(String  msg ){
		Toast mToast = new Toast(mContext);
		View v = layoutInflater.inflate(R.layout.toast_base, null);
		TextView messageInfo = (TextView) v.findViewById(R.id.toast_msg);
		messageInfo.setText(msg);
		mToast.setView(v);
		mToast.setGravity(Gravity.TOP, 0, marginTopH);
		mToast.setDuration(300);
		mToast.show();
	}

    /**
     * 调整居上显示的Toast距离顶部的间距
     * @param toMargintopH
     */
    public static void adjustToastMarginTopH(int toMargintopH) {
        marginTopH = toMargintopH;
    }
}
