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

    public static void hideInputMethod(Context context,View theViewRequest) {
        InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanger.hideSoftInputFromWindow(theViewRequest.getWindowToken(), 0);
        //added
//        1、方法一(如果输入法在窗口上已经显示，则隐藏，反之则显示)
//        inputmanger.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//        2、方法二（view为接受软键盘输入的视图，SHOW_FORCED表示强制显示）
//        inputmanger.showSoftInput(view,InputMethodManager.SHOW_FORCED);
//        inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘

//        3、调用隐藏系统默认的输入法 ???
//        inputmanger.hideSoftInputFromWindow(Activity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); (Activity是当前的Activity)

//        4、获取输入法打开的状态
//        boolean isOpen=inputmanger.isActive();//isOpen若返回true，则表示输入法打开
    }

    public static void showInputMethod(Context context,View theViewRequest) {
        InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputmanger != null) {
            inputmanger.showSoftInput(theViewRequest, InputMethodManager.SHOW_IMPLICIT);
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

    /**
     * Intent.ACTION_SENDTO 无附件的发送
     Intent.ACTION_SEND 带附件的发送
     Intent.ACTION_SEND_MULTIPLE 带有多附件的发送
     * @param context
     * @param targetEmail
     * @param subject
     * @param emailContent
     * @return
     */
    public static boolean jumpToSendEmail(Context context, String targetEmail, String subject, String emailContent) {
        Intent emailActionIntent = new Intent(Intent.ACTION_SENDTO);
//        PackageManagerUtil.getPackageVersionName()
        emailActionIntent.setData(Uri.parse("mailto:" + targetEmail));
//        emailActionIntent.putExtra(Intent.EXTRA_EMAIL, targetEmail);
        emailActionIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailActionIntent.putExtra(Intent.EXTRA_TEXT, emailContent);
//        context.startActivity(emailActionIntent);//这样不会弹出选择框
        context.startActivity(Intent.createChooser(emailActionIntent,"ddd"));//这样会弹出选择要使用的邮箱应用
        return true;
    }

    /**
     * 跳转到Wifi设备界面
     * @param context
     * @return
     */
    public static boolean jumpToWifiSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);
        return true;
    }

    public static boolean jumpToBtSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        context.startActivity(intent);
        return true;
    }

    public static void jumpToBrowser(Context context,String theUrl) {
        Uri uri = Uri.parse(theUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            jumpToActivity(context, intent, -1, false);
        } catch (Exception e) {
            //nt.ActivityNotFoundException: No Activity found to handle Intent { act=android.intent.action.VIEW dat=www.baidu.com }
            CommonLog.e("--->jumpToBrowser() occur:" + e);
        }
    }
}
