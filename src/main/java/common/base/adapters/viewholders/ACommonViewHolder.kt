package common.base.adapters.viewholders

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */
abstract class ACommonViewHolder<ItemData> : LocalViewHolder  {
    constructor(itemView: View) : super(itemView){
        initViews()
    }
    constructor(context: Context, @LayoutRes itemViewLayoutRes: Int) : this(View.inflate(context, itemViewLayoutRes, null)) {
    }
    constructor(context: Context, @LayoutRes itemViewLayoutRes: Int, parentView: ViewGroup?) : this(View.inflate(context, itemViewLayoutRes, parentView))

    fun initViews() {
        //can do nothing...
    }
    abstract fun convertByItemData(curItemData: ItemData)

}