package common.base.interfaces;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-12-16
 * Time: 17:11
 * DESC: 该接口是为了兼容BRVAH中把RecyclerView的item各点击事件的监听都变为抽象类后，用来转接回调出去,
 * 方便子类实现
 */
public interface IRecyclerViewItemClickEventsListener {
    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    void onItemClick(BaseQuickAdapter adapter, View view, int position);
    /**
     * callback method to be invoked when an item in this view has been
     * click and held
     * @param adapter
     * @param view     The view whihin the AbsListView that was clicked
     * @param position The position of the view int the adapter
     * @return true if the callback consumed the long click ,false otherwise
     */
    void onItemLongClick(BaseQuickAdapter adapter, View view, int position);

    /**
     * RecyclerView中的item布局内各子View的点击事件
     * @param adapter 当前的适配器
     * @param view 当前被点击的视图View，用id来switch区分
     * @param position 被点击的子View在RecyclerView中的位置
     */
    void onItemChildClick(BaseQuickAdapter adapter, View view, int position);

    /**
     * RecyclerView中的item布局内各子View的长按事件
     * @param adapter 当前的适配器
     * @param view 当前被点击的视图View，用id来switch区分
     * @param position 被长按的子View在RecyclerView中的位置
     */
    void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position);
}
