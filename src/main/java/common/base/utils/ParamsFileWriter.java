package common.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * 读取参数Property操作类
 * 注：如果用上引用队列则不支持跨进程
 * <br/>
 * 2015年12月23日-下午3:05:06
 * @author lifei
 */
public class ParamsFileWriter {
    private static final String TAG = "ParamsFileWriter";
    private static ParamsFileWriter mWriter = null;

    private ParamsFileWriter() {
        refs = Collections.synchronizedMap(new Hashtable<String, RefProper>());
        queue = new ReferenceQueue<Properties>();
    };

    private ReferenceQueue<Properties> queue;
    private final Map<String, RefProper> refs;

    public static ParamsFileWriter getWriter() {
        if (mWriter == null) {
            synchronized (ParamsFileWriter.class) {
                if (mWriter == null) {
                    mWriter = new ParamsFileWriter();
                }
            }
        }
        return mWriter;
    }

    private class RefProper extends SoftReference<Properties> {
        private String filePath;

        public RefProper(Properties r, ReferenceQueue<? super Properties> q, String filePath) {
            super(r, q);
            this.filePath = filePath;
        }
    }
    public Properties getProper(File theProperFile){
        if(theProperFile == null || !theProperFile.exists()) return null;
        Properties result = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(theProperFile);
            result = new Properties();
            result.load(fis);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }
    public Properties getProper(String filePath) {
        Properties result = null;
        // if(refs.containsKey(filePath)){
        // RefProper ref = refs.get(filePath);
        // result = ref.get();
        // CommonLog.sysOut(TAG +" 软引用中存在 ..." +filePath +" proper = "+ result
        // +" this = "+this);
        // }
        // boolean isOperateSuc = true;
        if (result == null) {
            result = new Properties();
            try {
                result.load(new FileInputStream(new File(filePath)));
            } catch (Exception e) {
                // isOperateSuc = false;
                CommonLog.e(TAG, "", e);
            }
            // cleanCache();
            // if(isOperateSuc){
            // RefProper aRef = new RefProper(result, queue, filePath);
            // refs.put(filePath, aRef);
            // CommonLog.sysOut(TAG +" 放入软引用中 ..." +filePath + " proper = "+ result
            // +" this = "+this);
            // }
        }
        return result;
    }

    // public Properties getProper(String filePath){
    // Properties result = null;
    // if(refs.containsKey(filePath)){
    // RefProper ref = refs.get(filePath);
    // result = ref.get();
    // }
    // boolean isOperateSuc = true;
    // if(result == null){
    // result = new Properties();
    // try {
    // result.load(new FileInputStream(new File(filePath)));
    // } catch (Exception e) {
    // isOperateSuc = false;
    // CommonLog.e(TAG, "", e);
    // }
    // cleanCache();
    // if(isOperateSuc){
    // RefProper aRef = new RefProper(result, queue, filePath);
    // refs.put(filePath, aRef);
    // }
    // }
    // return result;
    // }
    private void cleanCache() {
        RefProper ref = null;
        while ((ref = (RefProper) queue.poll()) != null) {
            refs.remove(ref.filePath);
        }
    }

    /**
     * 将信息写入文件，当value参数为空时，则不执行写操作
     * @param filePath
     *            如果文件不存在，则会先创建文件
     * @param key
     * @param value
     *            为空时，不写入
     */
    public void writeInfo(String filePath, String key, String value) {
        if (key == null || key.length() == 0) {
            return;
        }
        File theFile = new File(filePath);
        if (!theFile.exists()) {
            refs.remove(filePath);
            try {
                theFile.createNewFile();
            } catch (IOException e) {
                return;
            }
        }
        if (value == null) {
            return;
        }
        Properties curProper = getProper(filePath);
        curProper.setProperty(key, value);
        try {
            curProper.store(new FileOutputStream(theFile), null);
        } catch (FileNotFoundException e) {
            CommonLog.e(TAG, "", e);
        } catch (IOException e) {
            CommonLog.e(TAG, "", e);
        }
    }

    public void writeInfo(String filePath, String[] keys, String... vs) {
        if (keys == null || keys.length == 0) {
            return;
        }
        File theFile = new File(filePath);
        if (!theFile.exists()) {
            refs.remove(filePath);
            try {
                theFile.createNewFile();
            } catch (IOException e) {
                return;
            }
        }
        Properties curProper = getProper(filePath);
        for (int i = 0; i < keys.length; i++) {
            curProper.setProperty(keys[i], vs[i]);
        }
        try {
            curProper.store(new FileOutputStream(theFile), null);
        } catch (FileNotFoundException e) {
            CommonLog.e(TAG, "", e);
        } catch (IOException e) {
            CommonLog.e(TAG, "", e);
        }
    }

    /**
     * 
     * @param filePath
     * @param key
     * @return null if the file not exists,or no value be found.
     */
    public String getInfo(String filePath, String key) {
        File properFile = new File(filePath);
        if (!properFile.exists()) {
            return null;
        }
        Properties theOne = getProper(filePath);
        return theOne.getProperty(key);
    }
}
