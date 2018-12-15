package common.base.adapters;


import com.chad.library.adapter.base.BaseViewHolder;
import common.base.beans.IHasTextData;


/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/12/11<br>
 * Time: 11:10<br>
 * <P>DESC:
 * 数据实体里仅仅通过自有的文本属性来判断选中状态的适配器
 * 注：可单选、多选
 * </p>
 * ******************(^_^)***********************
 */
public class JustTextSelectableAdapter<T extends IHasTextData,VH extends BaseViewHolder> extends AbsSelectableAdapter<T,VH> {
    public JustTextSelectableAdapter() {
        super(null);
    }

    /**
     * 去判断itemData数据能作为选中依据的唯一标识
     *
     * @param theItemData
     * @return
     */
    @Override
    protected Object toJudgeItemDataUniqueMark(T theItemData) {
        return theItemData.getTextData();
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param viewHolder A fully initialized viewHolder.
     * @param item       The item that needs to be displayed.
     */
    @Override
    protected void convert(VH viewHolder, T item) {

    }
}
