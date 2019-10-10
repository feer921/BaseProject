package common.base.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/10/9<br>
 * Time: 16:23<br>
 * <P>DESC:
 * 一个可对受选择样本集进行选择操作的 代表者
 * 将选择操作功能独立成一个被委托者实现的功能
 * </p>
 * ******************(^_^)***********************
 */
public class ASelectableDelegate<T> {

    /**
     * 受选样本数据集,由外部提供
     * 是否有必要？
     */
    protected List<T> datasOfSelectable;
    /**
     * 用于标识数据被选中的唯一标识
     */
    protected HashSet<Object> selectedUniqueMarks;


    /**
     * 当前可选择的模式：
     * <ul>
     *     <li>单选</li>
     *     <li>多选</li>
     * </ul>
     */
    protected int curSelectableMode = MODE_NORMAL;
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

    public void setCurSelectableMode(int curSelectableMode) {
        if (this.curSelectableMode == curSelectableMode) {
            return;
        }
        if (curSelectableMode == MODE_NORMAL) {
            //要切成正常模式时，把之前如果在选择模式下选择的清空
            if (selectedUniqueMarks != null) {
                selectedUniqueMarks.clear();
            }
        }
        this.curSelectableMode = curSelectableMode;
        notifyDataSetChanged();
    }

    /**
     * 获取当前的选择模式
     * @return <ul> <li>{@link #MODE_MULTY_CHOICE}</li> <li>{@link #MODE_SINGLE_CHOICE} </li>
     * <li>{@link #MODE_NORMAL} </li></ul>
     */
    public int getCurSelectableMode() {
        return this.curSelectableMode;
    }

    private ISelectableObserver<T> selectableObserver;

    private void notifyDataSetChanged(){
        if (selectableObserver != null) {
            selectableObserver.notifySelectDatasChanged(-1);
        }
    }

    private void refreshNotifyItemChanged(int itemDataPosition) {
        if (selectableObserver != null) {
            selectableObserver.notifySelectDatasChanged(itemDataPosition);
        }
    }
    public HashSet<Object> getSelectedUniqueMarks() {
        return selectedUniqueMarks;
    }


    public boolean isTheDataSelected(T theItemData) {
        if (selectedUniqueMarks != null) {
            Object theItemUnqiuMark = toJudgeItemDataUniqueMark(theItemData);
//            boolean isTheItemMarkIn = selectedUniqueMarks.contains(theItemUnqiuMark);
            return selectedUniqueMarks.contains(theItemUnqiuMark);//如果 theItemUnqiuMark = "",结果是包含,true ???
//            return isTheItemMarkIn;
        }
        return false;
    }

    /**
     * 统一处理受选样本集中的可选择数据的点击、选择操作
     * @param willSelectItemPos 当前点击的item位置
     * @return true:当前位置的item数据选中了; false:未选择/取消选中
     */
    public boolean willSelectTheItemData(int willSelectItemPos) {
        if (curSelectableMode == MODE_NORMAL || willSelectItemPos < 0) {
            return false;
        }
        boolean isSelected = false;
        T theDataAtItemPos = null;
        List<T> sampleDatas = toSelectSampleDatas();
        if (sampleDatas != null) {
            int dataSize = sampleDatas.size();
            if (willSelectItemPos < dataSize) {
                theDataAtItemPos = sampleDatas.get(willSelectItemPos);
            }
        }
        if (theDataAtItemPos == null) {
            return false;
        }
        Object theUniqueMarkOfData = toJudgeItemDataUniqueMark(theDataAtItemPos);
        boolean isAlreadyAdded = false;
        if (selectedUniqueMarks == null) {
            selectedUniqueMarks = new HashSet<>();
        }
        else{
            isAlreadyAdded = selectedUniqueMarks.contains(theUniqueMarkOfData);
            if (curSelectableMode == MODE_SINGLE_CHOICE) {//如果当前是单选模式
                if (isAlreadyAdded) {//如果已经在选中集合里，可以不清空一遍并刷新列表了
                    return true;
                }
                selectedUniqueMarks.clear();
            }
        }
//        Object theUniqueMarkOfData = toJudgeItemDataUniqueMark(getItem(clickedPos));
        if (curSelectableMode == MODE_SINGLE_CHOICE) {//单选直接添加
            selectedUniqueMarks.add(theUniqueMarkOfData);
            isSelected = true;
            notifyDataSetChanged();//单选情况下，是需要全局刷新一下的,因为要把之前的单选更换掉
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
            refreshNotifyItemChanged(willSelectItemPos);
        }
        //回调选中信息
        callbackSelectedCase();
        return isSelected;
    }

    /**
     * 挑出被选中的样本数据
     * @return
     */
    public List<T> pickOutSelectedDatas() {
        if (selectedUniqueMarks != null && !selectedUniqueMarks.isEmpty()) {
            List<T> selectedDatas = null;
            List<T> theListDatas = new ArrayList<>(toSelectSampleDatas());
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

    private Object toJudgeItemDataUniqueMark(T theItemData){
        if (selectableObserver != null) {
            return selectableObserver.toJudgeItemDataUniqueMark(theItemData);
        }
        return null;
    }

    /**
     * 全选或者取消全选(只在多选的模式下有效，单选模式下，没有全选的操作)
     * @param toSelectAll true:去全选；false:取消全选
     */
    public void selectAllOrCancelAll(boolean toSelectAll) {
        if (curSelectableMode != MODE_MULTY_CHOICE) {
            return;
        }
        boolean effected = false;
        if (toSelectAll) {
            if (selectedUniqueMarks == null) {
                selectedUniqueMarks = new HashSet<>();
            }
            List<T> sampleSelectDatas = toSelectSampleDatas();
            if (sampleSelectDatas != null) {
                for (T itemData : sampleSelectDatas) {
                    selectedUniqueMarks.add(toJudgeItemDataUniqueMark(itemData));
                }
                effected = true;
            }
        }
        else{
            if (selectedUniqueMarks != null && !selectedUniqueMarks.isEmpty()) {
                selectedUniqueMarks.clear();
                effected = true;
            }
        }
        if (effected) {
            notifyDataSetChanged();
        }
        callbackSelectedCase();
    }

    public boolean markItemDataSelectedOrNot(boolean isSelectOrNot,T itemData) {
        if (itemData == null) {
            return false;
        }
        if (curSelectableMode != MODE_NORMAL) {
            if (!isSelectOrNot) {
                if (curSelectableMode == MODE_SINGLE_CHOICE) {//单选模式时不支持取消选中
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
            if (curSelectableMode != MODE_NORMAL) {
                if (selectedUniqueMarks == null) {
                    selectedUniqueMarks = new HashSet<>();
                }
                else{
                    if (curSelectableMode == MODE_SINGLE_CHOICE) {
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
        if (curSelectableMode == MODE_MULTY_CHOICE) {
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
        if (curSelectableMode == MODE_MULTY_CHOICE) {
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

    public boolean markItemDatasSelectedOrNot(boolean isToSelectOrNot, Collection<T> itemDatas) {
        if (curSelectableMode == MODE_MULTY_CHOICE) {
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

    /**
     * 获取受选择的样本数据集
     * 优秀使用 观察者所提供的样本集
     * @return 受选择的样本数据集
     */
    private List<T> toSelectSampleDatas() {
        List<T> sampleDatas = this.datasOfSelectable;
        List<T> providedSampleDatas = null;
        if (selectableObserver != null) {
            providedSampleDatas = selectableObserver.provideSelectSampleDatas();
        }
        return providedSampleDatas != null ? providedSampleDatas : sampleDatas;
    }

    private void callbackSelectedCase() {
        if (selectableObserver != null) {
            int hasSelectedCount = selectedUniqueMarks == null ? 0 : selectedUniqueMarks.size();
//            boolean isAllSelected = hasSelectedCount == getJustDataCount();
//            if (hasSelectedCount == 0) {
//                isAllSelected = false;
//            }
            selectableObserver.onSelected(hasSelectedCount/*,isAllSelected*/);
        }
    }

    public void setSelectableObserver(ISelectableObserver<T> selectableObserver) {
        this.selectableObserver = selectableObserver;
    }

    public void setDatasOfSelectable(List<T> datasOfSelectable) {
        this.datasOfSelectable = datasOfSelectable;
    }

    public void clearSelectedUniqMarks() {
        if (selectedUniqueMarks != null) {
            selectedUniqueMarks.clear();
        }
    }
    /**
     * 定义一下关于可选择的操作方法
     * 其实具体的实现还是 ASelectableDelegate
     * @param <T>
     */
    public interface ISelectOptions<T>{
        /**
         * 模式：正常模式
         */
        int MODE_NORMAL = 0;
        /**
         * 模式：单选模式
         */
        int MODE_SINGLE_CHOICE = MODE_NORMAL + 1;
        /**
         * 模式：多选模式
         */
        int MODE_MULTY_CHOICE = MODE_SINGLE_CHOICE + 1;

        /**
         * 操作之：判断形参数据是否被选中
         * @param theItemData 要判断的数据
         * @return true:选中了；false:未选中
         */
        boolean isTheItemSelected(T theItemData);

        /**
         * 操作之：获取选中的惟一标识集
         * @return 可标识选中的惟一的标识集
         */
        HashSet<Object> getSelectedUniqueMarks();

        /**
         * 操作之：挑出已经选中的数据
         * @return 受选数据集里已经被标识为选中的数据集
         */
        List<T> pickOutSelectedDatas();

        /**
         * 操作之：设置选中状态回调监听者
         * @param selectStateListener 监听者
         */
        void setSelectStateListener(IChooseCallback selectStateListener);

        /**
         * 操作之：依惟一标识信息来标记选中或者取消选中状态
         * @param isToSelectOrNot true:选中；false:取消选中
         * @param itemDataUniqueMarks 可标记为选中状态的惟一标识集
         * @return true: 操作了；false: do nothing
         */
        boolean markSelectedOrNot(boolean isToSelectOrNot, Collection<Object> itemDataUniqueMarks);

        /**
         * 操作之：直接标记目标原数据集(受选数据集)为选中或者取消选中
         * @param isToSelectOrNot true:选中；false:取消选中
         * @param itemDatas 受选数据集的原数据
         * @return true: 操作了；false: do nothing
         */
        boolean markItemDatasSelectedOrNot(boolean isToSelectOrNot, Collection<T> itemDatas);

//        void callbackSelectedCase();

        /**
         * 操作之：依惟一标识来取消选中
         * @param itemDataUniqueMark 可标志为选中状态的标识
         * @return true: 取消被选中生效；false:未操作
         */
        boolean markUnSelected(Object itemDataUniqueMark);

        /**
         * 操作之：依惟一标识来标志为选中
         * @param itemDataUniqueMark 可标志为选中状态的标识
         * @return true:标记了；false:未操作
         */
        boolean markSelected(Object itemDataUniqueMark);

        /**
         * 操作之：直接对受选原数据标志为选中或者取消选中
         * @param isSelectOrNot true:要选中；false:取消选中
         * @param itemData 受选原数据
         * @return true: 操作了； false: 未操作
         */
        boolean markItemDataSelectedOrNot(boolean isSelectOrNot, T itemData);

        /**
         * 操作之：全选或者取消全选
         * @param toSelectAll true: 要全选；false:取消全选
         */
        void selectAllOrCancelAll(boolean toSelectAll);

        /**
         * 操作之： 受选数据集中某个position位置的数据点击了
         * @param clickedPos 点击的位置
         * @return true:当前位置的item数据选中了; false:未选择/取消选中
         */
        boolean theItemClicked(int clickedPos);

        /**
         * 操作之：设置当前的选择模式
         * @param adapterMode 单选；多选
         */
        void setCurSelectMode(int adapterMode);

        /**
         * 操作之：获取当前的选择模式
         * @return 单选；多选
         */
        int getCurSelectMode();

        /**
         * 操作之：清空已选择的惟一标识
         */
        void clearSelectedUniqMarks();
    }

    public interface IChooseCallback{
        void onSelected(ISelectableObserver curSelectableObserver, int selectedCount, boolean isAllSelected);
    }
    /**
     * 可选观察者
     */
    public interface ISelectableObserver<T>{
        /**
         * 当有选择状态发生更改时，通知观察者数据发生了改变
         * @param selectStateChangeItemPos 选中状态的更改的数据实体在受选数据集里的position,当为负数时，为全部刷新; 为>=0时
         *                                 表示仅position处的数据更改
         */
        void notifySelectDatasChanged(int selectStateChangeItemPos);

        /**
         * 观察者要返回 如何来判断受选的样本数据集里每个数据里可标记为选中的标识
         * @param theData 受选样本
         * @return 可被标记为选中的标识对象(一般为唯一标识)
         */
        Object toJudgeItemDataUniqueMark(T theData);


        /**
         * 回调选中的数量，观察者自己可以再回调是否为全选
         * @param selectedCount 选中的数量
         */
        void onSelected(int selectedCount/*, boolean isAllSelected*/);

        /**
         * 由观察者提供受选 的数据集数据
         * @return 受选择的数据集
         */
        List<T> provideSelectSampleDatas();
    }
}
