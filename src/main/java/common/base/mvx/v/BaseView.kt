package common.base.mvx.v

import android.content.Context
import android.util.SparseArray
import android.view.View
import androidx.annotation.IdRes

/**
 * <P> DESC:
 * </P>
 */
abstract class BaseView(protected val mContext: Context) : IView {

    protected var viewsCache: SparseArray<View>? = null

//    protected val appContext: Context = mContext.applicationContext

    /**
     * 根据 指定的 View的 ID来查找对应的View
     * @param targetViewId View所声明的ID
     * @return null or View
     */
    override fun <V : View> findView(@IdRes targetViewId: Int): V? {
        var findedView = viewsCache?.get(targetViewId)//首先在 缓存中找,目的是 减少 View 树的遍历
        if (findedView == null) {
            findedView = super.findView(targetViewId)
            if (findedView != null) {
                if (viewsCache == null) {
                    viewsCache = SparseArray()
                }
                viewsCache?.put(targetViewId, findedView)
            }
        }
        return findedView as V?
    }
}