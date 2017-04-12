package common.base.activitys;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.flyco.banner.widget.Banner.BaseGuideBanner;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-16
 * Time: 17:29
 * DESC: 导航界面基类
 */
public abstract class BaseGuideActivity<GuidDataType> extends BaseActivity {
    protected BaseGuideBanner<GuidDataType> guideBanner;
    /**
     * 该导航页是否需要全屏显示(一般也需要全屏显示)
     */
    protected boolean needFullScreen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (needFullScreen) {//全屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);//这行代码对ActionBar无效
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        }
        super.onCreate(savedInstanceState);
        guideBanner = getGuideBanner();
        initGuideBanner();
        guideBanner.showBanner();
        //        setContentView(guideBanner,vlp);
        setContentView(guideBanner);
        initData();
    }

    /**
     * 初始化 BaseGuideBanner,eg.:给banner添加数据、设置切换动画、以及指示器等
     */
    protected abstract void initGuideBanner();

    /**
     * 子类可以重写这个，以提供自己实现的的BaseGuideBanner，如果提供了，则:<br>
     * {@linkplain #getGuideViewBaseData(Object, int)}<br>
     * {@linkplain #initGuideView(View, int)}<br>
     * 不用写逻辑了
     *
     * @return
     */
    protected BaseGuideBanner<GuidDataType> getGuideBanner() {
        return new BaseGuideBanner<GuidDataType>(this) {
            /**
             * 根据提供的数据集来获取每一页的视图
             *
             * @param itemData
             * @param itemPosition
             * @return
             */
            @Override
            protected View getItemViewBaseData(GuidDataType itemData, int itemPosition) {
                return getGuideViewBaseData(itemData, itemPosition);
            }

            /**
             * 初始化当前的导航页的视图，这个要交给具体子类去实现，因为本基类不知道，滑动导航到各页时，需要初始化哪些子View，并且不知道要做什么
             *
             * @param curView         当前的导航页(视图)
             * @param curViewPosition 当前导航页(视图)的位置
             */
            @Override
            public void initGuideView(View curView, int curViewPosition) {
                BaseGuideActivity.this.initGuideView(curView, curViewPosition);
            }
        };
    }

    protected abstract void initGuideView(View curGuideView, int curViewPosition);

    protected abstract View getGuideViewBaseData(GuidDataType itemData, int guideViewPos);

    protected void addGuideData(GuidDataType guideItemData) {
        guideBanner.addItemData(guideItemData);
    }

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

    @Override
    public void onBackPressed() {
        //一般在引导界面不能让用户点击返回键可以退出
    }
}
