package common.base.activitys;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;

import common.base.utils.CommonLog;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-16
 * Time: 17:29
 * DESC: 导航界面基类
 */
public abstract class BaseGuideActivity extends BaseActivity{
    /**
     * 该导航页是否需要全屏显示
     */
    protected boolean needFullScreen;
    protected SliderLayout toGuideSlideLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (needFullScreen) {//全屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        super.onCreate(savedInstanceState);
        toGuideSlideLayout = new SliderLayout(mContext);
        setContentView(toGuideSlideLayout);
        initData();
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

    protected void addSlideView(BaseSliderView oneSlideView) {
        toGuideSlideLayout.addSlider(oneSlideView);
    }
    protected class CommonSlideView extends BaseSliderView{
        private View slideContentView;
        private ImageView toShowGuideImageView;
        public CommonSlideView(Context context,View slideContentView,ImageView toShowGuideImageView) {
            super(context);
            this.slideContentView = slideContentView;
            this.toShowGuideImageView = toShowGuideImageView;
        }

        /**
         * the extended class have to implement getView(), which is called by the adapter,
         * every extended class response to render their own view.
         *
         * @return
         */
        @Override
        public View getView() {
            bindEventAndShow(this.slideContentView,toShowGuideImageView);
            return this.slideContentView;
        }
    }
}
