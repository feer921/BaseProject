package common.base.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.File;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/1/8<br>
 * Time: 22:39<br>
 * <P>DESC:
 * 系统媒体的工具类，后续增加通用获取媒体文件的方法
 * </p>
 * ******************(^_^)***********************
 */
public class MediaUtil {
    /**
     * 通知系统进行媒体文件的扫描任务
     * @param context
     */
    public static void notifySysMedia2Scan(Context context, MediaScannerConnection.OnScanCompletedListener callback) {
        notifySysMedia2Scan(context, callback, null);
    }

    /**
     * 通过广播的方式通知系统扫描某个文件
     * @param appContext appContext
     * @param filePath 要被扫描的文件路径
     */
    public static void notifySysScanFile(Context appContext, String filePath) {
        Uri data = Uri.parse("file://" + filePath);
        Intent toScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data);
        appContext.sendBroadcast(toScanIntent);
    }
    public static void notifySysMedia2Scan(Context context, MediaScannerConnection.OnScanCompletedListener callback, String toScanTheFilePath) {
        if (null == toScanTheFilePath) {
            toScanTheFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        if (!Util.isCompateApi(19)) {//android 4.4系统之前,发广播的方式通知系统去扫描
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + toScanTheFilePath)));
        }
        else{
            MediaScannerConnection.scanFile(context, new String[] {toScanTheFilePath}, null, callback);
        }
    }

    public static void notifyAddedImg(Context appContext,File newAddedImgFile) {
        if (newAddedImgFile != null && newAddedImgFile.exists()) {
            String fileAbsPath = newAddedImgFile.getAbsolutePath();
            String fileNmae = newAddedImgFile.getName();
//            try {//这个会再生成一个缩略图
//                MediaStore.Images.Media.insertImage(appContext.getContentResolver(),fileAbsPath, newAddedImgFile.getName(), fileNmae);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            notifySysScanFile(appContext,fileAbsPath);
        }
    }
}
