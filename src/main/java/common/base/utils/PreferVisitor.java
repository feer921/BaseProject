package common.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

/**
 * 喜好类【首选项】配置存储访问工具
 * 注：不支持跨进程
 * <br/>
 * 2015年12月23日-下午2:32:35
 * @author lifei
 */
public class PreferVisitor {
    // private static final String TAG = "PreferVisitor";
    private static Context mContext;
    public static final byte V_TYPE_STR = 1;
    public static final byte V_TYPE_INT = 2;
    public static final byte V_TYPE_BOOL = 3;
    public static final byte V_TYPE_LONG = 4;
    public static final byte V_TYPE_FLOAT = 5;
    static PreferVisitor mPreferVisitor = null;
    private Hashtable<String, PreferRef> refTables;
    private ReferenceQueue<SharedPreferences> queue; // 垃圾 Reference的队列

    private PreferVisitor() {
        refTables = new Hashtable<String, PreferRef>();
        queue = new ReferenceQueue<SharedPreferences>();
    }

    public static PreferVisitor getInstance(Context appContext) {
        if (mPreferVisitor == null) {
            mPreferVisitor = new PreferVisitor();
        }
        if (mContext == null) {
            mContext = appContext;
        }
        return mPreferVisitor;
    }

    private class PreferRef extends SoftReference<SharedPreferences> {
        private String keyInTable;

        public PreferRef(String preferFileName, SharedPreferences r, ReferenceQueue<? super SharedPreferences> q) {
            super(r, q);
            keyInTable = preferFileName;
        }
    }

    private SharedPreferences getSpByName(String preferFileName) {
        SharedPreferences sp = null;
        if (refTables.containsKey(preferFileName)) {
            PreferRef ref = refTables.get(preferFileName);
            sp = ref.get();
        }
        // 如果没有软引用，或者从软引用中得到的为null，则重新构建一个
        // 并保存对这个新建实例的软引用
        if (sp == null) {
            sp = mContext.getSharedPreferences(preferFileName, Context.MODE_PRIVATE);
            cacheGotSharedPrefer(sp, preferFileName);
        }
        return sp;
    }

    /**
     * 以一个软引用的方式对一个SharedPreference对象的实例进行引用并保存该软引用
     * @param theGotOne
     * @param keyInTable
     */
    private void cacheGotSharedPrefer(SharedPreferences theGotOne, String keyInTable) {
        cleanCache();
        PreferRef spRef = new PreferRef(keyInTable, theGotOne, queue);
        refTables.put(keyInTable, spRef);
    }

    /**
     * 清除已经被回收了的软件引用
     */
    private void cleanCache() {
        PreferRef ref = null;
        while ((ref = (PreferRef) queue.poll()) != null) {
            refTables.remove(ref.keyInTable);
        }
    }

    public Object getValue(String preferFileName, String key, Object defObj) {
        String objType = defObj.getClass().getSimpleName();
        SharedPreferences sp = getSpByName(preferFileName);

        if ("String".equals(objType)) {
            return sp.getString(key, (String) defObj);
        } else if ("Integer".equals(objType)) {
            return sp.getInt(key, (Integer) defObj);
        } else if ("Boolean".equals(objType)) {
            return sp.getBoolean(key, (Boolean) defObj);
        } else if ("Float".equals(objType)) {
            return sp.getFloat(key, (Float) defObj);
        } else if ("Long".equals(objType)) {
            return sp.getLong(key, (Long) defObj);
        }
        return null;
    }

    /**
     * 
     * @param preferFileName
     *            要保存的SharedPreference 文件名
     * @param key
     * @param value
     */
    public void saveValue(String preferFileName, String key, Object value) {
        String valueType = value.getClass().getSimpleName();
        System.out.println(" saveValue()   valueType = " + valueType);
        Editor mEditor = getSpByName(preferFileName).edit();
        if ("String".equals(valueType)) {
            mEditor.putString(key, (String) value);
        } else if ("Integer".equals(valueType)) {
            mEditor.putInt(key, (Integer) value);
        } else if ("Boolean".equals(valueType)) {
            mEditor.putBoolean(key, (Boolean) value);
        } else if ("Float".equals(valueType)) {
            mEditor.putFloat(key, (Float) value);
        } else if ("Long".equals(valueType)) {
            mEditor.putLong(key, (Long) value);
        }
        mEditor.commit();
    }

    /**
     * 参数的长度一定要一致
     * @param preferFileName
     * @param keys
     * @param vTypes
     * @param values
     */
    public void saveValue(String preferFileName, String[] keys, byte[] vTypes, Object... values) {
        if (keys == null) {
            return;
        }
        int len = keys.length;
        if (len == 0)
            return;
        Editor mEditor = getSpByName(preferFileName).edit();
        for (int i = 0; i < len; i++) {
            byte vType = vTypes[i];
            String key = keys[i];
            Object v = values[i];
            switch (vType) {
                case V_TYPE_STR:
                    mEditor.putString(key, (String) v);
                    break;
                case V_TYPE_INT:
                    mEditor.putInt(key, (Integer) v);
                    break;
                case V_TYPE_BOOL:
                    mEditor.putBoolean(key, (Boolean) v);
                    break;
                case V_TYPE_LONG:
                    mEditor.putLong(key, (Long) v);
                    break;
                case V_TYPE_FLOAT:
                    mEditor.putFloat(key, (Float) v);
                    break;
            }
        }
        mEditor.commit();
    }

    public Editor getEditor(String preferFile) {
        return getSpByName(preferFile).edit();
    }

    public long getValue(String preferFileName, String key, long defValue) {
        return getSpByName(preferFileName).getLong(key, defValue);
    }

    public int getValue(String preferFileName, String key, int defValue) {
        return getSpByName(preferFileName).getInt(key, defValue);
    }

    public String getValue(String preferFileName, String key, String defValue) {
        return getSpByName(preferFileName).getString(key, defValue);
    }

    public boolean getValue(String preferFileName, String key, boolean defValue) {
        return getSpByName(preferFileName).getBoolean(key, defValue);
    }

    public static final class PreferConfig {
        // 程序中各SharedPreference 文件的名字
        /**
         * 一般一个程序的设置首选项
         */
        public static final String SP_FILE_NAME_4_SETTING = "app_settings";
        /**
         * 一个程序中通用首选项
         */
        public static final String SP_FILE_NAME_4_COMMON = "app_common";

        // 各Keys
        
    }
}
