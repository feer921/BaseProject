package common.base.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import java.util.List;
import common.base.beans.BaseListEntity;
import common.base.interfaces.ICommonActionsInListUi;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-17
 * Time: 10:59
 * DESC: 带有列表据的基类Fragment 不指定布局,并且默认本基类统一处理单个列表的情况（该列表请求类型为requestTypeAboutListData）
 * 如果一个界面上有2个以上列表的网络请求，则子类可重载 dealWithBeyondListResponse以及dealWithBeyondListErrorResponse来做处理
 * 注：本类的<T,TListData> 其中的T指替父类的 T 表示网络请求需要返回的数据类型，TListData表示列表中需要的数据类型
 */
public abstract class BaseListFragment<T,TListData> extends BaseNetCallFragment<T> implements ICommonActionsInListUi<TListData>,BaseQuickAdapter.OnRecyclerViewItemClickListener{
    protected BaseQuickAdapter<TListData> adapter4RecyclerView;
    /**
     * 当前列表请求的页数
     */
    protected int curPage = 1;
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
            adapter4RecyclerView.setOnRecyclerViewItemClickListener(this);
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
            }
            //3通知一次成功的请求完成
            listDataRequestFinish(adapter4RecyclerView.getJustDataCount() != 0,null);
        }
        else{
            dealWithBeyondListResponse(requestDataType, result);
        }
    }

    /**
     * 列表数据请求成功,在此方法中做填充新增数据的逻辑
     *
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
    public abstract BaseQuickAdapter<TListData> getRecyclerViewAdapter();
    /***
     * 根据网络的响应类型解析成 BaseListEntity 实体对象
     * @param result
     * @return BaseListEntity object
     */
    protected abstract BaseListEntity<TListData> parseResponseResut(T result);

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
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    @Override
    public void onItemClick(View view, int position) {

    }
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
}
