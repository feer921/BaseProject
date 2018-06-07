package common.base.adapters;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/6/6<br>
 * Time: 15:36<br>
 * <P>DESC:
 * 用列表中的item的位置信息来标识选中状态的适配器
 * </p>
 * ******************(^_^)***********************
 */
public abstract class JustHoldPositionSelectAdapter<T, VH extends BaseViewHolder> extends AbsSelectableAdapter<T, VH> {
    protected int singleSelectedPos = -1;
    public JustHoldPositionSelectAdapter(@Nullable List<T> data) {
        super(data);
    }

    @Override
    protected final Object toJudgeItemDataUniqueMark(T theItemData) {
        return null;
    }

    @Override
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
            int datasSize = getJustDataCount();
            for(int i = 0; i < datasSize;i++) {
                selectedUniqueMarks.add(i);
            }
        }
        notifyDataSetChanged();
        callbackSelectedCase();
    }

    @Override
    public boolean theItemClicked(int clickedPos) {
        if (curAdapterMode == MODE_NORMAL) {
            return false;
        }
        boolean isSelected;
        if (selectedUniqueMarks == null) {
            selectedUniqueMarks = new HashSet<>();
        }
        else{
            if (curAdapterMode == MODE_SINGLE_CHOICE) {//如果当前是单选模式
                selectedUniqueMarks.clear();
            }
        }
        if (curAdapterMode == MODE_SINGLE_CHOICE) {//单选直接添加
            selectedUniqueMarks.add(clickedPos);
            isSelected = true;
            singleSelectedPos = clickedPos;
            notifyDataSetChanged();//单选情况下，是需要全局刷新一下的
        }
        else{
            boolean isAlready = selectedUniqueMarks.contains(clickedPos);
            isSelected = !isAlready;
            if (isSelected) {
                selectedUniqueMarks.add(clickedPos);
            }
            else{
                selectedUniqueMarks.remove(clickedPos);
            }
            notifyItemChanged(clickedPos);
        }
        //回调选中信息
        callbackSelectedCase();
        return isSelected;
    }

    protected boolean isTheItemSelected(int itemPos) {
        if (selectedUniqueMarks != null) {
            return selectedUniqueMarks.contains(itemPos);
        }
        return false;
    }
    @Override
    public List<T> pickOutSelectedDatas() {
        List<T> results = null;
        if (selectedUniqueMarks != null) {
            int selectedCount = selectedUniqueMarks.size();
            if (selectedCount > 0) {
                results = new ArrayList<>(selectedCount);
                if (curAdapterMode == MODE_SINGLE_CHOICE) {
                    results.add(getItem(singleSelectedPos));
                }
                else{
                    Iterator<Object> iterator = selectedUniqueMarks.iterator();
                    while (iterator.hasNext()) {
                        Object obj = iterator.next();
                        if (obj != null) {
                            int theSelectedPos = (int) obj;
                            results.add(getItem(theSelectedPos));
                        }
                    }
                }
            }
        }
        return results;
    }

    public T getSingleChoseItem() {
        if (curAdapterMode == MODE_SINGLE_CHOICE) {
            if (singleSelectedPos != -1) {
                return getItem(singleSelectedPos);
            }
        }
        return null;
    }
}
