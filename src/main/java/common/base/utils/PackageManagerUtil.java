package common.base.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 包管理工具，主要为获取项目清单AndroidManifest.xml中各信息
 * <br/>
 * 2015年12月23日-下午2:43:07
 * @author lifei
 */
public class PackageManagerUtil {
    private static final String TAG = PackageManagerUtil.class.getSimpleName();

    /**
     * 获取AndroidManifest中指定的meta-data字符串
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getStringMetaData(Context ctx, String key) {
        if (ctx == null || Util.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (NameNotFoundException e) {
            CommonLog.e(TAG, "", e);
        }

        return resultData;
    }

    /**
     * 获取AndroidManifest中指定的meta-data整形值
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回默认值
     */
    public static int getIntMeta(final Context context, final String metaName, final int defaultValue) {
        int meta = defaultValue;
        try {
            ApplicationInfo appinfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appinfo != null) {
                meta = appinfo.metaData.getInt(metaName, defaultValue);
            }
        } catch (Exception e) {
            CommonLog.e(TAG, "", e);
        }
        return meta;
    }

    /**
     * 获取AndroidManifest中指定版本号整形值
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为0
     */
    public static int getPackageVersionCode(Context context) {
        int verCode = 0;
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            if (null != pi.versionName) {
                verCode = pi.versionCode;
            }
        } catch (NameNotFoundException e) {
            CommonLog.e(TAG, "", e);
        }
        return verCode;
    }

    /**
     * 获取AndroidManifest中指定的版本号名字符串
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为""
     */
    public static String getPackageVersionName(Context context) {
        String verName = "";
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            if (null != pi.versionName) {
                verName = pi.versionName;
            }
        } catch (NameNotFoundException e) {
            CommonLog.e(TAG, "", e);
        }
        return verName;
    }

    /**
     * 收集设备参数信息,返回json对象
     * @param ctx
     */
    public static JSONObject collectDeviceInfo(Context ctx) {
        JSONObject result = new JSONObject();
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                result.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                CommonLog.d(TAG, "an error occured when collect crash info " + e.getMessage());
            }
        }

        return result;
    }

    /**
     * 程序是否在前台
     * 
     * @param context
     */
    public static boolean isAppOnForeground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            for (RunningAppProcessInfo appProcess : appProcesses) {
                // The name of the process that this object is associated with.
                if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 退出应用
     * @param context
     */
    public static void exitApp(final Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (context.getPackageName().equals(service.service.getPackageName())) {
                Intent stopIntent = new Intent();
                ComponentName serviceCMP = service.service;
                stopIntent.setComponent(serviceCMP);
                context.stopService(stopIntent);
                // new AppRecManager(context).unregisterListener();
                break;
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 判断apk是否被安装
     * @param context
     * @param packageName 对应APK的包名
     * @return
     */
    public static boolean checkApkExist(Context context, String packageName) {
        boolean result = false;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                result = true;
            }
        } catch (NameNotFoundException e) {
            CommonLog.e(TAG, TAG + "--->Exception:" + e.getMessage());
        }
        return result;
    }

    /**
     * 检查apk版本
     * 
     * @param context
     * @param packageName
     * @return
     */
    public static int getVersionCode(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return 0;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }

    }

    public static String getpackname(PackageInfo packageinfo) {
        if (packageinfo == null)
            return null;
        String packname = packageinfo.packageName;
        return packname;

    }

    /**
     * 获取手机内部安装的非系统应用 只有基本信息，不包含签名等特殊信息
     * @param context
     * @return
     */
    public static List<PackageInfo> getInstallApps(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> result = new ArrayList<PackageInfo>();
        List<PackageInfo> list = packageManager.getInstalledPackages(0);// 获取手机内所有应用
        for (PackageInfo packageInfo : list) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) { // 判断是否为非系统预装的应用程序
                result.add(packageInfo);
            }
        }

        return result;
    }

    /**
     * 为程序创建桌面快捷方式
     */
//    public static void addShortCut(Context mContext,Class<?> targetClass) {
//        if (!Util.readBoolean(mContext, "shortcut_flag_icon", false)) {
//            Util.saveBoolean(mContext, "shortcut_flag_icon", true);
//            CommonLog.d("shortcut", "first create successfull");
//        } else {
//            CommonLog.d("shortcut", "no created");
//            return;
//        }
//
//        String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
//        Intent intent = new Intent();
//        intent.setClass(mContext, targetClass);
//        intent.setAction("android.intent.action.MAIN");
//        intent.addCategory("android.intent.category.LAUNCHER");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//
//        Intent addShortcut = new Intent(ACTION_ADD_SHORTCUT);
//        Parcelable icon = Intent.ShortcutIconResource.fromContext(mContext, R.drawable.ic_launcher);// 获取快捷键的图标
//        addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, mContext.getString(R.string.app_name));// 快捷方式的标题
//        addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);// 快捷方式的动作
//        addShortcut.putExtra("duplicate", false);// 不允许重复创建
//        addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
//        mContext.sendBroadcast(addShortcut);// 发送广播
//    }

    /**
     * 检查当前进程名是否是默认的主进程
     * 
     * @param context
     * @return
     */
    public static boolean checkMainProcess(Context context) {
        final String curProcessName = getProcessNameByPid(context, android.os.Process.myPid());
        return isMainProcess(context, curProcessName);
    }

    /**
     * 判断进程是否为主进程
     * 
     * @param context
     * @param processName
     *            进程名称（包名+进程名）
     * @return
     */
    public static boolean isMainProcess(Context context, String processName) {
        final String mainProcess = context.getApplicationInfo().processName;
        return mainProcess.equals(processName);
    }

    /**
     * 获取当前进程的名称<br/>
     * 通过当前进程id在运行的栈中查找获取进程名称（包名+进程名）
     * 
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        final int curPid = android.os.Process.myPid();
        String curProcessName = getProcessNameByPid(context, curPid);
        if (null == curProcessName) {
            curProcessName = context.getApplicationInfo().processName;
            CommonLog.d(TAG, "getCurProcessName,no find process,curPid=", curPid, ",curProcessName=", curProcessName);
        }
        return curProcessName;
    }

    /**
     * 获取进程的名称<br/>
     * 通过进程id在运行的栈中查找获取进程名称（包名+进程名）
     * 
     * @param context
     * @param pid
     * @return
     */
    public static String getProcessNameByPid(Context context, final int pid) {
        String processName = null;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            for (RunningAppProcessInfo appProcess : appProcesses) {
                if (pid == appProcess.pid) {
                    processName = appProcess.processName;
                    break;
                }
            }
        }
        CommonLog.d(TAG, "getProcessNameByPid,pid=", pid, ",processName=", processName);
        return processName;
    }

    /**
     * 检查当前进程名是否是包含进程名的进程
     * 
     * @param context
     * @return
     */
    public static boolean checkTheProcess(final Context context, String endProcessName) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        int myPid = android.os.Process.myPid();
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            for (RunningAppProcessInfo appProcess : appProcesses) {
                if (myPid == appProcess.pid) {
                    CommonLog.d(TAG, "process.pid appProcess.processName=" + appProcess.processName + ", endProcessName=" + endProcessName);
                    if (appProcess.processName.endsWith(endProcessName)) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前堆栈中的第一个activity
     * @param context
     * @return
     */
    public static ComponentName getTheProcessBaseActivity(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        RunningTaskInfo task = activityManager.getRunningTasks(1).get(0);
        if (task.numActivities > 0) {
            CommonLog.d(TAG, "runningActivity topActivity=" + task.topActivity.getClassName());
            CommonLog.d(TAG, "runningActivity baseActivity=" + task.baseActivity.getClassName());
            return task.baseActivity;
        }

        return null;
    }
}
