package common.base.adapters;


import android.support.annotation.CallSuper;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;

import common.base.R;
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
    @CallSuper
    @Override
    protected void convert(VH viewHolder, T item) {
        View itemView = viewHolder.itemView;
        if (itemView != null) {//因为本Adapter默认没有item layout,而是交由子类提供，所以这里做一下重新设置itemview的回调处理
            Object relayoutTag = itemView.getTag(R.id.view_common_tag_id);
            if (relayoutTag == null || "".equals(relayoutTag.toString())) {//没有重置itemView
                itemView.setTag(R.id.view_common_tag_id,relayoutItemView(itemView));
            }
        }
    }

    /**
     * 重新设置item view的布局
     * @param itemView
     * @return 如果设置了，则返回任意的非空的字符串
     */
    protected String relayoutItemView(View itemView//, Object relayoutTag
                                       ) {
        return "";
    }

    protected String relayoutItemView(VH viewHolder) {
        return "";
    }
}
