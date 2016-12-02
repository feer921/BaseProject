package common.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-24
 * Time: 17:19
 * DESC: 界面跳转的帮助类
 */
public class BaseUiHelper{
    /**
     * 跳转到Intent的界面
     * @param context
     * @param startIntent 跳转意图
     * @param requestCode 请求码,如果目标界面返回结果的话的区分码
     * @param needBackResult 是否需要目标界面返回结果
     */
    public static void jumpToActivity(Context context, Intent startIntent, int requestCode, boolean needBackResult) {
        if (startIntent == null) {
            return;
        }
        if (needBackResult) {
            Activity curActivity = (Activity) context;
            curActivity.startActivityForResult(startIntent, requestCode);
        }
        else{
            context.startActivity(startIntent);
        }
    }

    /**
     * 跳转到相应的界面，不需要返回结果
     * @param context
     * @param targetActivityClass
     */
    public static void jumpToActivity(Context context, Class<?> targetActivityClass) {
        Intent startIntent = new Intent(context, targetActivityClass);
        jumpToActivity(context,startIntent,0,false);
    }

    /**
     * 跳转到界面并要求返回结果
     * @param context
     * @param targetActivityClass
     * @param requestCode
     */
    public static void jumpToActivity(Context context, Class<?> targetActivityClass, int requestCode) {
        Intent startIntent = new Intent(context, targetActivityClass);
        jumpToActivity(context,startIntent,requestCode,true);
    }

    /**
     * 隐藏输入法
     * @param activity
     */
    public static void hideInputMethod(Activity activity){
        if(activity!=null){
            View view = activity.getWindow().peekDecorView();
            if (view != null) {
                InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 跳转到系统设置界面
     * @param context
     */
    public static void jumpToSystemSetting(Context context) {
        Intent toSettingActionIntent = new Intent(Settings.ACTION_SETTINGS);
        jumpToActivity(context,toSettingActionIntent,0,false);
    }

    /**
     * 跳转到系统拨号界面
     * @param context
     * @param toDialTelNo
     */
    public static void jumpToSystemDialUi(Context context, String toDialTelNo) {
        Intent startIntent = new Intent(Intent.ACTION_DIAL);
        if (!Util.isEmpty(toDialTelNo)) {
            startIntent.setData(Uri.parse("tel:" + toDialTelNo));
        }
        jumpToActivity(context, startIntent, 0, false);
    }

    /**
     * 跳转到APN设置
     * @param context
     */
    public static void jumpToApnSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APN_SETTINGS);
        context.startActivity(intent);
    }

    public static void jumpToMobilDataSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
        context.startActivity(intent);
    }
}
