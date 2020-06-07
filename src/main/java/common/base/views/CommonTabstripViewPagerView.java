package common.base.views;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import common.base.R;
import common.base.utils.ViewUtil;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-25
 * Time: 11:19
 * DESC: 一个通用的界面布局视图View，界面元素一般为：头部Header View(本类需调用代码增加用户自定义头部布局)、
 *       水平方向的可滑动的标题选项卡(本类为PagerSlidingTabStrip)、剩下的全高度为ViewPager视图(可左右滑动切换页面)
 *       一般通用于Activity使用
 */
public class CommonTabstripViewPagerView extends FrameLayout{
    private ViewPager viewPager;
    private LinearLayout llContanerView;
    /**
     * 标签选项卡
     */
    private PagerSlidingTabStrip tabStrips;
    public CommonTabstripViewPagerView(Context context) {
        this(context, null);
    }

    public CommonTabstripViewPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }
    private void initViews(Context context){
        inflate(context, R.layout.common_tabstrip_viewpager_layout, this);
        llContanerView = ViewUtil.findViewInContainer(this, R.id.ll_contaner_view);
        tabStrips = ViewUtil.findViewInContainer(this, R.id.pager_tabstrips);
        viewPager = ViewUtil.findViewInContainer(this, R.id.viewpager);
    }
    public void setPagerAdapter(PagerAdapter adapter){
        viewPager.setAdapter(adapter);
    }

    public ViewPager getViewPager(){
        return viewPager;
    }
    public PagerSlidingTabStrip getPagerTabStrip(){
        return tabStrips;
    }

    public void addCustomHeaderView(View customHeaderView) {
        //不为空，并且所提供的头部视图无父控件即未添加到其他容器内
        if (customHeaderView != null && customHeaderView.getParent() == null) {
            llContanerView.addView(customHeaderView,0);
        }
    }
}
