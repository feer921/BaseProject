package common.base;

import android.app.Activity;
import android.app.Application;
import common.base.utils.PreferVisitor;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-07-02
 * Time: 12:00
 * DESC: APP程序上下文基类,一些通用的操作类 <E>指代BaseApplication子类自身，方便链式调用
 */
public class BaseApplication<E extends BaseApplication> extends Application{
    protected PreferVisitor preferVisitor;
    protected String appPreferFileName = "def_app_config";
    @Override
    public void onCreate() {
        super.onCreate();
        preferVisitor = PreferVisitor.getInstance(this);
    }

    public E prefer(String preferKey, Object value) {
        preferVisitor.saveValue(appPreferFileName, preferKey, value);
        return (E) this;
    }

    public <T> T getPrefer(String preferKey,T defValue) {
        return preferVisitor.getValue(appPreferFileName, preferKey, defValue);
    }

    /***
     * 批量将首选项数据存入首选项文件中
     * @param keys
     * @param valueDatas
     */
    public E batchPrefer(String[] keys, Object... valueDatas) {
        preferVisitor.batchSaveValues(appPreferFileName,keys,valueDatas);
        return (E) this;
    }

    /**
     * 栈入一个当前启动的Activity
     * @param curActivity
     */
    public E stackActivity(Activity curActivity) {
        AppManager.getMe().addActivity(curActivity);
        return (E) this;
    }

    /**
     * 当一个Activity结束时也从管理的栈内踢出当前的Activity
     * @param curActivity
     */
    public E kickOutActivity(Activity curActivity) {
//        AppManager.getMe().finishActivity(curActivity);
        //changed by fee 2018-04-26: 更改为从栈中移出当前关闭的Activity
        AppManager.getMe().removeStackedActivity(curActivity);
        return (E) this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AppManager.getMe().exitWholeApp(this);
    }
}
