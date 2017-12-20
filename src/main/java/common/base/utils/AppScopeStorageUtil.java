package common.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * ******************(^_^)***********************<br>
 * User: 11776610771@qq.com<br>
 * Date: 2017/12/20<br>
 * Time: 20:11<br>
 * <P>DESC:
 * 对APP内部缓存区(APK安装后在系统内的缓存区：data/data/com.xx.xx/))下的缓存管理
 * 注：1、如果APK被卸载，则该缓存区
 *    2、在系统的【设置】界面的【应用管理】界面的某个APP管理界面时，如果选择【删除数据】则也会清除该缓存区
 * </p>
 * ******************(^_^)***********************
 */

public class AppScopeStorageUtil {
    private static final String TAG = "AppScopeStorageUtil";
    /**
     * 获取APP内部cache文件夹file
     * 注：该文件夹会在系统内存紧张的时候自动删除里面的缓存文件
     * @param context
     * @return File("data/data/com.xx.xx/cache/");
     */
    public static File appCacheDir(Context context) {
        return context.getCacheDir();
    }

    public static File getFileInAppCacheDir(Context context, String fileName) {
//        File file = new File(appCacheDir(context), fileName);
//        createFileIfNotExisted(file);
        return getFileUnderDirFile(appCacheDir(context), fileName);
    }

    public static File getDirInAppCacheDir(Context context, String dirName) {
//        File dirFile = new File(appCacheDir(context), dirName);
//        if (!dirFile.exists()) {
//            dirFile.mkdirs();
//        }
        return getDirUnderDirFile(appCacheDir(context), dirName);
    }
    public static File createFileIfNotExisted(File mayNeedCreatedFile) {
        if (mayNeedCreatedFile != null) {
            if (!mayNeedCreatedFile.getParentFile().exists()) {
                mayNeedCreatedFile.getParentFile().mkdirs();
            }
            if (!mayNeedCreatedFile.exists()) {
                try {
                    boolean isOptSuc = mayNeedCreatedFile.createNewFile();
                } catch (IOException e) {
                    CommonLog.e(TAG, "-->createFileIfNotExisted() " + mayNeedCreatedFile + "create occur " + e);
                }
            }
        }
        return mayNeedCreatedFile;
    }
    /**
     * 获取APP内部存储文件的根目录file
     * @param context
     * @return File("data/data/com.xx.xx/");
     */
    @SuppressLint("NewApi")
    public static File appDataRootDir(Context context) {
        if (Util.isCompateApi(24)) {
            return context.getDataDir();
        }
        return appCacheDir(context).getParentFile();
    }

    public static File getFileInRootDir(Context context, String fileName) {
        return getFileUnderDirFile(appDataRootDir(context), fileName);
    }

    public static File getDirInRootDir(Context context, String dirName) {
        return getDirUnderDirFile(appDataRootDir(context), dirName);
    }

    public static File getFileUnderDirFile(File dirFile, String fileName) {
        if (fileName != null) {
            File theFile = new File(dirFile, fileName);
            return createFileIfNotExisted(theFile);
        }
        return null;
    }

    public static File getDirUnderDirFile(File dirFile, String dirName) {
        if (dirName != null) {
            File theDirFile = new File(dirFile, dirName);
            if (!theDirFile.exists()) {
                theDirFile.mkdirs();
            }
            return theDirFile;
        }
        return null;
    }
    /**
     * 获取APP内部存储目录下的files文件夹file
     * @param context
     * @return File("data/data/com.xx.xx/files/");
     */
    public static File appFilesDir(Context context) {
        return context.getFilesDir();
    }

    public static File getFileInFilesDir(Context context, String fileName) {
//        File theFile = new File(appFilesDir(context), fileName);
//        createFileIfNotExisted(theFile);
        return getFileUnderDirFile(appFilesDir(context), fileName);
    }

    public static File getDirInFilesDir(Context context, String dirName) {
//        File dirFile = new File(appFilesDir(context), dirName);
//        if (!dirFile.exists()) {
//            dirFile.mkdirs();
//        }
        return getDirUnderDirFile(appFilesDir(context), dirName);
    }
    /**
     * 获取APP内部存储目录下的所填参数文件夹名的文件夹
     * 注：系统会自动在所传的参数前面加上"app_"前缀
     * @param context 上下文
     * @param dirName 要获取或者创建的文件夹名称
     * @return File("data/data/com.xx.xx/app_dirName");
     */
    public static File getDirInAppDataRootDir(Context context, String dirName) {
        return context.getDir(dirName, Context.MODE_PRIVATE);
    }
}
