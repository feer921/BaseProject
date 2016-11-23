package common.base.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * 打造一个万能列表数据适配器,并且子类在填充item视图时可以根据当前ListView是否在滚动而选择是否去加载网络图片的操作
 * 并且支持子类实现ListView显示不同数据类型的要求
 * 从此子类适配器不用关心item的Viewholder了
 * @param <T><br/>
 * 2015年10月23日-上午10:20:03
 * @author lifei
 */
public abstract class BaseCommonAdapter<T> extends BaseAdapter implements OnScrollListener{
    protected boolean isScrolling;
    protected Collection<T> dataList;
    private OnScrollListener outScrollListener;
    protected Context mContext;
    /**
     * 适配器提供给ListView的item视图类型数量,默认为1即表示只有一种item布局
     */
    protected int viewTypeCount = 1;
    public BaseCommonAdapter(Context curContext,Collection<T> data){
        if(data != null){
            dataList = data;
        }
        else{
            dataList = new ArrayList<T>(0);
        }
        mContext = curContext;
    }
    public BaseCommonAdapter(AbsListView listView, Collection<T> data) {
        if(data != null){
            dataList = data;
        }
        else{
            dataList = new ArrayList<T>(0);
        }
        if(listView != null){
            mContext = listView.getContext();
            listView.setOnScrollListener(this);
        }
    }
    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public T getItem(int position) {
        if(dataList == null || dataList.isEmpty()) return null;
        if(dataList instanceof List){
            return ((List<T>) dataList).get(position);
        }
        if(dataList instanceof Set){
            return new ArrayList<T>(dataList).get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    /**
     * 获取当前item的显示布局资源ID，各子类可以根据当前item数据(Mode对象类型,比如第一行显示一张网页图片，第二行就显示一个APk下载)
     * @param itemPosition
     * @return 当前位置需要显示的布局资源ID
     */
    protected abstract int getItemLayoutResId(int itemPosition);
    /**
     * 添加一个外部的也需要监听ListView滚动的监听
     * @param outListener
     */
    public void addOutScrollListener(OnScrollListener outListener){
        this.outScrollListener = outListener;
    }
    public void setViewTypeCount(int itemViewTypeCount){
        this.viewTypeCount = itemViewTypeCount;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AdapterViewHolder viewHolder = AdapterViewHolder.getViewHolder(convertView, parent, getItemLayoutResId(position), position);
        //具体的各子类填充视图
        convert(viewHolder, getItem(position), isScrolling, position);
        return viewHolder.getConvertView();
    }
    /**
     * 具体的各子类实现填充item视图
     * @param viewHolder
     * @param itemData
     * @param isScrolling
     */
    public void convert(AdapterViewHolder viewHolder, T itemData, boolean isScrolling) {
    }
    public void convert(AdapterViewHolder viewHolder, T itemData, boolean isScrolling, int position) {
        convert(viewHolder, itemData, isScrolling);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(OnScrollListener.SCROLL_STATE_IDLE == scrollState){
            isScrolling = false;
            notifyDataSetChanged();
        }
        else{
            isScrolling = true;
        }
        if(outScrollListener != null){
            outScrollListener.onScrollStateChanged(view, scrollState);
        }
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(outScrollListener != null){
            outScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    /**
     * 此处为相当于重置数据集
     * @param newData
     */
    public void setData(Collection<T> newData){
        dataList = newData;
        notifyDataSetChanged();
    }
    public void addItems(Collection<T> toAddItems){
        if(toAddItems != null){
            dataList.addAll(toAddItems);
        }
        notifyDataSetChanged();
    }
    public void addItems(T addedOne,boolean insertToFirst){
        if(insertToFirst){
            addItemToFirst(addedOne);
        }
        else{
            dataList.add(addedOne);
        }
        sortDataList();
        notifyDataSetChanged();
    }
    protected void addItemToFirst(T addedOne) {
        //Let subClass which can append to add item to implements this method
        addItems(addedOne, 0);
    }
    public void addItems(T addedOne){
        addItems(addedOne, false);
    }

    public void addItems(T toAddedOne, int insertPos) {
        if (toAddedOne == null || insertPos < 0) {
            return;
        }
        if (dataList == null) {
            ArrayList<T> newList = new ArrayList<>(1);
            newList.add(insertPos, toAddedOne);
            dataList = newList;
        }
        else{
            if (dataList instanceof List) {
                ((List) dataList).add(insertPos, toAddedOne);
            }
            else{
                dataList.add(toAddedOne);
            }
        }
    }

    /**
     * 替换数据集中某个位置的元素
     * 也有可能是添加一个新的元素
     * @param theRePlacePos
     * @param newOne
     */
    public void replaceItem(int theRePlacePos,T newOne) {
        if (theRePlacePos < 0) {
            return;
        }
        if (dataList != null) {
//            if (theRePlacePos >= dataList.size()) {//超出可替换的范围，则直接添加进来
//
//            }
            if (theRePlacePos >= dataList.size()) {
                return;
            }
            if (dataList instanceof List) {
                ((List) dataList).set(theRePlacePos, newOne);
            }
            else{
                if (dataList.remove(getItem(theRePlacePos))) {
                    addItems(newOne, theRePlacePos);
                }
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 直接使用新的一个数据实体来更新旧的
     * 如果要更新的新数据体本身不存在数据集中则直接添加进数据集
     * @param newItemData 要更新的新数据体
     */
    public void updateItem(T newItemData) {
        if (newItemData == null) {
            return;
        }
        int existedIndex = itemDataIndexInList(newItemData);
        if (existedIndex >= 0) {
            replaceItem(existedIndex, newItemData);//在数据集中原来位置上更新
        }
        else{
            addItems(newItemData);
        }
    }

    public int itemDataIndexInList(T newItemData) {
        int existedIndex = -1;//不存在当前数据集里
        if (newItemData != null) {
            int size = dataList.size();
            for (int i = 0; i < size; i++) {
                if (isExistedBaseWhat(newItemData, getItem(i))) {
                    existedIndex = i;
                    break;
                }
            }
        }
        return existedIndex;
    }

    /**
     * 比较两个数据体时依据何种情况来判断新数据体原来就存在于数据集中
     * @param newItemData 新数据（需要直接更新或者插入）
     * @param itemDataAdPos 当前数据集中某个位置index上的数据体
     * @return
     */
    protected boolean isExistedBaseWhat(T newItemData, T itemDataAdPos) {
        return false;
    }
    public void removeItem(int itemPosition) {
        if (itemPosition < 0) {
            return;
        }
        if (dataList != null) {
            boolean isEmpty = dataList.isEmpty();
            if (isEmpty) {
                return;
            }
            if (itemPosition >= dataList.size()) {
                return;
            }
            if (dataList instanceof List) {
                List<T> datas = (List<T>) dataList;
                datas.remove(itemPosition);
            }
            else{
                dataList.remove(getItem(itemPosition));
            }
            notifyDataSetChanged();
        }
    }
    public void clearData(){
        dataList.clear();
        notifyDataSetChanged();
    }
    //附加功能
    protected String getStrFromRes(int resStrID,Object... args){
        return mContext == null ? "" : mContext.getString(resStrID, args);
    }
    protected String getStrFromRes(int resStrID){
        return mContext == null ? "" : mContext.getString(resStrID);
    }
    protected String[] getArrStrFromRes(int resStrID){
        return (String[]) (mContext == null ? "" : mContext.getResources().getStringArray(resStrID));
    }
    public List<T> getDatas() {
        return new ArrayList<T>(dataList);
    }
    /**
     * 对数据集进行排序,各子类依不同排序规则实现
     */
    protected void sortDataList(){
    }
    Comparator<T> dataComparator;
}
