package common.base.mvx.m

import common.base.utils.CommonLog

/**
 * @author fee
 * <P> DESC:
 * 业务[M]
 * 网络接口请求/数据库查询/IO流操作...
 * </P>
 */
abstract class BaseRepository : IRepository {
    protected val TAG = javaClass.simpleName

    /**
     * 是否使能Log输出
     * def: false
     */
    var isEnableLogDebug: Boolean = false
    var extraInfosInDebug: String = ""

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



}