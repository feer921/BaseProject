package common.base.mvx.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import common.base.annotations.InvokeStep
import common.base.mvx.v.IView

/**
 * @author fee
 * <P> DESC:
 * 可为 [MVC]或者 [MVP]或者[MVVM] 框架下的 基类 [Fragment]
 * [MVC]框架场景时，[Fragment]作为为 [C]层,操作[M] 与[V]
 * [MVP]框架场景时 [Fragment]作为[P]层
 * [MVVM]框架场景时，结合[DataBinding]或者[ViewModel]来使用
 * </P>
 */
abstract class BaseViewDelegateFragment<V : IView>() : BaseFragment() {

    protected val mViewModule: V? by lazy(mode = LazyThreadSafetyMode.NONE) {
        provideVModule()?.apply {
        attachViewModelStoreOwner(this@BaseViewDelegateFragment)
        attachLifecycleOwner(this@BaseViewDelegateFragment)
    } }



//    init {
//        println("in init time : 0 in lazy ")
//    }

    //下面 这个 会在 对象初始化阶段 就初始化
//    protected var mViewModule: V? = lazy(mode = LazyThreadSafetyMode.NONE) {
//        println("00000000000000000000000000000000000000000000000 in lazy ")
//        provideVModule()?.apply {
//            attachViewModelStoreOwner(this@BaseViewDelegateFragment)
//            attachLifecycleOwner(this@BaseViewDelegateFragment)
//        } }.value

    /**
     * 提供当前碎片的内容视图布局的资源ID
     * @return
     */
    @InvokeStep(1,desc = "in onCreateView()")
    override fun providedFragmentViewResId(): Int {
        return 0
    }

    override fun providedFragmentView(container: ViewGroup?, savedInstanceState: Bundle?): View? {
       return mViewModule?.onCreateView(container,savedInstanceState)
    }

    /**
     * 对所加载的视图进行初始化工作
     * @param contentView
     */
    @InvokeStep(3, desc = "in onViewCreated()")
    override fun initViews(contentView: View?) {
        mViewModule?.initViews(true, null, arguments)
    }


    /**
     * 初始化数据
     */
    @InvokeStep(4, desc = "in onViewCreated()")
    override fun initData() {
    }

    /**
     * 子类所提供的 视图[V]层的 对象
     */
    protected abstract fun provideVModule(): V?

    @CallSuper
    override fun onResume() {
        super.onResume()
        mViewModule?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mViewModule?.onPause()
    }
    @CallSuper
    override fun onStop() {
        super.onStop()
        mViewModule?.onStop()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mViewModule?.onSaveInstanceState(outState)
    }

    @CallSuper
    override fun onDetach() {
        super.onDetach()
        mViewModule?.onDetach()
    }

    @CallSuper
    override fun onHandleBackPressed(): Boolean {
        return mViewModule?.onConsumeBackPressed() ?: false
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mViewModule?.onActivityResult(requestCode,resultCode,data)
    }

    @CallSuper
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mViewModule?.onConfigurationChanged(newConfig)
    }



}