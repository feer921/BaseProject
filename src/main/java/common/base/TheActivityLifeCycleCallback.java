package common.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import common.base.utils.CommonLog;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/10/27<br>
 * Time: 15:45<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */
@RequiresApi(api = 14)
public class TheActivityLifeCycleCallback implements Application.ActivityLifecycleCallbacks {
    protected final String TAG = getClass().getSimpleName();
    /**
     * 在前台的Activity数量
     */
    protected volatile int theForegroundActivityCount = 0;
    protected boolean LIFE_CIRCLE_DEBUG = true;

    protected volatile Activity theAppTopActivity;
    /**
     * APP进程内存在Activity的数量统计
     */
    protected int existActivityCount = 0;
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        existActivityCount ++;
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "-->onActivityCreated() activity = " + activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        theForegroundActivityCount++;
        theAppTopActivity = activity;
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG, "-->onActivityStarted() activity = " + activity );
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG, "-->onActivityResumed() activity = " + activity + " theAppTopActivity = " + theAppTopActivity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "-->onActivityPaused() activity = " + activity);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        theForegroundActivityCount--;
        if (LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG, "-->onActivityStopped() activity = " + activity);
        }
        theAppTopActivity = null;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "-->onActivitySaveInstanceState() activity = " + activity);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        existActivityCount--;
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "-->onActivityDestroyed() activity = " + activity);
        }
    }

    /**
     * APP内是否没有任何Activity在前台(即有可能在后台了)
     * @return true:没有Activity在前台了;false:有Activity有前台
     */
    public boolean isNoActivityInForeground() {
        return theForegroundActivityCount <= 0;
    }

    /**
     * 是否app进程内没有Activity存在了
     * @return true:没有Activity存在了;false:App进程内有Activity存在
     */
    public boolean isNoActivitiesExist() {
        return existActivityCount <= 0;
    }

    public int getExistActivityCount() {
        return existActivityCount;
    }
    /**
     * 获取app当前在栈顶的Activity
     * @return
     */
    public Activity getTheAppTopActivity() {
        return theAppTopActivity;
    }
}
