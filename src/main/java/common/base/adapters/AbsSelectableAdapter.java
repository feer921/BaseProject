package common.base.adapters;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
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

    public static final int MODE_NORMAL = 0;
    public static final int MODE_SINGLE_CHOICE = MODE_NORMAL + 1;
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
            notifyItemChanged(clickedPos);
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
        if (selectedUniqueMarks == null) {
            selectedUniqueMarks = new HashSet<>();
        }
        if (!toSelectAll) {//取消全选
            selectedUniqueMarks.clear();
        }
        else{//全选
            for (T itemData : mData) {
                selectedUniqueMarks.add(toJudgeItemDataUniqueMark(itemData));
            }
        }
        notifyDataSetChanged();
        callbackSelectedCase();
    }

    /**
     *  根据item data数据的惟一标识，来标记一个数据应该被选中了
     * @param itemDataUniqueMark 要标识被选择的item data的唯一标识
     */
    public void markSekected(Object itemDataUniqueMark) {
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
            }
        }
    }
    protected IChooseCallback callback;
    protected void callbackSelectedCase() {
        if (callback != null) {
            int hasSelectedCount = selectedUniqueMarks.size();
            boolean isAllSelected = hasSelectedCount == getJustDataCount();
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
}
