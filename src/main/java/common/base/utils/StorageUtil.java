package common.base.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 路径存储工具类
 * <br/>
 * 2015年12月23日-下午1:50:48
 * @author lifei
 */
public final class StorageUtil {
    private static final String TAG = "StorageUtil";

    /**
     * 获取默认的外部存储目录
     * @return eg:/mnt/sdcard1/
     */
    public static String getExternalStorageDirectoryPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取默认的外部存储目录File
     * @return
     */
    public static File getExternalStorageDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    /**
     * 获取SD卡根目录或者cache目录 File
     * 获取一个程序的缓存根目录的父文件夹File
     * @param context Context
     * @return File("/mnt/storage0/") if the phone has SD card,else return
     *         File("data/data/[包名]/cache/")
     */
    public static File getCacheParent(Context context) {
        File externalStorageFile = getExternalStorageDirectory();
        if (null == externalStorageFile) {
            // return context.getDir(Config.APP_BASE_PATH,
            // Context.MODE_WORLD_WRITEABLE);
            return context.getCacheDir();//该目录下的文件在系统内存紧张时，会被清空文件，来腾出空间供系统使用
//            return context.getFilesDir(); //File("data/data/[包名]/files/")//卸载会被删除，其他的状况不会
        } else {
            return externalStorageFile;
        }
    }

    /**
     * 获取一个程序缓存的根目录[该目录为程序全局自定义配置类配置],此处说明指示为approot
     * approot @see {@link CommonConfigs#APP_BASE_PATH}}
     * @param context Context
     * @return 有SD卡： File("/mnt/storage0/approot_dir/");
     *         无SD卡 ：File("/data/data/[包名]/cache/approot_dir/");
     */
    public static File getAppCacheRoot(Context context) {
        return getDirInCache(context, CommonConfigs.APP_BASE_PATH);
    }

    /**
     * 获取一个程序缓存根目录的路径String,注意该路径已带"/"
     * @param context Context
     * @return eg. "/mnt/sdcard0/approotdir/"; 
     *              or:"/data/data/[包名]/cache/自定义缓存根目录/";
     */
    public static String getAppCacheRootPath(Context context) {
        String path = getAppCacheRoot(context).getPath();
        if (!TextUtils.isEmpty(path) && !path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        return path;
    }

    /**
     * 1：如果没有SD卡，获取的是data/data/[包名]/cache/文件夹下的各文件夹文件
     * 2：如果有SD卡，则获取的是eg.: /mnt/storage0/ 即SD卡根目录下任何文件夹 
     * 3：注：只能获取文件夹文件File对象，不能传入具体文件名eg.: a.text,传入文件名，仍会被创建成文件夹
     * @param context Context
     * @param dirName 任何"文件夹"名字
     * @return 无SD卡：eg.: File("/data/data/[包名]/cache/[参数：dirName]/");
     *         有SD卡：eg.: File("/mnt/storage0/[参数：dirName]/");
     */
    public static File getDirInCache(Context context, final String dirName) {
        File dir = new File(getCacheParent(context), dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 1:如果没有外置SD卡，则获取的是各程序缓存根目录下的各文件
     * 此时返回的是：File("/data/data/[包名]/cache/xx.txt")
     * 2:如果有外置SD卡，则获取的是外置SD卡根目录下任何的文件
     * @param context Context
     * @param fileName 相对于存储根路径的目录下的"文件名"
     * @return 如果没有外置SD卡 eg.: File("/data/data/com.cx/xx/cache/[参数：fileName] ");
     *         如果有SD卡 eg.: File("/mnt/storage0/[参数：fileName]"),but the file
     *         maybe created fail possibly;
     */
    public static File getFileInCache(Context context, final String fileName) {
        File file = new File(getCacheParent(context), fileName);
        createFileIfNotExisted(file);
        return file;
    }

    /**
     * 获取本程序自定义储存根目录approotdir 下的各文件File
     *
     * eg.: 如果有SD卡，则为File("/mnt/sdcard/approotdir/fileName");
     *      如果没有SD卡，则为File("/data/data/com.xx.xx/cache/approotdir/fileName");
     * @param mContext
     * @param fileName eg.: /ads/pic.data/; 或者/user.data; 即可多层级
     * @return
     */
    public static File getFileInAppBaseDir(Context mContext,String fileName){
                            // mnt/sdcard/appbasedir/, fileName)
        File file = new File(getAppCacheRoot(mContext),fileName);
        createFileIfNotExisted(file);
        return file;
    }
    /**
     * 获取一个程序缓存根目录“/approot/”目录下 能匹配到对应匹配规则的所有文件夹 eg. 找出全是以数字命名的文件夹
     * @param context
     * @param dirNameRegex
     *            匹配正则，eg. 是数字的文件夹
     * @return
     */
    public static File[] getRegexFiles(Context context, final String dirNameRegex) {
        return getAppCacheRoot(context).listFiles(new FileFilter() {
            Pattern mPattern = Pattern.compile(dirNameRegex, Pattern.CASE_INSENSITIVE);

            @Override
            public boolean accept(File pathname) {
                if (!pathname.isDirectory()) {
                    return false;
                }
                String dirName = pathname.getName();
                return mPattern.matcher(dirName).find();
            }
        });
    }

    /**
     * @deprecated 声明过期，因为不同的系统可能路径不同
     * @return 本意为获取存储拍照图片的缩略图的文件夹路径
     */
    public static String getThumbnailsDir() {
        return Environment.getExternalStorageDirectory() + "/DCIM/.thumbnails";
    }

    /**
     * 有SD卡情况下：File("/storage/emulated/0/Android/data/应用包名/cache/")
     * 没有SD卡情况下：File("/data/data/应用包名/files")
     * @param context 上、下文
     * @return 无需申请存储权限的公共缓存路径；或者 app内部的files路径
     */
    public static File getAppCacheRootDirWithOutPermission(Context context) {
        if (getExternalStorageDirectory() == null) {
            //没有SD卡的情况下
            return context.getFilesDir();
        }
        File externalCacheDir = context.getExternalCacheDir();
        if (externalCacheDir == null) {//有些系统可能获取到的是null???
            externalCacheDir = context.getFilesDir();
        }
        return externalCacheDir;
    }

    public static File getFileInExternalCacheDir(Context context, String fileName) {
        File targetFile = new File(getAppCacheRootDirWithOutPermission(context), fileName);
        createFileIfNotExisted(targetFile);
        return targetFile;
    }

    /**
     * 获取系统外部存储模块的公用cache目录下 任何的文件夹File
     * @param context context
     * @param dirName 要创建的文件夹名
     * @return  有SD卡情况下：File("/storage/emulated/0/Android/data/应用包名/cache/dirName")
     * 没有SD卡情况下：File("/data/data/应用包名/files/dirName")
     */
    public static File getDirInExternalCacheDir(Context context, String dirName) {
        File targetDirFile = new File(getAppCacheRootDirWithOutPermission(context), dirName);
        if (!targetDirFile.exists()) {
            targetDirFile.mkdirs();
        }
        return targetDirFile;
    }

    /**
     * 获取一个文件、或者一个文件夹下所有的文件所占用的内存大小
     * @param pathFile 某个文件或者文件夹
     * @return 参数pathFile 字节大小或者该文件夹下所有文件的占用字节大小
     */
    public static long getFileTotalSize(File pathFile) {
        long totalFileSize = 0;
        if (pathFile != null && pathFile.exists()) {
            if (pathFile.isFile()) {
                totalFileSize = pathFile.length();
            }
            else if(pathFile.isDirectory()){
                File[] subFiles = pathFile.listFiles();
                for (File theSubFile : subFiles) {
                    totalFileSize += getFileTotalSize(theSubFile);
                }
            }
        }
        return totalFileSize;
    }

    /**
     * 如果一个文件不存在，创建该文件
     * @param mayNeedCreatedFile 可能需要创建的文件
     * @return 传入的参数自身
     */
    public static File createFileIfNotExisted(File mayNeedCreatedFile) {
        if (mayNeedCreatedFile != null) {
            File itsParentFile = mayNeedCreatedFile.getParentFile();
            if (itsParentFile!= null && !itsParentFile.exists()) {
                boolean mkOk = itsParentFile.mkdirs();
                CommonLog.e(TAG, "--> createFileIfNotExisted() mkOk = " + mkOk + " mayNeedCreatedFile = " + mayNeedCreatedFile);
            }
            if (!mayNeedCreatedFile.exists()) {
                try {
                    boolean isOptSuc = mayNeedCreatedFile.createNewFile();
                    CommonLog.d(TAG, "--> createFileIfNotExisted() createNewFile isOptSuc = " + isOptSuc,
                            " mayNeedCreatedFile = " + mayNeedCreatedFile);
                } catch (Exception e) {
                    CommonLog.e(TAG, "-->createFileIfNotExisted() " + mayNeedCreatedFile + "create occur: " + e);
                }
            }
        }
        return mayNeedCreatedFile;
    }

    /**
     * 追加文件夹文件的路径"/"目录符号
     * @param dirFile 可以是目录File也可以是文件File
     * @return 如果参数是文件夹："xxx/xxx/xx/"; 如果参数是文件:"xxx/xx/xx.txt"
     */
    public static String appendDirSeparator(File dirFile) {
        String filePathStr = dirFile.getAbsolutePath();
        if (dirFile.isDirectory()) {
            if (!filePathStr.endsWith(File.separator)) {
                filePathStr += File.separator;
            }
        }
        return filePathStr;
    }
//    //**************************** 添加只对APP内部缓存区(APK安装后在系统内的缓存区：data/data/com.xx.xx/))下的缓存管理,如果APK被卸载，则该缓存区被删除 *************//
//
//    /**
//     * 获取APP内部cache文件夹file
//     * 注：该文件夹会在系统内存紧张的时候自动删除里面的缓存文件
//     * @param context
//     * @return File("data/data/com.xx.xx/cache/");
//     */
//    public static File appCacheDir(Context context) {
//        return context.getCacheDir();
//    }
//
//    public static File getFileInAppCacheDir(Context context, String fileName) {
//        File file = new File(appCacheDir(context), fileName);
//        createFileIfNotExisted(file);
//        return file;
//    }
//
//    public static File getDirInAppCacheDir(Context context, String dirName) {
//        File dirFile = new File(appCacheDir(context), dirName);
//        if (!dirFile.exists()) {
//            dirFile.mkdirs();
//        }
//        return dirFile;
//    }
//    private static File createFileIfNotExisted(File mayNeedCreatedFile) {
//        if (mayNeedCreatedFile != null) {
//            if (!mayNeedCreatedFile.getParentFile().exists()) {
//                mayNeedCreatedFile.getParentFile().mkdirs();
//            }
//            if (!mayNeedCreatedFile.exists()) {
//                try {
//                    boolean isOptSuc = mayNeedCreatedFile.createNewFile();
//                } catch (IOException e) {
//                    CommonLog.e(TAG, "-->createFileIfNotExisted() " + mayNeedCreatedFile + "create occur " + e);
//                }
//            }
//        }
//        return mayNeedCreatedFile;
//    }
//    /**
//     * 获取APP内部存储文件的根目录file
//     * @param context
//     * @return File("data/data/com.xx.xx/");
//     */
//    @SuppressLint("NewApi")
//    public static File appDataRootDir(Context context) {
//        if (Util.isCompateApi(24)) {
//            return context.getDataDir();
//        }
//        return appCacheDir(context).getParentFile();
//    }
//
//    /**
//     * 获取APP内部存储目录下的files文件夹file
//     * @param context
//     * @return File("data/data/com.xx.xx/files/");
//     */
//    public static File appFilesDir(Context context) {
//        return context.getFilesDir();
//    }
//
//    /**
//     * 获取APP内部存储目录下的所填参数文件夹名的文件夹
//     * 注：系统会自动在所传的参数前面加上"app_"前缀
//     * @param context 上下文
//     * @param dirName 要获取或者创建的文件夹名称
//     * @return File("data/data/com.xx.xx/app_dirName");
//     */
//    public static File getDirInAppDataRootDir(Context context, String dirName) {
//        return context.getDir(dirName, Context.MODE_PRIVATE);
//    }

    /**
     * 删除文件或者文件夹(文件夹直接删除好像删除不了，所以要递归删除)
     * @param toDeleteFileOrDir 要删除的文件或者文件夹
     * @throws IOException 删除时的异常
     */
    public static void deleteFile(File toDeleteFileOrDir)throws IOException {
        if (toDeleteFileOrDir != null) {
            boolean isDirFile = toDeleteFileOrDir.isDirectory();
            if (isDirFile) {
                File[] listFiles =  toDeleteFileOrDir.listFiles();
                if (listFiles == null) {
                    throw new IOException("not a readable directory: " + toDeleteFileOrDir);
                }
                for (File subFile : listFiles) {
                    deleteFile(subFile);
                }
            }
            else{
                if (!toDeleteFileOrDir.delete()) {
                    throw new IOException("failed to delete file: " + toDeleteFileOrDir);
                }
            }
        }
    }
}