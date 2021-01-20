package common.base.adapters

import android.graphics.Color
import android.view.View
import androidx.annotation.ColorInt
import common.base.adapters.viewholders.LocalViewHolder
import common.base.utils.CheckUtil

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */
abstract class AbsSelectableLocalViewHolderAdapter<D> : AbsSelectableAdapter<D, LocalViewHolder>(null) {
    /**
     * 如果使用了 [BaseViewHolder]的子类，则
     * 一般重写该方法，返回
     */
    override fun createBaseViewHolder(view: View?): LocalViewHolder {
        return LocalViewHolder(view)
    }

    fun parseStrColor(colorFormatStr: String?,@ColorInt fallbackColorIntAtFail: Int): Int {
        return try {
            if (colorFormatStr.isNullOrBlank()) {
                fallbackColorIntAtFail
            }
            else{
                Color.parseColor(colorFormatStr)
            }
        } catch (ex: Exception) {
            fallbackColorIntAtFail
        }
    }
    fun parseStrColor(colorFormatStr: String?, fallbackColorStr: String): Int {
        return if (!colorFormatStr.isNullOrBlank()) {
            try {
                Color.parseColor(colorFormatStr)
            } catch (ex: Exception) {
                parseStrColor(fallbackColorStr, 0)
            }
        }else{
            parseStrColor(fallbackColorStr,0)
        }
    }

    fun judgeVisibleByText(contentText: CharSequence?, willGoneAtNoText: Boolean = true): Int {
        return if (CheckUtil.isEmpty(contentText)) {
            if(willGoneAtNoText){
                View.GONE
            }
            else{
                View.INVISIBLE
            }
        }else(View.VISIBLE)
    }

}