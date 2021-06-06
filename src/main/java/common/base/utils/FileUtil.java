package common.base.utils;

import androidx.annotation.WorkerThread;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * *****************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2021/4/24<br>
 * Time: 22:42<br>
 * <P>DESC:
 * 操作文件的工具类
 * </p>
 * ******************(^_^)***********************
 */
public class FileUtil {


    public static boolean writeStrContentToFile(String content, String fileAbsPath,boolean toAppendFileEnd) {
        boolean isWritenOk = false;
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileAbsPath, toAppendFileEnd);
            fw.write(content);
            isWritenOk = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }finally {
            if (fw != null) {
                try {
                    fw.flush();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isWritenOk;
    }

    @WorkerThread
    public static boolean deleteFileOrDir(String filePath) {
        if (CheckUtil.isEmpty(filePath)) {
            return false;
        }
        try {
            boolean isOptSuc = false;
            File theFile = new File(filePath);
            if (theFile.exists()) {
                if (theFile.isFile()) {
                    isOptSuc = theFile.delete();
                }else if(theFile.isDirectory()) {
                    //文件夹需要递归删除
                    StorageUtil.deleteFile(theFile);
                }
            }
            return isOptSuc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
