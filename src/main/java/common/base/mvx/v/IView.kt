package common.base.mvx.v

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import common.base.annotations.InvokeStep
import common.base.mvx.interfaces.IHostActions

/**
 * @author fee
 * DESC:<P>
 *  不管是 MVP 框架还是 MVVM框架，
 *  V层尽量需要写成 可复用V层：1、方便独立维护不与Activity、Fragment强偶合; 2、方便通用：比如如果项目中由Activity 变更成 Fragment方案，则直接V层的
 *  挪用过来就行；
 * </P>
 */
interface IView : IHostActions {

    /**
     * @param container 将装载 本 视图层的容器 View; eg.: 当本视图层是提供给 [Fragment]时
     * @param savedInstanceState [Activity] 重绘时临时存储了状态数据的 对象
     * @return 本视图层最终创建的 视图 View
     */
    @InvokeStep(1, desc = "init")
    fun onCreateView(container: ViewGroup?, savedInstanceState: Bundle?): View?

    /**
     * 提供 的视图/布局资源 ID
     */
    @LayoutRes
    fun provideVLayoutRes(): Int

    /**
     * 在该方法回调里初始化 Views
     * @param isInitState 是否为初始化状态,eg.: [Activity]的[onCreate]生命周期方法回调时；
     * @param dataIntent 从其他地方 跳转/路由 过来时所携带的 [Intent]
     * @param extraData 从其他地方 跳转/路由 过来时所携带的 [Bundle]数据； eg.: [Fragment]的初始化
     */
    @InvokeStep(2,desc = "init")
    fun initViews(isInitState: Boolean, dataIntent: Intent?, extraData: Bundle?)

    /**
     * 本 接口View 的根View，可以提供给 [Activity] 或者 [Fragment]
     */
    fun peekRootView(): View?




    /**
     * 根据 指定的 View的 ID来查找对应的View
     * @param targetViewId View所声明的ID
     * @return null or View
     */
    fun <V : View> findView(@IdRes targetViewId: Int): V? =
        peekRootView()?.findViewById(targetViewId)

    /**
     * 本 View 层的 调试信息
     * @return def = ""
     */
    fun theDebugInfo(): String = ""

}