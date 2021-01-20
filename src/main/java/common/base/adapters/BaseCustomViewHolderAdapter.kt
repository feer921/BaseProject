package common.base.adapters

import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * 需要 创建自定义 ViewHolder 的 Adapter 基类
 * </p>
 * ******************(^_^)***********************
 */
abstract class BaseCustomViewHolderAdapter<D, VH : BaseViewHolder> : BaseQuickAdapter<D, VH>(null) {
    /**
     * 因为子类可能需要 根据具体的 item data 数据，来返回 当前 item data的数据类型
     */
    abstract override fun getDefItemViewType(position: Int): Int

    /**
     * 因为要使用自定义 的ViewHolder，所以本类把该方法再 变更为[abstract]
     */
    abstract override fun onCreateDefViewHolder(parent: ViewGroup?, viewType: Int): VH
}