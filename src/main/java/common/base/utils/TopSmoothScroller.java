package common.base.utils;

import android.content.Context;
import androidx.recyclerview.widget.LinearSmoothScroller;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/10/10<br>
 * Time: 15:15<br>
 * <P>DESC:
 * 供RecyclerView作平滑滚动到指定position的滚动者
 * 有一个平滑滚动效果，并且可以滚动到对应position位置并置顶
 * </p>
 * ******************(^_^)***********************
 */
public class TopSmoothScroller extends LinearSmoothScroller {

    public TopSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected int getHorizontalSnapPreference() {
        return SNAP_TO_START;//具体见源码注释
    }
    @Override
    protected int getVerticalSnapPreference() {
        return SNAP_TO_START;//具体见源码注释
    }
}
