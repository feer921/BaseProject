package com.chad.library.adapter.base.listener;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-12-16
 * Time: 16:55
 * DESC: 提供给RecyclerView item的各点击事件，也可以直接使用SimpleClickListener
 * 包含的监听的事件类型为：
 * 1、onItemClick()整个item布局的点击事件
 * 2、onItemLongClick() 整个item布局的长按事件
 * 3、onItemChildClick() item布局内的各子类View的点击事件
 * 4、onItemChildLongClick() item布局内的各子类View的长按事件
 * 本类抽象出onItemClick()，即为主要用来监听整个Item布局的点击事件
 * 也可以重写剩下其他的click事件来处理item中的所有view的click事件
 */
public abstract class OnRecyclerItemClickEventListener extends SimpleClickListener{
    /**
     * callback method to be invoked when an item in this view has been
     * click and held
     *
     * @param adapter
     * @param view     The view whihin the AbsListView that was clicked
     * @param position The position of the view int the adapter
     * @return true if the callback consumed the long click ,false otherwise
     */
    @Override
    public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
