package common.base.popupwindow

import android.content.Context

/**
 * <P> DESC:
 * 目的是为了让该类的使用 和原生[PopupWindow]类似，消除 BasePopupWindow 的泛型参数
 * </P>
 */
class DefPopupWindow(context: Context) : BasePopupWindow<DefPopupWindow>(context) {
    override fun providerContentLayoutRes(): Int? {
        return null
    }

}