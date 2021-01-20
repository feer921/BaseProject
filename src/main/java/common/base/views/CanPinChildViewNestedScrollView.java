package common.base.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import common.base.utils.CommonLog;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * 可以 将 本类的某个 子View 吸顶的 [NestedScrollView]
 * 实现原理为：要吸顶的 ViewGroup(该容器View包含滚动子View)的高度设置成 本类的测量高度，则当垂直方向向上滑动时，滑动 该要吸顶的ViewGroup，因为它的高度正好是
 * 本类 的高度，则不能再向上滑动了
 * </p>
 * ******************(^_^)***********************
 */
public class CanPinChildViewNestedScrollView extends NestedScrollView {
    private final String TAG = "CanPinChildViewNestedScrollView";
    private ViewGroup needPinnedView;
    private View topView;
    public CanPinChildViewNestedScrollView(@NonNull Context context) {
        this(context,null);
    }

    public CanPinChildViewNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        needPinnedView = findViewWithTag("need_pinned_view");
        topView = findViewWithTag("top_view");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (needPinnedView != null) {
            ViewGroup.LayoutParams vlp = needPinnedView.getLayoutParams();
            if (vlp != null) {
                vlp.height = getMeasuredHeight();
            }
            needPinnedView.setLayoutParams(vlp);
            CommonLog.e(TAG, " --> onMeasure()  vlp.height = " + vlp.height);
        }
    }

    /**
     * 为什么重写这个，不回调呢？？？？
     * @param target
     * @param dx
     * @param dy
     * @param consumed
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        CommonLog.i(TAG, " --> onNestedPreScroll() target = " + target + " dx = " + dx + " dy = " + dy );
        super.onNestedPreScroll(target, dx, dy, consumed);
    }

    /**
     * 嵌套的子类 在 滚动之前，询问父类，是否需要处理
     * @param target 现在将要滚动的 子 View
     * @param dx
     * @param dy
     * @param consumed
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed,int type) {
        CommonLog.d(TAG, " --> onNestedPreScroll() target = " + target + " dx = " + dx + " dy = " + dy + " type = " + type);
//        boolean myselfNeedScroll = dy>0 && getSc
        if (dy > 0) {
            if (topView != null) {
                if (getScrollY() < topView.getMeasuredHeight()) {//当前本 CanPinChildViewNestedScrollView Y 轴方向滚动的值小于
                    //需要 固定的视图的高,则本类需要滚动
                    scrollBy(0,dy);
                    consumed[1] = dy;//告诉子类，本类消费了 dy的距离
                }
            }
        }
    }

}
