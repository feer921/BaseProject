package common.base.adapters;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/10/9<br>
 * Time: 20:37<br>
 * <P>DESC:
 * 关于多类型item view视图的 并且可选择item操作基类适配器
 * </p>
 * ******************(^_^)***********************
 */
public abstract class BaseSelectableMultiItemQuickAdapter<T extends MultiItemEntity, VH extends BaseViewHolder>
                extends BaseMultiItemQuickAdapter<T, VH> implements ASelectableDelegate.ISelectableObserver<T>,
        ASelectableDelegate.ISelectOptions<T> {
    protected ASelectableDelegate<T> selectableDelegate;

    protected int curAdapterMode = MODE_NORMAL;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     */
    public BaseSelectableMultiItemQuickAdapter() {
        super(null);
        selectableDelegate = new ASelectableDelegate<>();
        selectableDelegate.setSelectableObserver(this);
    }

    //----------------------------- @接口 ISelectableObserver 的基类可实行方法 start -----

    /**
     * 当有选择状态发生更改时，通知观察者数据发生了改变
     *
     * @param selectStateChangeItemPos 选中状态的更改的数据实体在受选数据集里的position,当为负数时，为全部刷新; 为>=0时
     *                                 表示仅position处的数据更改
     */
    @Override
    public void notifySelectDatasChanged(int selectStateChangeItemPos) {
        if (selectStateChangeItemPos < 0) {
            notifyDataChangedInVisibleScope();
        }
        else {
            refreshNotifyItemChanged(selectStateChangeItemPos);
        }
    }


    /**
     * 回调选中的数量，观察者自己可以再回调是否为全选
     *
     * @param selectedCount 选中的数量
     */
    @Override
    public void onSelected(int selectedCount) {
        if (chooseCallback != null) {
            boolean isAllSelected = selectedCount == getJustDataCount();
            if (selectedCount == 0) {
                isAllSelected = false;
            }
            chooseCallback.onSelected(this,selectedCount,isAllSelected);
        }
    }

    /**
     * 由观察者提供受选 的数据集数据
     *
     * @return 受选择的数据集
     */
    @Override
    public List<T> provideSelectSampleDatas() {
        return mData;
    }
    //----------------------------- @接口 ISelectableObserver 的基类可实行方法 end -----



    //----------------------------- @接口 ISelectOptions 的基类可实行方法 start -----

    /**
     * 操作之：判断形参数据是否被选中
     *
     * @param theItemData 要判断的数据
     * @return true:选中了；false:未选中
     */
    @Override
    public boolean isTheItemSelected(T theItemData) {
        if (selectableDelegate != null) {
            return selectableDelegate.isTheDataSelected(theItemData);
        }
        return false;
    }

    /**
     * 操作之：获取选中的惟一标识集
     *
     * @return 可标识选中的惟一的标识集
     */
    @Override
    public HashSet<Object> getSelectedUniqueMarks() {
        if (selectableDelegate != null) {
            selectableDelegate.getSelectedUniqueMarks();
        }
        return null;
    }

    /**
     * 操作之：挑出已经选中的数据
     *
     * @return 受选数据集里已经被标识为选中的数据集
     */
    @Override
    public List<T> pickOutSelectedDatas() {
        if (selectableDelegate != null) {
            selectableDelegate.pickOutSelectedDatas();
        }
        return null;
    }

    /**
     * 操作之：设置选中状态回调监听者
     *
     * @param selectStateListener 监听者
     */
    @Override
    public void setSelectStateListener(ASelectableDelegate.IChooseCallback selectStateListener) {
        this.chooseCallback = selectStateListener;
    }

    /**
     * 操作之：依惟一标识信息来标记选中或者取消选中状态
     *
     * @param isToSelectOrNot     true:选中；false:取消选中
     * @param itemDataUniqueMarks 可标记为选中状态的惟一标识集
     * @return true: 操作了；false: do nothing
     */
    @Override
    public boolean markSelectedOrNot(boolean isToSelectOrNot, Collection<Object> itemDataUniqueMarks) {
        if (selectableDelegate != null) {
            return selectableDelegate.markSelectedOrNot(isToSelectOrNot, itemDataUniqueMarks);
        }
        return false;
    }

    /**
     * 操作之：直接标记目标原数据集(受选数据集)为选中或者取消选中
     *
     * @param isToSelectOrNot true:选中；false:取消选中
     * @param itemDatas       受选数据集的原数据
     * @return true: 操作了；false: do nothing
     */
    @Override
    public boolean markItemDatasSelectedOrNot(boolean isToSelectOrNot, Collection<T> itemDatas) {
        if (selectableDelegate != null) {
            return selectableDelegate.markItemDatasSelectedOrNot(isToSelectOrNot, itemDatas);
        }
        return false;
    }

    /**
     * 操作之：依惟一标识来取消选中
     *
     * @param itemDataUniqueMark 可标志为选中状态的标识
     * @return true: 取消被选中生效；false:未操作
     */
    @Override
    public boolean markUnSelected(Object itemDataUniqueMark) {
        if (selectableDelegate != null) {
            return selectableDelegate.markUnSelected(itemDataUniqueMark);
        }
        return false;
    }

    /**
     * 操作之：依惟一标识来标志为选中
     *
     * @param itemDataUniqueMark 可标志为选中状态的标识
     * @return true:标记了；false:未操作
     */
    @Override
    public boolean markSelected(Object itemDataUniqueMark) {
        if (selectableDelegate != null) {
            return selectableDelegate.markSelected(itemDataUniqueMark);
        }
        return false;
    }

    /**
     * 操作之：直接对受选原数据标志为选中或者取消选中
     *
     * @param isSelectOrNot true:要选中；false:取消选中
     * @param itemData      受选原数据
     * @return true: 操作了； false: 未操作
     */
    @Override
    public boolean markItemDataSelectedOrNot(boolean isSelectOrNot, T itemData) {
        if (selectableDelegate != null) {
            return selectableDelegate.markItemDataSelectedOrNot(isSelectOrNot, itemData);
        }
        return false;
    }

    /**
     * 操作之：全选或者取消全选
     *
     * @param toSelectAll true: 要全选；false:取消全选
     */
    @Override
    public void selectAllOrCancelAll(boolean toSelectAll) {
        if (selectableDelegate != null) {
            selectableDelegate.selectAllOrCancelAll(toSelectAll);
        }
    }

    /**
     * 操作之： 受选数据集中某个position位置的数据点击了
     *
     * @param clickedPos 点击的位置
     * @return true:当前位置的item数据选中了; false:未选择/取消选中
     */
    @Override
    public boolean theItemClicked(int clickedPos) {
        if (selectableDelegate != null) {
            return selectableDelegate.willSelectTheItemData(clickedPos);
        }
        return false;
    }

    /**
     * 操作之：设置当前的选择模式
     *
     * @param adapterMode 单选；多选
     */
    @Override
    public void setCurSelectMode(int adapterMode) {
        if (selectableDelegate != null) {
            selectableDelegate.setCurSelectableMode(adapterMode);
        }
    }

    /**
     * 操作之：获取当前的选择模式
     *
     * @return 单选；多选
     */
    @Override
    public int getCurSelectMode() {
        return curAdapterMode;
    }

    /**
     * 操作之：清空已选择的惟一标识
     */
    @Override
    public void clearSelectedUniqMarks() {
        if (selectableDelegate != null) {
            selectableDelegate.clearSelectedUniqMarks();
        }
    }

    protected ASelectableDelegate.IChooseCallback chooseCallback;


    //----------------------------- @接口 ISelectOptions 的基类可实行方法 start -----
}
