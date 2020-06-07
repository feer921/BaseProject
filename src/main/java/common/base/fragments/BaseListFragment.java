package common.base.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnRecyclerItemClickEventListener;

import java.util.List;

import common.base.beans.BaseListEntity;
import common.base.interfaces.ICommonActionsInListUi;
import common.base.interfaces.IRecyclerViewItemClickEventsListener;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-17
 * Time: 10:59
 * DESC: 带有列表数据的基类Fragment 不指定布局,并且默认本基类统一处理单个列表的情况（该列表请求类型为requestTypeAboutListData）
 * 如果一个界面上有2个以上列表的网络请求，则子类可重载 dealWithBeyondListResponse以及dealWithBeyondListErrorResponse来做处理
 * 注：本类的<T,TListData,VH> 其中的T指替父类的 T 表示网络请求需要返回的数据类型，TListData表示列表中需要的数据类型，VH指替父类的VH为适配器的
 * BaseQuickAdapter所指定的ViewHold类型
 */
public abstract class BaseListFragment<T,TListData,VH extends BaseViewHolder> extends BaseNetCallFragment<T> implements ICommonActionsInListUi<TListData,VH>,IRecyclerViewItemClickEventsListener {
    protected BaseQuickAdapter<TListData,VH> adapter4RecyclerView;
    /**
     * 当前列表请求的页数
     */
    protected int curPage = 1;
    /**
     * 每页从服务器上获取的数量
     */
    protected int perPageDataCount = 20;
    /**
     * 总页数
     */
    protected int totalPages = 1;
    /**
     * 即本列表的数据来源于哪个网络请求类型
     * 由子类指定
     */
    protected int requestTypeAboutListData;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initNetDataListener();
        needSomeUiHintAgent();
        if (adapter4RecyclerView == null) {
            adapter4RecyclerView = getRecyclerViewAdapter();
            //remove it by fee 2016-12-16 RecyclerView的点击事件更改为交由RecyclerView来设置
//            adapter4RecyclerView.setOnRecyclerViewItemClickListener(this);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void dealWithErrorResponse(int curRequestDataType, String errorInfo) {
        if (curRequestDataType == requestTypeAboutListData) {
            listDataRequestFinish(adapter4RecyclerView.getJustDataCount()!= 0 ,errorInfo);
        }
        else{
            dealWithBeyondListErrorResponse(curRequestDataType, errorInfo);
        }
    }

    @Override
    protected void dealWithResponse(int requestDataType, T result) {
        if (requestDataType == requestTypeAboutListData) {
            //1、解析返回的数据成列表数据
            BaseListEntity<TListData> parsedListEntity = parseResponseResut(result);
            //2、从(含集合类的实体)中取出集合,并填充数据到适配器中
            if (parsedListEntity != null) {
                listDataRequestSuccess(parsedListEntity.getListData());
            } else{
                //modified/added by fee 2016-07-09 考虑：有时服务器的返回结果T result,并不能方便的解析成并转换成BaseListEntity的形式,为了通用，再增加一个方法
                //让子类直接转换成对应的集合数据
                listDataRequestSuccess(parseResponseResut2List(result));
            }
            //3通知一次成功的请求完成
            listDataRequestFinish(adapter4RecyclerView.getJustDataCount() != 0,null);
        }
        else{
            dealWithBeyondListResponse(requestDataType, result);
        }
    }



    /**
     * 列表数据请求成功,在此方法中做填充新增数据并做去重处理的逻辑
     * @param newData
     */
    @Override
    public void listDataRequestSuccess(List<TListData> newData) {
        if (newData == null || newData.isEmpty()) {
            return;
        }
        if (curPage == 1) {//仍然是在第一页请求数据，比如下拉刷新
            adapter4RecyclerView.clearData();
        }
        //处理可能重复的数据
        int newDataCount = newData.size();
        for(int i = 0 ; i < newDataCount; i++) {
            if (compareData(adapter4RecyclerView.getData(), newData.get(i))) {
                newData.remove(i);
                i--;
            }
        }
        if (!newData.isEmpty()) {
            adapter4RecyclerView.addData(newData);
        }
    }
    /**
     * 获取或者初始化RecyclerView的适配器
     *
     * @return
     */
    @Override
    public abstract BaseQuickAdapter<TListData,VH> getRecyclerViewAdapter();
    /***
     * 根据网络的响应类型解析成 BaseListEntity 实体对象
     * @param result
     * @return BaseListEntity object
     */
    protected abstract BaseListEntity<TListData> parseResponseResut(T result);

    /**
     * 将网络请求的响应结果T result交给子类直接转换成集合数据
     * @param result
     * @return
     */
    protected abstract List<TListData> parseResponseResut2List(T result);
    /**
     * 通过比较要新添加的数据与老列表数据，判断是否重复的数据
     * 如果重复的数据一般就不添加进原来的列表
     *
     * @param oldDatas
     * @param willAddedOne
     * @return
     */
    @Override
    public abstract boolean compareData(List<TListData> oldDatas, TListData willAddedOne);

    /**
     * 列表数据请求结束，实现此方法，通过判断一次请求数据后是否有列表数据来决定UI的显示逻辑等
     *
     * @param hasListDataResult
     * @param errorInfoIfRequestFail
     */
    @Override
    public abstract void listDataRequestFinish(boolean hasListDataResult, String errorInfoIfRequestFail);


    /**
     * 处理不是 获取列表数据的网络请求的结果
     * @param requestDataType
     * @param result
     */
    protected void dealWithBeyondListResponse(int requestDataType, T result) {

    }

    /**
     * 处理不是 获取列表数据的网络请求的错误
     * @param curRequestDataType
     * @param errorInfo
     */
    protected void dealWithBeyondListErrorResponse(int curRequestDataType, String errorInfo){
        super.dealWithErrorResponse(curRequestDataType, errorInfo);//如果子类也不处理交由基类统一处理
    }

    //----------- add by fee 2016-12-16 --------------

    private OnRecyclerItemClickEventListener onRecyclerItemClickEventListener;

    /**
     * 子类Activity的列表控件RecyclerView调用mRecyclerView.addOnItemTouchListener(obtainTheRecyclerItemClickListen())
     * 则可重写
     * @return
     */
    protected OnRecyclerItemClickEventListener obtainTheRecyclerItemClickListen() {
        if (onRecyclerItemClickEventListener == null) {
            onRecyclerItemClickEventListener = new OnRecyclerItemClickEventListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    BaseListFragment.this.onItemClick(adapter, view, position);
                }

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
                    BaseListFragment.this.onItemLongClick(adapter,view,position);
                }

                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    BaseListFragment.this.onItemChildClick(adapter, view, position);
                }

                @Override
                public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                    BaseListFragment.this.onItemChildLongClick(adapter, view, position);
                }
            };
        }
        return onRecyclerItemClickEventListener;
    }
    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param adapter
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position){

    }

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

    /**
     * RecyclerView中的item布局内各子View的点击事件
     *
     * @param adapter  当前的适配器
     * @param view     当前被点击的视图View，用id来switch区分
     * @param position 被点击的子View在RecyclerView中的位置
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }

    /**
     * RecyclerView中的item布局内各子View的长按事件
     *
     * @param adapter  当前的适配器
     * @param view     当前被点击的视图View，用id来switch区分
     * @param position 被长按的子View在RecyclerView中的位置
     */
    @Override
    public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
