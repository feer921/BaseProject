package com.flyco.banner.widget.Banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import common.base.R;

/**
 * ******************(^_^)***********************
 * User: 11776610771@qq.com
 * Date: 2017/4/6
 * Time: 14:18
 * DESC: 泛型X，为继承基类的数据集的类型，可以是任意的类型
 * ******************(^_^)***********************
 */

public abstract class BaseGuideBanner<X> extends BaseIndicatorBanner<X,BaseGuideBanner<X>> {

    public BaseGuideBanner(Context context) {
        this(context, null, R.style.base_guide_banner);
    }

    public BaseGuideBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBarShowWhenLast(false);//在滑动到最后一页时不显示指示器布局
    }

//    /**
//     * 于基类的PagerAdapter中初始化Item布局时调用
//     * @param position
//     * @return
//     */
//    @Override
//    public View onCreateItemView(int position) {
//        View curItemView;
//        if (mDatas == null || mDatas.isEmpty()) {
//            curItemView = provideYourViewWithOutDatas(position);
//        }
//        else{
//            X itemData = mDatas.get(position);
//            if(itemData instanceof View){//可以直接装入各导航页(视图View)的Item数据集
//                curItemView = (View) itemData;
//            }
//            else{
//                curItemView = getItemViewBaseData(itemData,position);
//            }
//        }
//        initGuideView(curItemView,position);
//        return curItemView;
//    }
    /**
     * 于基类的PagerAdapter中初始化Item布局时调用
     * @param position
     * @return
     */
    @Override
    public View onCreateItemView(int position) {
        View curItemView;
        X itemData = mDatas.get(position);
        if (itemData instanceof View) {//可以直接装入各导航页(视图View)的Item数据集
            curItemView = (View) itemData;
        } else {
            curItemView = getItemViewBaseData(itemData, position);
        }
        initGuideView(curItemView, position);
        return curItemView;
    }

//
//    /**
//     * 如果使用者连往Banner里装入Data数据都不愿意装入，则自己使用这个方法来提供每一导航页的View吧
//     * @param position
//     * @return
//     */
//    protected abstract View provideYourViewWithOutDatas(int position);

    /**
     * 根据提供的数据集来获取每一页的视图
     * @param itemData
     * @param itemPosition
     * @return
     */
    protected abstract View getItemViewBaseData(X itemData,int itemPosition);

    /**
     * 初始化当前的导航页的视图，这个要交给具体子类去实现，因为本基类不知道，滑动导航到各页时，需要初始化哪些子View，并且不知道要做什么
     * @param curView 当前的导航页(视图)
     * @param curViewPosition 当前导航页(视图)的位置
     */
    public abstract void initGuideView(View curView, int curViewPosition);
}
