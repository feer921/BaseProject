package common.base.adapters.viewholders

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */
abstract class WithRecyclerViewViewHolder<ItemData> : ACommonViewHolder<ItemData> {
    protected lateinit var recyclerView: RecyclerView

    constructor(itemView: View) : super(itemView)
    constructor(context: Context, itemViewLayoutRes: Int) : super(context, itemViewLayoutRes)
    constructor(context: Context, itemViewLayoutRes: Int, parentView: ViewGroup?) : super(context, itemViewLayoutRes, parentView)


}