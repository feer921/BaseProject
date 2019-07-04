package common.base.adapters;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/6/5<br>
 * Time: 20:59<br>
 * <P>DESC:
 * 支持单选、多选模式的适配器
 * </p>
 * ******************(^_^)***********************
 */
public abstract class AbsSelectableAdapter<T, VH extends BaseViewHolder> extends BaseQuickAdapter<T, VH> {
    /**
     * 用于标识列表数据被选中的唯一标识
     */
    protected HashSet<Object> selectedUniqueMarks;

    protected int curAdapterMode = MODE_NORMAL;
    /**
     * 模式：正常模式
     */
    public static final int MODE_NORMAL = 0;
    /**
     * 模式：单选模式
     */
    public static final int MODE_SINGLE_CHOICE = MODE_NORMAL + 1;
    /**
     * 模式：多选模式
     */
    public static final int MODE_MULTY_CHOICE = MODE_SINGLE_CHOICE + 1;

    public AbsSelectableAdapter(@Nullable List<T> data) {
        super(data);
    }

    /**
     * 切换模式，需要全局刷新适配器
     * @param adapterMode
     */
    public void setCurAdapterMode(int adapterMode) {
        if (this.curAdapterMode == adapterMode) {
            return;
        }
        if (adapterMode == MODE_NORMAL) {
            //要切成正常模式时，把之前如果在选择模式下选择的清空
            if (selectedUniqueMarks != null) {
                selectedUniqueMarks.clear();
            }
        }
        this.curAdapterMode = adapterMode;
        notifyDataSetChanged();
    }

    public int getCurAdapterMode() {
        return curAdapterMode;
    }

    /**
     * 去判断itemData数据能作为选中依据的唯一标识
     * @param theItemData
     * @return
     */
    protected abstract Object toJudgeItemDataUniqueMark(T theItemData);

    protected Object toJudgeItemDataUniqueMark(T theItemData, int itemPos) {
        return itemPos;
    }

    /**
     * 统一处理适配器中的可选择数据的点击、选择操作
     * @param clickedPos 当前点击的item位置
     * @return true:当前位置的item数据选中了; false:未选择/取消选中
     */
    public boolean theItemClicked(int clickedPos) {
        if (curAdapterMode == MODE_NORMAL) {
            return false;
        }
        boolean isSelected = false;
        Object theUniqueMarkOfData = toJudgeItemDataUniqueMark(getItem(clickedPos));
        //added by fee 2018-09-21 :判断当前点击的item是否已经在选中集合里了
        boolean isAlreadyAdded = false;
        if (selectedUniqueMarks == null) {
            selectedUniqueMarks = new HashSet<>();
        }
        else{
            isAlreadyAdded = selectedUniqueMarks.contains(theUniqueMarkOfData);
            if (curAdapterMode == MODE_SINGLE_CHOICE) {//如果当前是单选模式
                if (isAlreadyAdded) {//如果已经在选中集合里，可以不清空一遍并刷新列表了
                    return true;
                }
                selectedUniqueMarks.clear();
            }
        }
//        Object theUniqueMarkOfData = toJudgeItemDataUniqueMark(getItem(clickedPos));
        if (curAdapterMode == MODE_SINGLE_CHOICE) {//单选直接添加
            selectedUniqueMarks.add(theUniqueMarkOfData);
            isSelected = true;
            notifyDataSetChanged();//单选情况下，是需要全局刷新一下的
        }
        else{
//            boolean isAlready = selectedUniqueMarks.contains(theUniqueMarkOfData);
//            isSelected = !isAlready;
            isSelected = !isAlreadyAdded;
            if (isSelected) {
                selectedUniqueMarks.add(theUniqueMarkOfData);
            }
            else{
                selectedUniqueMarks.remove(theUniqueMarkOfData);
            }
            refreshNotifyItemChanged(clickedPos);//modified by fee 2019-07-04: 修正可能存在的headerView 导致的不position位置不对的问题
//            notifyItemChanged(clickedPos);
        }
        //回调选中信息
        callbackSelectedCase();
        return isSelected;
    }
    /**
     * 全选或者取消全选(只在多选的模式下)
     * @param toSelectAll
     */
    public void selectAllOrCancelAll(boolean toSelectAll) {
        if (curAdapterMode != MODE_MULTY_CHOICE) {
            return;
        }
        boolean effected = false;
        if (toSelectAll) {
            if (selectedUniqueMarks == null) {
                selectedUniqueMarks = new HashSet<>();
            }
            for (T itemData : mData) {
                selectedUniqueMarks.add(toJudgeItemDataUniqueMark(itemData));
            }
            effected = true;
        }
        else{
            if (selectedUniqueMarks != null && !selectedUniqueMarks.isEmpty()) {
                selectedUniqueMarks.clear();
                effected = true;
            }
        }
//        if (selectedUniqueMarks == null) {
//            selectedUniqueMarks = new HashSet<>();
//        }
//        if (!toSelectAll) {//取消全选
//            selectedUniqueMarks.clear();
//        }
//        else{//全选
//            for (T itemData : mData) {
//                selectedUniqueMarks.add(toJudgeItemDataUniqueMark(itemData));
//            }
//        }
        if (effected) {
            notifyDataSetChanged();
        }
        callbackSelectedCase();
    }

    public boolean markItemDataSelectedOrNot(boolean isSelectOrNot,T itemData) {
        if (itemData == null) {
            return false;
        }
        if (curAdapterMode != MODE_NORMAL) {
            if (!isSelectOrNot) {
                if (curAdapterMode == MODE_SINGLE_CHOICE) {
                    return false;
                }
            }
            if (isSelectOrNot) {
                return markSelected(toJudgeItemDataUniqueMark(itemData));
            }
            return markUnSelected(toJudgeItemDataUniqueMark(itemData));
        }
        return false;
    }
    /**
     *  根据item data数据的惟一标识，来标记一个数据应该被选中了
     * @param itemDataUniqueMark 要标识被选择的item data的唯一标识
     */
    public boolean markSelected(Object itemDataUniqueMark) {
        if (itemDataUniqueMark != null) {
            if (curAdapterMode != MODE_NORMAL) {
                if (selectedUniqueMarks == null) {
                    selectedUniqueMarks = new HashSet<>();
                }
                else{
                    if (curAdapterMode == MODE_SINGLE_CHOICE) {
                        selectedUniqueMarks.clear();
                    }
                }
                selectedUniqueMarks.add(itemDataUniqueMark);
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }
    /**
     * 标记某个item data 不要被选中
     * 注：只适用多选模式下
     * @param itemDataUniqueMark Item data 用来标记是否选中的唯一标识
     * @return true: 取消被选中生效；false:未操作
     */
    public boolean markUnSelected(Object itemDataUniqueMark) {
        if (curAdapterMode == MODE_MULTY_CHOICE) {
            if (selectedUniqueMarks != null) {
                boolean isEffected = selectedUniqueMarks.remove(itemDataUniqueMark);
                if (isEffected) {
                    notifyDataSetChanged();
                }
                return isEffected;
            }
        }
        return false;
    }
    /**
     * 根据T itemData的(选中唯一标识)来标记被选中或者移除选中
     * @param isToSelectOrNot true:要标记为选中状态；false:移除选中状态
     * @param itemDataUniqueMarks 要选中/取消选中的 itemDatas 的标识
     * @return true:操作了(更新列表); false: do nothing
     */
    public boolean markSelectedOrNot(boolean isToSelectOrNot,Collection<Object> itemDataUniqueMarks) {
        if (curAdapterMode == MODE_MULTY_CHOICE) {
            if (itemDataUniqueMarks != null && !itemDataUniqueMarks.isEmpty()) {
                if (selectedUniqueMarks == null) {
                    if (!isToSelectOrNot) {
                        return false;
                    }
                    selectedUniqueMarks = new HashSet<>(itemDataUniqueMarks);
                }
                else{
                    if (isToSelectOrNot) {
                        selectedUniqueMarks.addAll(itemDataUniqueMarks);
                    }
                    else{
                        selectedUniqueMarks.removeAll(itemDataUniqueMarks);
                    }
                }
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    public boolean markItemDatasSelectedOrNot(boolean isToSelectOrNot,Collection<T> itemDatas) {
        if (curAdapterMode == MODE_MULTY_CHOICE) {
            if (!isToSelectOrNot) {
                if (selectedUniqueMarks == null || selectedUniqueMarks.isEmpty()) {
                    return false;
                }
            }
            if (itemDatas != null) {
                ArrayList<Object> itemDataUniqueMarks = null;
                for (T itemData : itemDatas) {
                    Object uniqueMarkOfItemData = toJudgeItemDataUniqueMark(itemData);
                    if (uniqueMarkOfItemData != null) {
                        if (itemDataUniqueMarks == null) {
                            itemDataUniqueMarks = new ArrayList<>(itemDatas.size());
                        }
                        itemDataUniqueMarks.add(uniqueMarkOfItemData);
                    }
                }
                if (itemDataUniqueMarks != null && !itemDataUniqueMarks.isEmpty()) {
                    return markSelectedOrNot(isToSelectOrNot, itemDataUniqueMarks);
                }
            }
        }
        return false;
    }

    protected IChooseCallback callback;
    protected void callbackSelectedCase() {
        if (callback != null) {
            int hasSelectedCount = selectedUniqueMarks == null ? 0 : selectedUniqueMarks.size();
            boolean isAllSelected = hasSelectedCount == getJustDataCount();
            if (hasSelectedCount == 0) {
                isAllSelected = false;
            }
            callback.onSelected(this,hasSelectedCount, isAllSelected);
        }
    }

    public void setSelectedCallback(IChooseCallback callback) {
        this.callback = callback;
    }

    protected boolean isTheItemSelected(T theItemData) {
        if (selectedUniqueMarks != null) {
            Object theItemUnqiuMark = toJudgeItemDataUniqueMark(theItemData);
//            boolean isTheItemMarkIn = selectedUniqueMarks.contains(theItemUnqiuMark);
            return selectedUniqueMarks.contains(theItemUnqiuMark);//如果 theItemUnqiuMark = "",结果是包含,true ???
//            return isTheItemMarkIn;
        }
        return false;
    }
    public List<T> pickOutSelectedDatas() {
        if (selectedUniqueMarks != null && !selectedUniqueMarks.isEmpty()) {
            List<T> selectedDatas = null;
            List<T> theListDatas = new ArrayList<>(mData);
            for (T itemData : theListDatas) {
                boolean isTheItemSelected = selectedUniqueMarks.contains(toJudgeItemDataUniqueMark(itemData));
                if (isTheItemSelected) {
                    if (selectedDatas == null) {
                        selectedDatas = new ArrayList<>();
                    }
                    selectedDatas.add(itemData);
                }
            }
            return selectedDatas;
        }
        return null;
    }
    public interface IChooseCallback{
        void onSelected(AbsSelectableAdapter curAdapter,int selectedCount, boolean isAllSelected);
    }

    public HashSet<Object> getSelectedUniqueMarks() {
        return selectedUniqueMarks;
    }
}
