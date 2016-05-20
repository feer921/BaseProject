package common.base.interfaces;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-18
 * Time: 11:29
 * DESC: 相同的通用的一些行为的接口
 */
public interface ICommonBehaviors {
    /**
     * 获取当前Activity需要填充、展示的内容视图，如果各子类提供，则由基类来填充，如果不提供，各子类也可自行处理
     * @return 当前Activity需要展示的内容视图资源ID
     */
    int getProvideContentViewResID();
    /**
     * 如果子类在getProvideContentViewResID()方法提供了视图资源，那么子类的初始化视图可在此方法中完成
     */
    abstract void initViews();

    /**
     * 初始化数据
     */
    void initData();
}
