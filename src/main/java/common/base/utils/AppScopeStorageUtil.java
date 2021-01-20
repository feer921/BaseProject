package common.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    /**
     * 从APP内部的/data/data/com.xx.xx/cache/文件夹下获取参数名的File
     * @param context context
     * @param fileName 要获取的文件名
     * @return File("/data/data/com.xx.xx/cache/fileName").
     */
    public static File getFileInAppCacheDir(Context context, String fileName) {
//        File file = new File(appCacheDir(context), fileName);
//        createFileIfNotExisted(file);
        return getFileUnderDirFile(appCacheDir(context), fileName);
    }

    /**
     * 从APP内部的/data/data/com.xx.xx/cache/文件夹下获取参数名的目录File
     * @param context context
     * @param dirName 要获取的文件夹名
     * @return File("/data/data/com.xx.xx/cache/dirName").
     */
    public static File getDirInAppCacheDir(Context context, String dirName) {
//        File dirFile = new File(appCacheDir(context), dirName);
//        if (!dirFile.exists()) {
//            dirFile.mkdirs();
//        }
        return getDirUnderDirFile(appCacheDir(context), dirName);
    }

    /**
     * 创建文件(如果某个文件不存在)，在创建文件的过程中会创建该文件的上级目录
     * @param mayNeedCreatedFile 可能需要创建的文件File
     * @return 要创建的文件
     */
    public static File createFileIfNotExisted(File mayNeedCreatedFile) {
        if (mayNeedCreatedFile != null) {
            File parentFile = mayNeedCreatedFile.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                boolean isMkSuc = parentFile.mkdirs();
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
     * @param context context
     * @return File("data/data/com.xx.xx/");
     */
    @SuppressLint("NewApi")
    public static File appDataRootDir(Context context) {
        if (Util.isCompateApi(24)) {//android 7.0
            return context.getDataDir();
        }
        return appCacheDir(context).getParentFile();
    }

    /**
     * 从/data/data/com.xx.xx/【文件夹】下查找对应【文件】名的文件
     * @param context context
     * @param fileName 要查找的【文件】名
     * @return File("/data/data/com.xx.xx/fileName").
     */
    public static File getFileInRootDir(Context context, String fileName) {
        return getFileUnderDirFile(appDataRootDir(context), fileName);
    }
    /**
     * 从/data/data/com.xx.xx/【文件夹】下查找对应【文件夹】名的文件
     * @param context context
     * @param dirName 要查找的【文件夹】名
     * @return File("/data/data/com.xx.xx/dirName").
     */
    public static File getDirInRootDir(Context context, String dirName) {
        return getDirUnderDirFile(appDataRootDir(context), dirName);
    }

    /**
     * 从传入的参数文件夹中获取对应文件名的File
     * @param dirFile 要查找的文件夹目录
     * @param fileName 要查找的文件名
     * @return File("/dirFile/fileName");
     */
    public static File getFileUnderDirFile(File dirFile, String fileName) {
        if (fileName != null) {
            File theFile = new File(dirFile, fileName);
            return createFileIfNotExisted(theFile);
        }
        return null;
    }
    /**
     * 从传入的参数【文件夹】中获取对应【文件夹】名的File
     * @param dirFile 要查找的文件夹目录
     * @param dirName 要查找的文件夹名
     * @return File("/dirFile/dirName");
     */
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
     * @param context context
     * @return File("data/data/com.xx.xx/files/");
     */
    public static File appFilesDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * 从/data/data/com.xx.xx/files/文件夹下查找对应文件名的文件
     * @param context context
     * @param fileName 要查找的文件名
     * @return File("/data/data/com.xx.xx/files/fileName").
     */
    public static File getFileInFilesDir(Context context, String fileName) {
//        File theFile = new File(appFilesDir(context), fileName);
//        createFileIfNotExisted(theFile);
        return getFileUnderDirFile(appFilesDir(context), fileName);
    }
    /**
     * 从/data/data/com.xx.xx/files/【文件夹】下查找对应【文件夹】名的文件
     * @param context context
     * @param dirName 要查找的【文件夹】名
     * @return File("/data/data/com.xx.xx/files/dirName").
     */
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
     * 注：这里好像不能传多级目录!!!!!!!!! 即不能传/yourdir1/yourdir2/
     * @param context 上下文
     * @param dirName 要获取或者创建的文件夹名称   这里好像不能传多级目录!!!!!!!!! 即不能传/yourdir1/yourdir2/
     * @return File("data/data/com.xx.xx/app_dirName");
     */
    public static File getDirInRootDirAppendApp(Context context, String dirName) {
        return context.getDir(dirName, Context.MODE_PRIVATE);
    }

    public static File getDataBaseFile(Context context, String dbfileName) {
        return context.getDatabasePath(dbfileName);
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


    /**
     * 从APP的 [assets]目录中复制 整个目录或者某个文件到 指定的目录[targetDirPath]路径下
     * @param context Context 上下文，最好使用 Application类型的
     * @param existManager 已经存在的 AssetManager (目的是复用已经存在的 AssetManager )
     * @param fileOrDirNameInAssets 在[assets]目录下的 目录(不能带"/"结尾,eg.: "bDir")或者相对[assets]的文件的文件名(eg.: bDir/test.txt)
     * @param isJustDirInAsset [assetFileOrDirName] 是否为文件夹
     * @param targetDirPath 要复制到的目标 目录路径, eg.: /sdcard/target/
     */
    public static void copyAssetsFilesToTargetPath(Context context, AssetManager existManager, String fileOrDirNameInAssets, boolean isJustDirInAsset, String targetDirPath) {
        if (existManager == null) {
            existManager = context.getAssets();
        }
        if (existManager == null) {
            return;
        }
        if (isJustDirInAsset) {
            try {
                String[] assetsFilesNamesInDir = existManager.list(fileOrDirNameInAssets);
                if (assetsFilesNamesInDir == null) {
                    return;
                }
                for (String aFileNameInDir : assetsFilesNamesInDir) {
                    //这里要再判断，[aFileNameInDir]是否仍为文件夹??
                    String theWillAccessAssetFilePath = fileOrDirNameInAssets + "/" + aFileNameInDir;
                    boolean isSubFileIsDir = false;
                    try {
                        String[] stillHasSubFiles = existManager.list(theWillAccessAssetFilePath);
                        isSubFileIsDir = stillHasSubFiles != null && stillHasSubFiles.length > 0;///如果该文件是文件夹但是目录下并没有文件呢？length ==0
                    } catch (Exception listSubFileException) {
                        listSubFileException.printStackTrace();
                    }
                    copyAssetsFilesToTargetPath(context, existManager, theWillAccessAssetFilePath, isSubFileIsDir, targetDirPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {//在 assets目录下为文件，可能的文件名为: a/test.txt
            //判断是否在 要复制到目标目录下该文件是否已经存在,如：/data/data/com.xx.xx/file/a/test.text
            File targetFile = new File(targetDirPath, fileOrDirNameInAssets);
            if (targetFile.exists() && targetFile.length() > 1) {
                return;
            }
            File parentFile = targetFile.getParentFile();
            if (parentFile!= null && !parentFile.exists()) {//使用这个就可以解决,带目录的 assets下资源名，复制到 目标文件夹下未先创建相应的 目录的问题
                boolean isMkSuc = parentFile.mkdirs();
            }
            InputStream is = null;
            OutputStream os = null;
            try {
                is = existManager.open(fileOrDirNameInAssets);
                os = new FileOutputStream(targetFile);
                byte[] readBuffer = new byte[1024];
                int readLen = -1;
                while ((readLen = is.read()) != -1) {
                    os.write(readBuffer, 0, readLen);
                }
                os.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
