package common.base.utils;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-30
 * Time: 19:39
 * DESC:
 */
public class BaseDataAssetor {
    protected final String TAG = getClass().getSimpleName();
    /***
     * 数据存放位置:SD卡目录上; data缓存区
     */
    protected DataLocation curDataLocation;
    public enum DataLocation{
        SD_CARD,DATA_CACHE
    }
    /**
     * 普通的从文件中读取出序列化对象
     * @param context
     * @param fileName 文件的全路径名称
     * @return
     */
    public static Serializable readDataObject(Context context, String fileName){
        if (Util.isEmpty(fileName)) {
            return null;
        }
        File dataFile = new File(fileName);
        if(!dataFile.exists()) return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(dataFile);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof InvalidClassException){
                dataFile.delete();
            }
        }
        finally {
            if(ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                }
            }
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
    /**
     * 存储一个可序列化的数据
     * @param data
     * @param targetFilePath 目标文件的全路径
     * @return
     */
    public static boolean saveAdataObject(Serializable data,String targetFilePath){
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(targetFilePath);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            fos.flush();
            return true;
        } catch (Exception e) {
            CommonLog.e("BaseDataAssetor", e);
            return false;
        }
        finally {
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
