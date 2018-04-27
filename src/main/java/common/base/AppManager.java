package common.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-11-15
 * Time: 14:00
 * DESC: app中对Activity的管理
 */
public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getMe() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack != null) {
            return activityStack.lastElement();
        }
        return null;
    }

    public void finishLatestActivity() {
        if (activityStack != null) {
            Activity latestActivity = activityStack.lastElement();
            finishActivity(latestActivity);
        }
    }
    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            if (activityStack != null) {
                activityStack.remove(activity);
            }
        }
    }

    public void removeStackedActivity(Activity toRemoveOne) {
        if (toRemoveOne != null) {
            if (activityStack != null) {
                activityStack.remove(toRemoveOne);
            }
        }
    }
    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (activityStack == null) {
            return;
        }
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack == null) {
            return;
        }
//        for (int i = 0, size = activityStack.size(); i < size; i++) {
//            if (null != activityStack.get(i)) {
//                activityStack.get(i).finish();
//            }
//        }
        //增加一个集合临时存储一下，避免在Activity one.finish()时，发生concurrentmodifyException
        Stack<Activity> temp = new Stack<>();
        temp.addAll(activityStack);
        for (Activity one : temp) {
            if (!one.isFinishing()) {
                one.finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void exitWholeApp(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
