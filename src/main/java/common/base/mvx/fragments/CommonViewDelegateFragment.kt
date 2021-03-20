package common.base.mvx.fragments

import android.content.Context
import android.os.Bundle
import common.base.mvx.v.BaseViewDelegate
import common.base.mvx.v.IView

/**
 ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2021/3/20<br>
 * Time: 12:31<br>
 * <P>DESC:
 * 一个通用的加载 视图层代理的 Fragment，
 * 从而可以不用写各个Fragment，而只写 相应的 ViewDelegate
 * </p>
 * ******************(^_^)***********************
 */
open class CommonViewDelegateFragment : BaseViewDelegateFragment<IView>() {

    companion object Builder{
        fun buildAFragment(
            theViewDelegateClass: Class<out BaseViewDelegate>,
            extraDataConfigs: (Bundle) -> Unit
        ): CommonViewDelegateFragment {
            val theFragment = CommonViewDelegateFragment()
            val dataBundle = Bundle()
            dataBundle.putSerializable("theViewDelegateClass", theViewDelegateClass)
            extraDataConfigs(dataBundle)
            theFragment.arguments = dataBundle
            return theFragment
        }
    }
    /**
     * 子类所提供的 视图[V]层的 对象
     */
    override fun provideVModule(): IView? {
        return arguments?.let {
            it.getSerializable("theViewDelegateClass")?.let {
                ser->
                try {
                    val viewDelegateClass: Class<out BaseViewDelegate> =
                        ser as Class<out BaseViewDelegate>
                    val declaredConstructor =
                        viewDelegateClass.getDeclaredConstructor(Context::class.java)
                    return declaredConstructor.newInstance(context)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            null
        }
    }

}