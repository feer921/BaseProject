package common.base.mvx.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.base.utils.CommonLog
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

}