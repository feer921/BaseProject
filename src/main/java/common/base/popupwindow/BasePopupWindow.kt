package common.base.popupwindow

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat

/**
 * @author fee
 * <P> DESC:
 * PopupWindow 的基类
 * 注意：使用子类时最好调用一下 [build] 方法
 * </P>
 */
abstract class BasePopupWindow<I : BasePopupWindow<I>>(protected val mContext: Context) : PopupWindow(mContext) {

    @LayoutRes
    private var mContentViewResId: Int? = null

    @LayoutRes
    protected abstract fun providerContentLayoutRes(): Int?

    protected open fun providerContentView(): View? = null

    fun withWidth(popWidth: Int): I {
        width = popWidth
        return self()
    }


    fun withHeight(popHeight: Int):I {
        height = popHeight
        return self()
    }

    fun withBackground(backgroundDrawabl1e: Drawable?):I {
        setBackgroundDrawable(backgroundDrawabl1e)
        return self()
    }
    fun withBackground(@ColorInt bgColor: Int) :I{
        return withBackground(ColorDrawable(bgColor))
    }

    fun withBackgroundRes(@DrawableRes bgDrawableResId: Int): I {
        return withBackground(ContextCompat.getDrawable(mContext, bgDrawableResId))
    }

    fun withContentViewLayoutRes(@LayoutRes contentViewLayoutRes: Int?):I {
        this.mContentViewResId = contentViewLayoutRes
        return self()
    }

    fun withFocusable(focusable: Boolean):I {
        isFocusable = focusable
        return self()
    }

    /**
     * 外部如果仍然调用该方法提供 [contentView]的优先级最高
     */
    override fun setContentView(contentView: View?) {
        var finalContentView: View? = contentView
        if (finalContentView == null) {
            finalContentView = providerContentView()
        }
        if (finalContentView == null) {
            val contentViewResId = mContentViewResId ?: providerContentLayoutRes()
            if (0!= contentViewResId) {
                finalContentView = LayoutInflater.from(mContext).inflate(contentViewResId!!, null)
            }
        }
        super.setContentView(finalContentView)
    }

    /**
     * 需要调用一下这个方法
     * 如果外部不调用 [setContentView]的情况下
     */
    fun build(): I {
        if (contentView == null) {//先获取当前 PopupWindow的 contentView，如果为null，则表示还没有配置 [contentView]
            contentView = null//则主动调用一次 [setContentView()]
        }
        return self()
    }

    final override fun getContentView(): View? {
        return super.getContentView()
    }

    protected fun self(): I = this as I
}