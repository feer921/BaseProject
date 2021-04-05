package common.base.mvx.vm

import common.base.mvx.m.IRepository

/**
 ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2021/4/4<br>
 * Time: 10:26<br>
 * <P>DESC:
 * 框架为 ViewModel has IRepository 的架构模式
 * </p>
 * ******************(^_^)***********************
 */
abstract class BaseViewModelWithRepository<REPO : IRepository> : BaseViewModel() {

    protected val mMainRepository: REPO? by lazy {
        provideMainRepository()
    }

    /**
     * 提供当前 [M]业务层 的主仓库，因为有可能 一个 [ViewModel] 会需要多个[M]层的业务仓库
     */
    protected abstract fun provideMainRepository(): REPO?
}