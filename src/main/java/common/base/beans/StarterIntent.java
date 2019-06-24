package common.base.beans;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import common.base.utils.Util;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/6/24<br>
 * Time: 18:04<br>
 * <P>DESC:
 * extends Intent,自已本身来调用启动Activity或者Service
 * 场景为：再使用Intent设置各种数据后，并不想立刻调用 Context # startActivity()等API，而是需要再次去设置
 * 可能需要的数据，则使用本类非常合适
 * </p>
 * ******************(^_^)***********************
 */
public class StarterIntent extends Intent {

    public void startActivity(Context context) {
        startActivity(context,false,-1,null);
    }

    public void startActivityForResult(Context activity, int requestCode) {
        startActivity(activity,true,requestCode,null);
//        if (activity != null) {
//            activity.startActivityFromFragment();
//        }
    }

//    public void startActivityFromFragment() {
//
//    }

    /**
     * 注：在调用startActivity() API 之后，就不能 put数据了
     * @param context Context
     * @param needReturnResult true:需要被启动的Activity返回result数据
     * @param requestCode 如果needReturnResult 为true, 则需要启动Activity的请求码
     * @param extraOptionsBundle 附加的启动操作
     * 可能 抛出 android.content.ActivityNotFoundException
     */
    @SuppressLint("NewApi")
    public void startActivity(Context context, boolean needReturnResult, int requestCode, Bundle extraOptionsBundle) {
        if (context != null) {
            boolean isActivityContext = context instanceof Activity;
            if (needReturnResult && isActivityContext) {
                Activity theActivity = (Activity) context;
                if (extraOptionsBundle == null) {
                    theActivity.startActivityForResult(this,requestCode);
                }
                else {
                    if (Util.isCompateApi(16)) {
                        theActivity.startActivityForResult(this, requestCode, extraOptionsBundle);
                    }
                }
            }
            else {
                if (!isActivityContext) {
                    setFlags(FLAG_ACTIVITY_NEW_TASK);
                }
                if (extraOptionsBundle == null) {
                    context.startActivity(this);
                }
                else {
                    if (Util.isCompateApi(16)) {
                        context.startActivity(this, extraOptionsBundle);
                    }
                }
            }
        }
    }

    public ComponentName startService(Context context) {
        if (context != null) {
            return context.startService(this);
        }
        return null;
    }

    @RequiresApi(api = 26)
    public ComponentName startForegroundService(Context context) {
        if (context != null) {
            return context.startForegroundService(this);
        }
        return null;
    }


    public void sendBroadcast(Context context) {
        if (context != null) {
            context.sendBroadcast(this);
        }
    }
}
