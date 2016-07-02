package common.base;

import android.app.Application;
import common.base.utils.PreferVisitor;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-07-02
 * Time: 12:00
 * DESC: APP程序上下文基类,一些通用的操作类
 */
public class BaseApplication extends Application{
    protected PreferVisitor preferVisitor;
    protected String appPreferFileName = "def_app_config";
    @Override
    public void onCreate() {
        super.onCreate();
        preferVisitor = PreferVisitor.getInstance(this);
    }

    public void prefer(String preferKey, Object value) {
        preferVisitor.saveValue(appPreferFileName, preferKey, value);
    }

    public <T> T getPrefer(String preferKey,T defValue) {
        return preferVisitor.getValue(appPreferFileName, preferKey, defValue);
    }

    /***
     * 批量将首选项数据存入首选项文件中
     * @param keys
     * @param valueDatas
     */
    public void batchPrefer(String[] keys, Object... valueDatas) {
        preferVisitor.batchSaveValues(appPreferFileName,keys,valueDatas);
    }
}
