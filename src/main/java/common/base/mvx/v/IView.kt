package common.base.mvx.v

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

/**
 * @author fee
 * DESC:<P>
 *  不管是 MVP 框架还是 MVVM框架，
 *  V层尽量需要写成 可复用V层：1、方便独立维护不与Activity、Fragment偶合; 2、方便通用：比如如果项目中由Activity 变更成 Fragment方案，则直接V层的
 *  挪用过来就行；
 * </P>
 */
interface IView {

    /**
     * 本 接口View 的根View，可以提供给 [Activity] 或者 [Fragment]
     */
    fun rootView(): View?

    /**
     * 提供 的视图/布局资源 ID
     */
    @LayoutRes
    fun provideVLayoutRes(): Int

    /**
     * 根据 指定的 View的 ID来查找对应的View
     * @param targetViewId View所声明的ID
     * @return null or View
     */
    fun <V : View> findView(@IdRes targetViewId: Int): V? = rootView()?.findViewById(targetViewId)

    /**
     * 在该方法回调里初始化 Views
     */
    fun initViews()

}