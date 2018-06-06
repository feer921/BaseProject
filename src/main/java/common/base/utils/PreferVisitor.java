package common.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;
import java.util.Set;

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
    public static final byte V_TYPE_SET_STR = 6;
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
            mContext = appContext.getApplicationContext();
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

    public Object getValueWithOutSet(String preferFileName, String key, Object defObj) {
        if (Util.isEmpty(preferFileName) || Util.isEmpty(key)) {
            return null;
        }
        SharedPreferences sp = getSpByName(preferFileName);
        if (defObj == null) {
            return sp.getString(key, "");
        }
        else{
            String objType = defObj.getClass().getSimpleName();
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
        }
        return null;
    }

    @SuppressLint("NewApi")
    public <T> T getValue(String preferFileName, String preferKey, T defValue) {
        if (Util.isEmpty(preferFileName) || Util.isEmpty(preferKey)) {
            return null;
        }
        T resultData = null;
        SharedPreferences sp = getSpByName(preferFileName);
        if (defValue == null) {
            resultData = (T) sp.getString(preferKey, "");
        }
        else{
            if (defValue instanceof Integer) {
                Integer defValueInt = (Integer) defValue;
                resultData = (T) (defValueInt = sp.getInt(preferKey, defValueInt));
            } else if (defValue instanceof String) {
                resultData = (T) sp.getString(preferKey, (String) defValue);
            } else if (defValue instanceof Float) {
                Float defFloat = (Float) defValue;
                resultData = (T) (defFloat = sp.getFloat(preferKey, defFloat));
            } else if (defValue instanceof Boolean) {
                Boolean defBoolean = (Boolean) defValue;
                resultData = (T) (defBoolean = sp.getBoolean(preferKey, defBoolean));
            } else if (defValue instanceof Long) {
                Long defLong = (Long) defValue;
                resultData = (T) (defLong = sp.getLong(preferKey, defLong));
            } else if (defValue instanceof Set) {
                try {
                    Set<String> defSetStr = (Set<String>) defValue;
                    if(Util.isCompateApi(11))
                        resultData = (T) (defSetStr = sp.getStringSet(preferKey, defSetStr));
                } catch (Exception e) {
                }
            }
        }
        return resultData;
    }
    /**
     * 判断是否为SharedPreferences支持的数据类型
     * @param defValue
     * @return
     */
    private boolean isCanOptInSharedPreference(Object defValue) {
        if (defValue == null) {
            return false;
        }
        boolean stringT = defValue instanceof String;
        boolean intT = defValue instanceof Integer;
        boolean floatT = defValue instanceof Float;
        boolean longT = defValue instanceof Long;
        boolean booleanT = defValue instanceof Boolean;
        boolean setStrT = defValue instanceof Set;
        return stringT || intT || floatT || longT || booleanT || setStrT;
    }
    /**
     * 存储一条数据到首选项文件中
     * @param preferFileName 要保存的SharedPreference 文件名
     * @param key
     * @param value
     */
    @SuppressLint("NewApi")
    public boolean saveValue(String preferFileName, String key, Object value) {
        if (Util.isEmpty(preferFileName)) {
            return false;
        }
        Editor mEditor = getSpByName(preferFileName).edit();
        if (value == null) {
            mEditor.putString(key, null);
        }
        else{
            String valueType = value.getClass().getSimpleName();
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
            } else if ("Set".equals(valueType)) {
                if (Util.isCompateApi(11)) {
                    mEditor.putStringSet(key, (Set<String>) value);
                }
            }
        }
        return mEditor.commit();
    }

    /**
     * 批量存储数据 参数的长度一定要一致 并且keys中的key 与 values中同序号的元素要一一对应，以保证数据存储的正确性
     * @param preferFileName
     * @param keys
     * @param vTypes keys中键key要存储的对应的 values中的元素的数据类型 eg.: keys[0]= "name"; vTypes[0] = V_TYPE_STR;values[0]="ZhangSan"
     * @param values 要存储的值
     */
    @Deprecated
    @SuppressLint("NewApi")
    public boolean saveValue(String preferFileName, String[] keys, byte[] vTypes, Object... values) {
        if (keys == null || vTypes == null || values == null) {
            return false;
        }
        int keysLen = keys.length;
        if (keysLen == 0)
            return false;
        int vTypesLen = vTypes.length;
        int valuesLen = values.length;
        if (vTypesLen == 0 || valuesLen == 0) {
            return false;
        }
        //三者长度取最小的,以保证任何一个数组中不出现异常
        int canOptLen = Math.min(keysLen, Math.min(vTypesLen,valuesLen));
        Editor mEditor = getSpByName(preferFileName).edit();
        for (int i = 0; i < canOptLen; i++) {
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
                case V_TYPE_SET_STR:
                    if (Util.isCompateApi(11)) {
                        mEditor.putStringSet(key, (Set<String>) v);
                    }
                    break;
            }
        }
        return mEditor.commit();
    }

    /**
     * 批量存储数据
     * @param spFileName
     * @param keys
     * @param values
     */
    public boolean batchSaveValues(String spFileName, String[] keys, Object... values) {
        if (Util.isEmpty(spFileName) || keys == null || values == null) {
            return false;
        }
        int keysLen = keys.length;
        int valuesLen = values.length;
        int canOptLen = Math.min(keysLen, valuesLen);
        if (canOptLen == 0) {
            return false;
        }
        Editor editor = getSpByName(spFileName).edit();
        for(int i = 0; i < canOptLen ; i++) {
            String key = keys[i];
            if (Util.isEmpty(key)) {
                continue;
            }
            editorPutValues(editor,key,values[i]);
        }
        return editor.commit();
    }

    @SuppressLint("NewApi")
    private void editorPutValues(Editor editor, String preferKey, Object valueData) {
        if (editor == null) {
            return;
        }
        if (valueData == null) {
            editor.putString(preferKey, null);
        } else {
            if (valueData instanceof String) {
                editor.putString(preferKey, (String) valueData);
            } else if (valueData instanceof Integer) {
                editor.putInt(preferKey, (Integer) valueData);
            } else if (valueData instanceof Boolean) {
                editor.putBoolean(preferKey, (Boolean) valueData);
            } else if (valueData instanceof Float) {
                editor.putFloat(preferKey, (Float) valueData);
            } else if (valueData instanceof Set) {
                if (Util.isCompateApi(11))
                    editor.putStringSet(preferKey, (Set<String>) valueData);
            } else if (valueData instanceof Long) {
                editor.putLong(preferKey, (Long) valueData);
            }
        }
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

    /**
     * 批量移除喜好配置中的keys
     * @param spFileName
     * @param spKeys
     */
    public boolean batchRemoveKeys(String spFileName, String... spKeys) {
        if (spKeys != null && spKeys.length > 0) {
            Editor spFileEditor = getEditor(spFileName);
            for (String toRemoveKey : spKeys) {
                spFileEditor.remove(toRemoveKey);
            }
            return spFileEditor.commit();
        }
        return false;
    }
    public static final class PreferConfig {
        // 程序中各SharedPreference 文件的名字
        /**
         * 一般一个程序的设置首选项的文件名
         */
        public static final String SP_FILE_NAME_4_SETTING = "app_settings";
        /**
         * 一个程序中通用首选项的文件名
         */
        public static final String SP_FILE_NAME_4_COMMON = "app_common";

        // 各Keys
        
    }
}
