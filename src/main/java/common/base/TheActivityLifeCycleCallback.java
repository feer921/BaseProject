package common.base;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.RequiresApi;
import common.base.utils.CommonLog;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/10/27<br>
 * Time: 15:45<br>
 * <P>DESC:
 * 针对APP内所有Activity的生命周期的回调
 * 扩展做些处理，可以直接继承再扩展相关功能
 * </p>
 * ******************(^_^)***********************
 */
@RequiresApi(api = 14)
public class TheActivityLifeCycleCallback implements Application.ActivityLifecycleCallbacks {
    protected final String TAG = getClass().getSimpleName();
    /**
     * 在前台的Activity数量
     */
    protected int theForegroundActivityCount = 0;
    protected boolean LIFE_CIRCLE_DEBUG = true;
//    /**
//     * 栈顶Activity
//     */
//    protected Activity theAppTopActivity;
    /**
     * APP进程内存在Activity的数量统计
     */
    protected int existActivityCount = 0;

    @CallSuper //基类有逻辑处理，则需要调用本基类
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        existActivityCount++;
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "-->onActivityCreated() activity = " + activity);
        }
    }

    @CallSuper //基类有逻辑处理，则需要调用本基类
    @Override
    public void onActivityStarted(Activity activity) {
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG, "-->onActivityStarted() activity = " + activity );
        }
    }

    /**
     * 要验证一下是否，代码主动调用Activity的onResume()会不会也执行该回调 经验证：如果代码主动调用onResume()会回调本方法
     * @param activity
     */
    @CallSuper //基类有逻辑处理，则需要调用本基类
    @Override
    public void onActivityResumed(Activity activity) {
        theForegroundActivityCount++;
//        theAppTopActivity = activity;
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG, "-->onActivityResumed() activity = " + activity /*+ " theAppTopActivity = " + theAppTopActivity*/);
        }
        onForegroundActivityCountCallback(theForegroundActivityCount);
    }
    @CallSuper //基类有逻辑处理，则需要调用本基类
    @Override
    public void onActivityPaused(Activity activity) {
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "-->onActivityPaused() activity = " + activity);
        }
        theForegroundActivityCount--;//这里只记录把数量减，但不回调
//        if (activity == theAppTopActivity) {
//            theAppTopActivity = null;
//        }
//        onForegroundActivityCountCallback(theForegroundActivityCount);
    }

    @CallSuper //基类有逻辑处理，则需要调用本基类
    @Override
    public void onActivityStopped(Activity activity) {
        //removed by fee 2019-06-14: 数量的记录移至 onActivityPaused()方法，因为有些场景下(比如当前Activity被半透明的Activity遮住
        // 并不会调用 onStop(),但是半透明的Activity销毁后下面的Activity却仍会执行onResume()造成多记录了次数，所以需要 onPause()时减，然后成对的onResume()时+)
//        theForegroundActivityCount--;
        if (LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG, "-->onActivityStopped() activity = " + activity);
        }
//        if (activity == theAppTopActivity) {
//            theAppTopActivity = null;
//        }
        onForegroundActivityCountCallback(theForegroundActivityCount);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "-->onActivitySaveInstanceState() activity = " + activity);
        }
    }

    @CallSuper //基类有逻辑处理，则需要调用本基类
    @Override
    public void onActivityDestroyed(Activity activity) {
        boolean isMarkedFinish = false;
        Intent theFlagIntent = activity.getIntent();
        if (theFlagIntent != null) {
            isMarkedFinish = theFlagIntent.getBooleanExtra("onActivityFinish", false);
        }
        if (!isMarkedFinish) {
            existActivityCount--;
        }
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "-->onActivityDestroyed() activity = " + activity + " existActivityCount = " + existActivityCount +" isMarkedFinish = "+isMarkedFinish);
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
//    /**
//     * 获取app当前在栈顶的Activity
//     * @return
//     */
//    public Activity getTheAppTopActivity() {
//        return theAppTopActivity;
//    }

    /**
     * added by fee 2019-05-16:由于{#android.app.Application.ActivityLifecycleCallbacks}该接口中没有finish()
     * 的回调，则主动加一个
     * 另由于Activity的onDestroy()方法回调不及时，会导致本类统计的existActivityCount 不准确，所以在finish()方法中统计
     * 更准确一点
     */
    @CallSuper //基类有逻辑处理，则需要调用本基类
    public void onActivityFinish(Activity activity) {
        Intent intentFlag = activity.getIntent();//一般不为null
        boolean needAdd = false;
        if (intentFlag == null) {
            intentFlag = new Intent();
            needAdd = true;
        }
        intentFlag.putExtra("onActivityFinish", true);
        if (needAdd) {
            activity.setIntent(intentFlag);
        }
        existActivityCount--;
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "-->onActivityFinish() activity = " + activity);
        }
//        if (activity == theAppTopActivity) {
//            theAppTopActivity = null;
//        }
    }

//    /**
//     * added by fee 2019-05-16:由于{@link android.app.Application.ActivityLifecycleCallbacks}该接口中没有onRestart()
//     * 的回调，则主动加一个
//     * 该回调更能精确的认为当前栈顶的Activity,不回调这个方法前，使用{@link #onActivityResumed(Activity)}
//     */
//    @CallSuper //基类有逻辑处理，则需要调用本基类
//    public void onActivityRestart(Activity activity) {
//        theAppTopActivity = activity;
//        theForegroundActivityCount++;
//        if (LIFE_CIRCLE_DEBUG) {
//            CommonLog.i(TAG, "-->onActivityRestart() activity = " + activity);
//        }
//        onForegroundActivityCountCallback(theForegroundActivityCount);
//    }

    /**
     * 回调当前APP内 在前台的Activity的数量
     * 注：此处为空实现，子类可以实现用来，满足要关注当前APP 已经退出到后台运行的功能
     * eg.: 类似一些APP提示"当前应用已在后台运行"
     * @param theForegroundActivityCounts 当前前台Activity的数量
     */
    protected void onForegroundActivityCountCallback(int theForegroundActivityCounts) {

    }

    public void enableLog(boolean enable) {
        LIFE_CIRCLE_DEBUG = enable;
    }
}
