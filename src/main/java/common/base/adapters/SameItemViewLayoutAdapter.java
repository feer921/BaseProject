package common.base.adapters;

import androidx.annotation.Nullable;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;
import common.base.R;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/6/7<br>
 * Time: 15:12<br>
 * <P>DESC:
 * 有相同的item布局(左边TextView,右侧ImageView)的适配器,如有不同之处，则需要子类区分处理，比如itemview的各方向padding不一样
 * </p>
 * ******************(^_^)***********************
 */
public abstract class SameItemViewLayoutAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    protected int itemLeftPadding = -1;
    protected int itemRightPadding = -1;
    protected int itemTopPadding = -1;
    protected int itemBottomPadding = -1;

    public void setNeedAdjustItemViewLayout(boolean needAdjustItemViewLayout) {
        this.needAdjustItemViewLayout = needAdjustItemViewLayout;
    }

    protected boolean needAdjustItemViewLayout = true;

    public SameItemViewLayoutAdapter(@Nullable List<T> data) {
        super(data);
    }
    public void setItemPadding(int leftPadding, int topPadding, int rightPadding, int bottomPadding) {
        this.itemLeftPadding = leftPadding;
        this.itemTopPadding = topPadding;
        this.itemRightPadding = rightPadding;
        this.itemBottomPadding = bottomPadding;
    }

    protected void recoverItemViewPaddingInfos(View itemView) {
        if (itemLeftPadding == -1 && itemTopPadding == -1 && itemRightPadding == -1 && itemBottomPadding == -1) {
            return;
        }
        int oldTopPadding = itemView.getPaddingTop();
        int oldBottomPadding = itemView.getPaddingBottom();

        itemView.setPadding(0, oldTopPadding, 0, oldBottomPadding);
    }
    protected void adjustItemViewPaddingInfos(View itemViewPaddingInfos) {
        if (itemLeftPadding == -1 && itemTopPadding == -1 && itemRightPadding == -1 && itemBottomPadding == -1) {
            return;
        }
        int leftPadding = itemViewPaddingInfos.getPaddingLeft();
        int topPadding = itemViewPaddingInfos.getPaddingTop();
        int rightPadding = itemViewPaddingInfos.getPaddingRight();
        int bottomPadding = itemViewPaddingInfos.getPaddingBottom();

        boolean leftPaddingSame = leftPadding == itemLeftPadding;
        boolean topPaddingSame = topPadding == itemTopPadding;
        boolean rightPaddingSame = rightPadding == itemRightPadding;
        boolean bottomPaddingSame =  bottomPadding == itemBottomPadding;

        if (leftPaddingSame
                &&
                topPaddingSame
                &&
                rightPaddingSame
                &&
                bottomPaddingSame
                ) {
            return;
        }
//        this.itemTopPadding = topPadding;// TODO: 2018/6/7 ???????
//        this.itemBottomPadding  = bottomPadding;
        int newLeft = itemLeftPadding > 0 ? itemLeftPadding : leftPadding;
        int newTop = itemTopPadding > 0 ? itemTopPadding : topPadding;
        int newRight = itemRightPadding > 0 ? itemRightPadding : rightPadding;
        int newBottom = itemBottomPadding > 0 ? itemBottomPadding : bottomPadding;
        itemViewPaddingInfos.setPadding(newLeft, newTop, newRight, newBottom);
    }

    public void setItemLeftPadding(int positiveLeftPadding) {
        if (positiveLeftPadding > 0) {
            this.itemLeftPadding = positiveLeftPadding;
        }
    }

    public void setItemRightPadding(int positiveRightPadding) {
        if (positiveRightPadding > 0) {
            this.itemRightPadding = positiveRightPadding;
        }
    }
    @Override
    protected final int providedContentViewResId() {
        return R.layout.item_text_imageview;
    }

    @Override
    protected final void convert(BaseViewHolder viewHolder, T item) {
        if (needAdjustItemViewLayout) {
            adjustItemViewPaddingInfos(viewHolder.itemView);
        }
        else{
            //恢复原来的item布局状态
            recoverItemViewPaddingInfos(viewHolder.itemView);
        }
        convertItemView(viewHolder, item);
    }

    protected abstract void convertItemView(BaseViewHolder viewHolder, T itemData);
}
