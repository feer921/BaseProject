package common.base.activitys;

import android.support.v4.view.PagerAdapter;
import common.base.adapters.ViewPagerFragmentAdapter;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-27
 * Time: 13:47
 * DESC: 通用的布局界面，只是ViewPager中的内容视图指定为装载Fragment 布局，即指定了ViewPager的适配器为ViewPagerFragmentAdapter
 */
public abstract class CommonViewpagerFragmentViewActivity<T> extends CommonTabstripViewPagerActivity<T>{
    protected ViewPagerFragmentAdapter adapter4ViewPagerFragmentInner;
    /**
     * 为ViewPager提供适配器
     *
     * @return
     */
    @Override
    protected final PagerAdapter getAdapter4ViewPager() {
        adapter4ViewPagerFragmentInner = new ViewPagerFragmentAdapter(getSupportFragmentManager(), mContext);
        return adapter4ViewPagerFragmentInner;
    }

    /**
     * 初始伦ViewPager 的适配器中的数据
     */
    @Override
    protected final void setupAdapter() {
        setupViewPagerFragmentAdapter(adapter4ViewPagerFragmentInner);
    }

    /**
     * 在该方法中给适配器添加子视图(Fragment类型)
     * @param adapter4ViewPagerFragmentInner 当前界面中为ViewPager提供碎片界面视图的适配器
     */
    protected abstract void setupViewPagerFragmentAdapter(ViewPagerFragmentAdapter adapter4ViewPagerFragmentInner);
}
