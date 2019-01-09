package common.base.utils;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/12/25<br>
 * Time: 10:23<br>
 * <P>DESC:
 * 通知先生
 * </p>
 * ******************(^_^)***********************
 */
public class MrNotification {

    /**
     * 判断是否通知权限被拒绝了
     * @param context Context
     * @return true:允许发送通知(一些系统会涉及到Toast"权限"); false:通知栏权限可能被禁止了
     */
    public static boolean isNotifyPermissionAllowed(Context context) {
       return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }
}