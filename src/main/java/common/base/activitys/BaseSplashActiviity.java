package common.base.activitys;

import android.view.animation.Animation;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-16
 * Time: 17:29
 * DESC: 闪屏基类,本来该类没有什么通用性,在此写一些简单的通用内容
 */
public class BaseSplashActiviity extends BaseActivity{
    /**
     * 闪屏(广告)、动画持续的时间
     * 毫秒为单位
     */
    protected int splashDuringTime;
    /**
     * 获取当前Activity需要填充、展示的内容视图，如果各子类提供，则由基类来填充，如果不提供，各子类也可自行处理
     *
     * @return 当前Activity需要展示的内容视图资源ID
     */
    @Override
    protected int getProvideContentViewResID() {
        return 0;
    }

    /**
     * 如果子类在getProvideContentViewResID()方法提供了视图资源，那么子类的初始化视图可在此方法中完成
     */
    @Override
    protected void initViews() {

    }

    protected boolean isAppFirstStart() {
        return false;
    }

    protected Animation getAnimation4BackgroudImsg() {
        return null;
    }
}
