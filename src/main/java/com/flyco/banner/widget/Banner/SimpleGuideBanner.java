package com.flyco.banner.widget.Banner;

import android.content.Context;
import android.view.View;

/**
 * ******************(^_^)***********************
 * User: 11776610771@qq.com
 * Date: 2017/4/6
 * Time: 14:41
 * DESC:
 * ******************(^_^)***********************
 */

public class SimpleGuideBanner extends BaseGuideBanner<Integer>{
    public SimpleGuideBanner(Context context) {
        super(context);
    }

    /**
     * 如果使用者连往Banner里装入Data数据都不愿意装入，则自己使用这个方法来提供每一导航页的View吧
     *
     * @param position
     * @return
     */
    @Override
    protected View provideYourViewWithOutDatas(int position) {
        return null;
    }

    /**
     * 根据提供的数据集来获取每一页的视图
     *
     * @param itemData
     * @param itemPosition
     * @return
     */
    @Override
    protected View getItemViewBaseData(Integer itemData, int itemPosition) {
        return null;
    }

    /**
     * 初始化当前的导航页的视图，这个要交给具体子类去实现，因为本基类不知道，滑动导航到各页时，需要初始化哪些子View，并且不知道要做什么
     *
     * @param curView         当前的导航页(视图)
     * @param curViewPosition 当前导航页(视图)的位置
     */
    @Override
    public void initGuideView(View curView, int curViewPosition) {

    }
}
