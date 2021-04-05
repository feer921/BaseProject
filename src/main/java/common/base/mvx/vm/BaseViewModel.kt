package common.base.mvx.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.base.mvx.m.DoResult
import common.base.utils.AThreadPoolHolder
import common.base.utils.CommonLog
import common.base.utils.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author fee
 * <P> DESC:
 * ViewModel的基类
 * 在[MVC]框架下，可以作为[M]
 * 在[MVP]框架下，可以作为[M]
 * 在[MVVM]框架下，作为[VM]
 * </P>
 */
open class BaseViewModel : ViewModel() {
    protected val TAG = javaClass.simpleName

    /**
     * Log 输出时的 额外信息
     */
    var extraInfosInDebug: String? = ""

    /**
     * 是否使能Log输出
     * def: false
     */
    var isEnableLogDebug: Boolean = false

    open fun e(tag: String? = "$TAG[$extraInfosInDebug]", vararg logBodys: Any?) {
        CommonLog.e(tag ?: "$TAG[$extraInfosInDebug]", *logBodys)
    }

    open fun v(tag: String? = "$TAG[$extraInfosInDebug]", vararg logBodys: Any?) {
        CommonLog.fullLog('v', tag ?: "$TAG[$extraInfosInDebug]", CommonLog.getInfo(*logBodys))
    }

    open fun i(tag: String? = "$TAG[$extraInfosInDebug]", vararg logBodys: Any?) {
        CommonLog.iFullLog(tag ?: "$TAG[$extraInfosInDebug]", *logBodys)
    }

    open fun d(logTag: String? = "$TAG[$extraInfosInDebug]", vararg logBodys: Any?) {
        CommonLog.d(logTag ?: "$TAG[$extraInfosInDebug]", *logBodys)
    }


    fun lanchOnUi(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    /**
     * 基于 框架 为 [ViewModel] 给 视图层 提供可观测 [Model]数据
     * 本方法 提供 基础的通用数据 [DoResult] 数据
     */
    protected fun <R> getADoResultLiveData(): MutableLiveData<DoResult<R>> {
        return MutableLiveData<DoResult<R>>()
    }

    protected fun getADoResultLoading(isNeedShowLoading: Boolean = true) = DoResult.Loading(isNeedShowLoading)

    protected fun getADoResultError(exceptionMsg: String? = null, exception: Exception? = null) =
        DoResult.Error(exceptionMsg, exception)


    protected fun <R> assignLoadingToDoResultLiveData(
        theLiveData: MutableLiveData<DoResult<R>>?,
        isNeedLoading: Boolean = true
    ) {
        theLiveData?.let {
            if (AThreadPoolHolder.isOnMainThread()) {
                it.value = getADoResultLoading(isNeedLoading)
            }
            else{
                it.postValue(getADoResultLoading(isNeedLoading))
            }
        }
    }

    protected fun <R> assignErrorToDoResultLiveData(
        theLiveData: MutableLiveData<DoResult<R>>?,
        theExceptionMsg: String? = "", exception: Exception? = null
    ) {
        theLiveData?.let {
            it.value = getADoResultError(theExceptionMsg,exception)
        }
    }

    protected fun <R> assignResultToDoResultLiveData(theLiveData: MutableLiveData<DoResult<R>>?,theResult:R?,
                                           exceptionMsg:String? = null) {
        theLiveData?.let {
            val doResult: DoResult<R> = if (!exceptionMsg.isNullOrBlank()) {
                DoResult.Error(exceptionMsg,null)
            }
            else{
                DoResult.Success(theResult)
            }
            if (AThreadPoolHolder.isOnMainThread()) {
                it.value = doResult
            }
            else{
                it.postValue(doResult)
            }
        }
    }
}