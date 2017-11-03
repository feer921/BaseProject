package common.base.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.annotation.DimenRes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 普通工具类
 * <br/>
 * 2015年12月23日-下午2:31:43
 * @author lifei
 */
public class Util {

    private static final String TAG = "util";
    /**
     * 获取压缩文件里的bitmap
     * @param file
     * @param zipEntryName
     * @return
     */
    public static Bitmap transformZipEntryToBmp(File file, final String zipEntryName) {
        CommonLog.d(TAG, "get.name:" + file.getPath());
        Bitmap bmp = null;
        ZipInputStream in = null;
        try {
            BufferedInputStream br = new BufferedInputStream(new FileInputStream(file));
            in = new ZipInputStream(br);
            ZipEntry entry;

            while ((entry = in.getNextEntry()) != null) {
                if (entry.getName().contains(zipEntryName)) {
                    break;
                }
            }
            if (entry != null && in != null) {
                bmp = BitmapFactory.decodeStream(in);
            }
        } catch (Exception e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {

                }
            }
        }
        return bmp;
    }

    /* 输出线程ID，线程名，线程优先级等 */
    public static void printCurrentThreadId(String functionName, String tag) {
        CommonLog.d(tag, "In Function:" + functionName + "\nthread class = " + Thread.currentThread().getClass() + "\nthread name = "
                + Thread.currentThread().getName() + "\nthread id = " + Thread.currentThread().getId() + "\nthread priority  = "
                + Thread.currentThread().getPriority() + "\nthread state = " + Thread.currentThread().getState());
    }


    public static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }


    @SuppressLint("NewApi")
    public static void importSMS(Context mContext) {
        if (Build.VERSION.SDK_INT >= 19) {
            // Android 4.4
            // 及以上才有以下功能，并且可直接设置程序有写短信权限，如果以下运行有异常，则表示可能系统为Android5.0
            String PHONE_PACKAGE_NAME = "com.cx.huanji";
            PackageManager packageManager = mContext.getPackageManager();
            AppOpsManager appOps = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
            try {
                PackageInfo info = packageManager.getPackageInfo(PHONE_PACKAGE_NAME, 0);
                Field field = AppOpsManager.class.getDeclaredField("OP_WRITE_SMS");
                int WRITE_SMS = field.getInt(appOps);// 写短信权限
                Method method = AppOpsManager.class.getMethod("setMode", int.class, int.class, String.class, int.class);
                method.invoke(appOps, WRITE_SMS, info.applicationInfo.uid, PHONE_PACKAGE_NAME, AppOpsManager.MODE_ALLOWED);

            } catch (Exception ex) {
                // 表明该系统可能为Android5.0，且需要将程序设置成默认的短信程序
//                Util.saveBoolean(mContext, "_defaultSMS", true);
            }
        }
    }

    @SuppressLint("NewApi")
    public static void defaultSMS(Context mContext) {
//        if (Util.readBoolean(mContext, "_defaultSMS", false)) {
            String myPackageName = mContext.getPackageName();
            if (!Telephony.Sms.getDefaultSmsPackage(mContext).equals(myPackageName)) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, mContext.getPackageName());
                mContext.startActivity(intent);
            }
//        }
    }

//    @SuppressLint("NewApi")
//    public static boolean getDefaultSMS(Context mContext) {
//        if (Util.readBoolean(mContext, "_defaultSMS", false)) {
//            final String myPackageName = mContext.getPackageName();
//            if (Telephony.Sms.getDefaultSmsPackage(mContext).equals(myPackageName))
//                return true;
//            return false;
//        }
//        else {
//            return true;
//        }
//    }

    /**
     * 获取联系人数量
     *
     * @param mContext
     * @return
     */
    public static int getContactVal(Context mContext) {
        int val = 0;
        ContentResolver conResolver = mContext.getContentResolver();
        if (null == conResolver) {
            return val;
        }
        try {
            Cursor curcontact = conResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (curcontact != null && curcontact.getCount() > 0) {
                val = curcontact.getCount();
            }
            if (curcontact != null) {
                curcontact.close();
            }
        } catch (Exception e) {
            CommonLog.i("Exception_Contect", e + "");
        }

        return val;
    }



    /**
     * 获取短信数量
     *
     * @param mContext
     * @return
     */
    public static int getSmsVal(Context mContext) {
        int val = 0;
        ContentResolver conResolver = mContext.getContentResolver();
        if (null == conResolver) {
            return val;
        }
        try {
            Cursor cursms = conResolver.query(Uri.parse("content://sms/"), null, null, null, null);
            // Cursor cursmsdraft = conResolver.query(
            // Uri.parse("content://sms/draft"), null, null, null, null);
            if (cursms != null && cursms.getCount() > 0) {
                int count = cursms.getCount();
                // if (cursmsdraft.getCount() != 0) {
                // count = count - cursmsdraft.getCount();
                // }
                val = count;
            }
            if (cursms != null) {
                cursms.close();
            }
            // if (cursmsdraft != null) {
            // cursmsdraft.close();
            // }
        } catch (Exception e) {
            CommonLog.i("Exception_Sms", e + "");
        }

        return val;
    }

    /**
     * 获取图片媒体库数量
     *
     * @param mContext
     * @return
     */
    public static int getImageVal(Context mContext) {
        int val = 0;
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            val = cursor.getCount();
            cursor.close();
        }
        return val;
    }
    /**
     * 外置SDCard是否可用[一般来讲始终会返回true]
     * @return
     */
    public static boolean sdCardIsAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * sdcard是否有足够的空间
     *
     * @return
     */
    public static boolean sdCardHasEnoughSpace() {
        if (!sdCardIsAvailable()) {
            return false;
        }
        long realSize = getRealSizeOnSdCard();
        return realSize <= 0;
    }

    private static long getRealSizeOnSdCard() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        StatFs statfs = new StatFs(file.getPath());
        long blockSize = statfs.getBlockSize();
        long availableBlocks = statfs.getAvailableBlocks();
        return blockSize * availableBlocks;
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.toString() == null || str.toString().trim()
                .length() == 0 || str.length() == 0 || "null".equalsIgnoreCase(str.toString())) {
            return true;
        } else {
        }
            return false;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        float supplementValue = scale / 10;//如果是3，则0.3补充如果是N，则0.N补充
        return (pxValue / scale + supplementValue);
    }

    public static int getDimenResPixelSize(Context context,@DimenRes int dimeResId) {
        return context.getResources().getDimensionPixelSize(dimeResId);
    }
    public static Bitmap bytes2Bimap(byte[] b) {
        if (b != null && b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * <p>
     * 判断传入电话号码 是否合法
     * </p>
     *
     * @param phoneNumber
     *            指定的字符串
     *
     * @return 如果为电话号码合法 则为true 否则非false
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        Pattern p = Pattern.compile("1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}");
        Matcher m = p.matcher(phoneNumber);
        while (m.find()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 隐式启动对应action的Service 兼容Android5.0的不能显示通过action方式启动Service
     *
     * @param mContext
     * @param action
     */
    public static void startImpliedService(Context mContext, String action) {
        if (null == action)
            return;
        Intent targetIntent = getServiceIntentCompatibledApi20(mContext, action);
        mContext.startService(targetIntent);
    }

    /**
     * 通过携带相应action且带有其他信息内容的原Intent隐式启动对应action的Service
     * @param mContext
     * @param actionIntent
     */
    public static void startImpliedService(Context mContext, Intent actionIntent) {
        if (actionIntent == null) {
            return;
        }
        actionIntent = getServiceIntentCompatibledApi20(mContext, actionIntent);
        mContext.startService(actionIntent);
    }

    /**
     * 获取对应action的Service的Intent
     *
     * @param mContext
     * @param serviceAction
     * @return
     */
    public static Intent getServiceIntentCompatibledApi20(Context mContext, String serviceAction) {
        Intent actionIntent = new Intent(serviceAction);
        if (Build.VERSION.SDK_INT < 19) {// android 5.0以下
            return actionIntent;
        }
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> resultServices = pm.queryIntentServices(actionIntent, 0);
        if (resultServices == null) {
            return actionIntent;
        }
        int size = resultServices.size();
        if (size == 0) {
            return actionIntent;
        }
        ResolveInfo theFirstService = resultServices.get(0);
        String pkgName = theFirstService.serviceInfo.packageName;
        String className = theFirstService.serviceInfo.name;
        ComponentName componentName = new ComponentName(pkgName, className);
        actionIntent.setComponent(componentName);
        return actionIntent;
    }

    public static Intent getServiceIntentCompatibledApi20(Context mContext, Intent actionIntent) {
        if (Build.VERSION.SDK_INT < 19) {// android 5.0以下
            return actionIntent;
        }
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> resultServices = pm.queryIntentServices(actionIntent, 0);
        if (resultServices == null) {
            return actionIntent;
        }
        int size = resultServices.size();
        if (size == 0) {
            return actionIntent;
        }
        ResolveInfo theFirstService = resultServices.get(0);
        String pkgName = theFirstService.serviceInfo.packageName;
        String className = theFirstService.serviceInfo.name;
        ComponentName componentName = new ComponentName(pkgName, className);
        actionIntent.setComponent(componentName);
        return actionIntent;
    }

    /**
     * 调试追踪信息，记录到本地文件 注：正式发版时将变量CxConfig.ALLOW_DEBUG_TRACE 置为false;
     * @param mContext
     * @param message
     */
    public static synchronized void debugTraceLog(Context mContext, String message) {
        if (!CommonConfigs.DEBUG_TRACE_FLAG) {
            return;
        }
        if (mContext == null) {
            mContext = NetHelper.getAppContext();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String oneDayAsFileName = dateFormat.format(new Date());
        File logFile = StorageUtil.getFileInCache(mContext, CommonConfigs.DEBUG_TRACE_DIR_NAME + mContext.getPackageName()
                +
//                CommonConfigs.DEBUG_TRACE_FILE_NAME
                "/"+
                //changed : 以天为文件名
                oneDayAsFileName
        );
        if (!logFile.exists()) {
            return;
        }
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(logFile, true));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            Date now = new Date();
            String dateInfo = sdf.format(now) + " --- ";
            pw.println(dateInfo + message);
            pw.flush();
            pw.close();
        } catch (Exception e) {
            CommonLog.e("info", e);
        }
    }

    /**
     * 是否向下兼容某个系统版本
     * @param apiLevle 要兼容的某个版本
     * @return true:兼容，即当前系统版本大于或者等于目标要兼容的版本；false：不兼容，即当前系统版本小于目标版本
     */
    public static boolean isCompateApi(int apiLevle){
        if(apiLevle <= 0) return  false;
        if(apiLevle <= Build.VERSION.SDK_INT){
            return true;
        }
        return false;
    }

    /**
     * 查询指定服务是否在运行状态
     * @param mContext
     * @param serviceClass 要查询的Service的Class对象; 注：该Class类必须全路径(完整包类名)
     * @return true:在运行状态，false:不在运行状态或者未查询出
     */
    public static boolean queryServiceIsRunning(Context mContext,Class<?> serviceClass){
        if(serviceClass == null){
            return false;
        }
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(Integer.MAX_VALUE);
        if(runningServiceInfos == null || runningServiceInfos.isEmpty()){
            return false;
        }
        for (ActivityManager.RunningServiceInfo curService : runningServiceInfos) {
            ComponentName componentName = curService.service;
            if(componentName.getClassName().equals(serviceClass.getName())){
                return true;
            }
        }
        return false;
    }

    public static void queryActionService(Context mContext, Intent actionIntent) {
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> intentServiceInfos = pm.queryIntentServices(actionIntent, PackageManager.GET_SERVICES);
        if(intentServiceInfos == null || intentServiceInfos.isEmpty()){
            return;
        }
        for (ResolveInfo resolveInfo : intentServiceInfos) {
//            ComponentName
        }
    }

    /**
     * 从SharedPreferences文件中获取数据
     * @param context
     * @param sharedPreferFileName
     * @param preferKey
     * @param defValue
     * @param <T>
     * @return
     */
    @SuppressLint("NewApi")
    public static <T> T sharedPreferenceGetData(Context context, String sharedPreferFileName, String preferKey, T defValue) {
        T resultData = null;
        if (isEmpty(sharedPreferFileName) || isEmpty(preferKey)) {
            return resultData;
        }
        SharedPreferences sp = context.getSharedPreferences(sharedPreferFileName, Context.MODE_PRIVATE);
        if (defValue == null) {
            resultData = (T) sp.getString(preferKey, null);
        }
        else{
            switch (defValue.getClass().getSimpleName()) {
                case "String":
                    resultData = (T) sp.getString(preferKey, (String) defValue);
                    break;
                case "Integer":
                    Integer defValueInt = (Integer) defValue;
                    resultData = (T) (defValueInt = sp.getInt(preferKey, defValueInt));
                    break;
                case "Boolean":
                    Boolean defBoolean = (Boolean) defValue;
                    resultData = (T) (defBoolean = sp.getBoolean(preferKey, defBoolean));
                    break;
                case "Float":
                    Float defFloat = (Float) defValue;
                    resultData = (T) (defFloat = sp.getFloat(preferKey, defFloat));
                    break;
                case "Long":
                    Long defLong = (Long) defValue;
                    resultData = (T) (defLong = sp.getLong(preferKey, defLong));
                    break;
                case "Set":
                    try {
                        Set<String> defSetStr = (Set<String>) defValue;
                        if(isCompateApi(11))
                        resultData = (T) (defSetStr = sp.getStringSet(preferKey, defSetStr));
                    } catch (Exception e) {
                    }
                    break;
            }
        }
        return resultData;
    }

    @SuppressLint("NewApi")
    public static void saveData2SharedPreferences(Context context, String spFileName, String preferKey, Object valueData) {
        if (isEmpty(spFileName) || isEmpty(preferKey)) {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (valueData == null) {
            editor.putString(preferKey, null);
        }
        else{
            if (valueData instanceof String) {
                editor.putString(preferKey, (String) valueData);
            } else if (valueData instanceof Integer) {
                editor.putInt(preferKey, (Integer) valueData);
            } else if (valueData instanceof Boolean) {
                editor.putBoolean(preferKey, (Boolean) valueData);
            } else if (valueData instanceof Float) {
                editor.putFloat(preferKey, (Float) valueData);
            } else if (valueData instanceof Set) {
                if(isCompateApi(11))
                editor.putStringSet(preferKey, (Set<String>) valueData);
            } else if (valueData instanceof Long) {
                editor.putLong(preferKey, (Long) valueData);
            }
//            switch (valueData.getClass().getSimpleName()) {
//                case "String":
//                    editor.putString(preferKey, (String) valueData);
//                    break;
//                case "Integer":
//                    editor.putInt(preferKey, (Integer) valueData);
//                    break;
//                case "Long":
//                    editor.putLong(preferKey, (Long) valueData);
//                    break;
//                case "Float":
//                    editor.putFloat(preferKey, (Float) valueData);
//                    break;
//                case "Boolean":
//                    editor.putBoolean(preferKey, (Boolean) valueData);
//                    break;
//                case "Set":
//                    if(isCompateApi(11))
//                    editor.putStringSet(preferKey, (Set<String>) valueData);
//                    break;
//            }
        }
        editor.commit();
    }
    @SuppressLint("NewApi")
    public static void copyTextToClipboard(Context context, CharSequence toCopyChars) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData textClipData = ClipData.newPlainText("text", toCopyChars);
        clipboardManager.setPrimaryClip(textClipData);
    }

    /**
     * 使用中间的字符连接前后两个字符串. eg.: AStr--Bstr,"--"即为中间的连接字符
     * @param frontStr
     * @param behindStr
     * @param concatChars eg.:前后两字符串需要换行，则连接字符为:"\n"
     * @return
     */
    public static String concat2StrWithChars(String frontStr, String behindStr, String concatChars) {
        if (isEmpty(frontStr)) {
            return behindStr;
        }
        if (isEmpty(behindStr)) {
            return frontStr;
        }
        return frontStr + concatChars + behindStr;
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
