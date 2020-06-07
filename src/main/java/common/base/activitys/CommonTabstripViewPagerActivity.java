package common.base.activitys;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import common.base.views.CommonTabstripViewPagerView;
import common.base.views.PagerSlidingTabStrip;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-27
 * Time: 10:26
 * DESC: 通用的指定了布局CommonTabstripViewPagerView(子类不需要提供布局)
 * 布局类型：(从上至下)【子类可提供的自定义头部视图】、水平方向选项卡、ViewPager的界面
 */
public abstract class CommonTabstripViewPagerActivity<T> extends BaseNetCallActivity<T> implements ViewPager.OnPageChangeListener{
    protected CommonTabstripViewPagerView tabstripViewPagerView;
    protected View customHeaderView;
    /**
     * 提供给ViewPager的适配器
     */
    protected PagerAdapter adapter4ViewPager;
    /**
     * 水平方向的选项卡
     */
    protected PagerSlidingTabStrip pagerSlidingTabStrip;
    @Override
    protected final View providedContentView() {
        if (tabstripViewPagerView == null) {
            tabstripViewPagerView = new CommonTabstripViewPagerView(mContext);
        }
        customHeaderView = providedCustomHeaderView();
        tabstripViewPagerView.addCustomHeaderView(customHeaderView);
        pagerSlidingTabStrip = tabstripViewPagerView.getPagerTabStrip();
        adapter4ViewPager = getAdapter4ViewPager();
        if (adapter4ViewPager != null) {
            tabstripViewPagerView.setPagerAdapter(adapter4ViewPager);
//            pagerSlidingTabStrip.setViewPager(tabstripViewPagerView.getViewPager());
        }
        tabstripViewPagerView.getViewPager().addOnPageChangeListener(this);
        return tabstripViewPagerView;
    }

    /**
     * 本基类将该方法final掉，因为不需要子类提供布局视图了
     * 获取当前Activity需要填充、展示的内容视图，如果各子类提供，则由基类来填充，如果不提供，各子类也可自行处理
     * @return 当前Activity需要展示的内容视图资源ID
     */
    @Override
    protected final int getProvideContentViewResID() {
        return 0;
    }

    /**
     * 如果子类在getProvideContentViewResID()方法提供了视图资源，那么子类的初始化视图可在此方法中完成
     */
    @Override
    protected final void initViews() {
        initNetDataListener();
        if (customHeaderView != null) {
            initHeaderView(customHeaderView);
        }
        initTabStripView();
        setupAdapter();
        pagerSlidingTabStrip.setViewPager(tabstripViewPagerView.getViewPager());
    }

    /**
     * 为ViewPager提供适配器
     * @return
     */
    protected abstract PagerAdapter getAdapter4ViewPager();

    /**
     * 各子类提供的自定义的头部视图
     * @return
     */
    protected abstract View providedCustomHeaderView();

    /**
     * 初始伦ViewPager 的适配器中的数据
     */
    protected abstract void setupAdapter();

    /***
     * 初始化水平方向的选项卡
     * 比如选项卡中的文本颜色、间隔颜色、背景等
     */
    protected abstract void initTabStripView();

    /**
     * 如果子类有提供自定义头部布局视图的话，在此初始化视图
     * @param customHeaderView
     */
    protected abstract void initHeaderView(View customHeaderView);
}
