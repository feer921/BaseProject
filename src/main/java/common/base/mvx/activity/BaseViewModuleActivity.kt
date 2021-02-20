package common.base.mvx.activity

import android.view.View
import common.base.mvx.v.IView

/**
 * <P> DESC:
 * </P>
 */
abstract class BaseViewModuleActivity<V : IView> : BaseActivity() {

    protected var mViewModule: V? = lazy { provideVModule() }.value

    /**
     * 获取当前Activity需要填充、展示的内容视图，如果各子类提供，则由基类来填充，如果不提供，各子类也可自行处理
     * @return 当前Activity需要展示的内容视图资源ID
     * @step 1
     */
    override fun getProvideContentViewResID(): Int {
        return mViewModule?.provideVLayoutRes() ?: 0
    }

    /**
     * @step 2
     */
    override fun providedContentView(): View? {
        return mViewModule?.rootView()
    }

    /**
     * 如果子类在getProvideContentViewResID()方法提供了视图资源，那么子类的初始化视图可在此方法中完成
     * @step 3
     */
    override fun initViews() {
        mViewModule?.initViews()
    }

    /**
     *
     */
    protected abstract fun provideVModule(): V?

}