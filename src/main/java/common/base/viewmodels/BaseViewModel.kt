package common.base.viewmodels

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.OkgoNetCallback
import common.base.netAbout.INetEvent
import common.base.netAbout.NetRequestLifeMarker
import common.base.netAbout.WrapperRespData
import common.base.utils.CommonLog
import common.base.utils.GenericsParamUtil

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * [ViewModel] 的基础封装，
 * 可进行 基于[OkGo]的网络数据请求
 * </p>
 * ******************(^_^)***********************
 */
open class BaseViewModel<R> : ViewModel(), INetEvent<R> {
    protected val TAG = javaClass.simpleName

    protected var mContext: Context? = null

    protected var mAppContext: Context? = null

    /**
     * Log 输出时的 额外信息
     */
    var extraInfosInDebug: String? = ""

    /**
     * 是否启用 网络请求响应回调时的 Log输出
     * def: false
     */
    var enableDebug: Boolean = false

    /**
     * 是否启用 网络请求错误回调时的 Log 输出
     * def: true
     */
    protected var enableDebugOnErrorResp = true

    /**
     * 是否启用 网络请求 正常回调里的 Log 输出
     */
    protected var enableDebugOnNormalResp = true

    protected var aOkGoCallback: OkgoNetCallback<R>? = null

    /**
     * 通用的 网络请求 响应 的[LiveData],子类可以不用
     */
    private var commonNetRespLiveData: MutableLiveData<WrapperRespData<R?>> ? = null

    /**
     * 如果该方法调用了，即表明 某处是希望来 观察 该 LiveData 的
     */
    fun getCommonNetRespLiveData(): MutableLiveData<WrapperRespData<R?>>? {
        if (commonNetRespLiveData == null) {
            commonNetRespLiveData = MutableLiveData()
        }
        return commonNetRespLiveData
    }
    /**
     * 是否启用 本基类 通用的网络请求 结果数据的 LiveData
     * def: true
     */
    protected var enableCommonNetRespLivedata = true

    /**
     * 当本[ViewModel] 销毁时，是否自动标记当前进行的网络请求(如果有的话) 为取消状态
     * def: true
     */
    protected var enableAutoCancelNetReq = true


    /**
     * 网络请求 状态 标记者
     * 用于标记一个网络请求的：开始、结束
     */
    protected val netRequestStateMarker = NetRequestLifeMarker()
    protected fun initOkgoNetDataListener() {
        if (aOkGoCallback == null) {
            aOkGoCallback = createOkgoNetListener()
        }
    }

    protected fun createOkgoNetListener(): OkgoNetCallback<R> {
        return OkgoNetCallback(getSubClasGenericsParamClass(), this)
    }

    /**
     * 获取本基类的子类所指定的泛型T的具体的对象类型的Class对象
     * 即：最多有效到第一个非泛型参数的本基类的子类
     * changed by fee 2017-04-29:如果本基类的子类已经是一个实体类[非泛型参数类]的子类时，需要自己指定泛型的class类型了
     * eg.: FirstSubClasss extends BaseNetCallFragment<ServerResut>{} 在FirstSubClass是可以自动获取到泛型参数为ServerResult的
     * 但是如果 FirstSubClass 再有子类，则自动获取不到了，需要重写本方法，直接指定ServerResut.class
     * @return Class<T> T指代本类所声明的泛型T eg.: xxSubClass extends BaseNetCallFragment<String>,
    </String></T></ServerResut> */
    protected open fun getSubClasGenericsParamClass(): Class<R> {
        return GenericsParamUtil.getGenericsParamCalss(javaClass.genericSuperclass, 0)
    }

    /**
     * 按tag 取消 Okgo 的网络请求
     * @param toCancelTag
     */
    protected fun cancelOkGoNetWork(toCancelTag: Any) {
        OkGo.getInstance().cancelTag(toCancelTag)
    }
    /**
     * 网络请求失败
     *
     * @param requestDataType 当前请求类型
     * @param errorInfo       错误信息
     */
    final override fun onErrorResponse(requestDataType: Int, errorInfo: String?) {
        if (enableDebug && enableDebugOnErrorResp) {
            e(null, " --> onErrorResponse() requestDataType = $requestDataType, errorInfo = $errorInfo")
        }
        //如果用户主动取消了当前网络请求如【Loading dialog】被取消了(实际上该请求已到达服务端,因而会响应回调)
        //则不让各子类处理已被用户取消了的请求
        if (curRequestCanceled(requestDataType)) {
            return
        }
        addRequestStateMark(requestDataType, NetRequestLifeMarker.REQUEST_STATE_FINISHED)
        dealWithErrorResponse(requestDataType, errorInfo)
    }

    /**
     * 网络请求的【错误/异常】 响应回调
     * @param requestDataType 当前网络请求类型
     * @param errorInfo 错误/异常 信息
     */
    protected open fun dealWithErrorResponse(requestDataType: Int, errorInfo: String?){
        //如果子类重写，并不调用 supper本方法，则[netRespLiveData] 无效
        if (enableCommonNetRespLivedata) {
//            if (commonNetRespLiveData == null) {
//                commonNetRespLiveData = MutableLiveData()
//            }
            commonNetRespLiveData?.value = WrapperRespData(null, requestDataType, errorInfo)
        }
    }
    /**
     * 网络请求的响应
     *
     * @param requestDataType 当前网络请求数据类型
     * @param result          响应实体:即表示将网络响应的结果转化成指定的数据实体bean
     */
    final override fun onResponse(requestDataType: Int, result: R?) {
        if (enableDebug && enableDebugOnNormalResp) {
            v(null, "--> onResponse() requestDataType = $requestDataType, result = $result")
        }
        if (curRequestCanceled(requestDataType)) {
            return
        }
        addRequestStateMark(requestDataType, NetRequestLifeMarker.REQUEST_STATE_FINISHED)
        dealWithResponse(requestDataType, result)

    }

    /**
     * 网络请求的 正常响应回调
     * @param requestDataType 当前网络请求类型
     * @param result 序列化后[如JSON序列化后] 的结果对象
     */
    protected open fun dealWithResponse(requestDataType: Int, result: R?){
        //如果子类重写，并不调用 supper本方法，则[netRespLiveData] 无效
        if (enableCommonNetRespLivedata) {
//            if (commonNetRespLiveData == null) {
//                commonNetRespLiveData = MutableLiveData()
//            }
            commonNetRespLiveData?.value = WrapperRespData(result, requestDataType, null)
        }
    }
    /**
     * 错误回调，在还没有开始请求之前，比如：一些参数错误
     *
     * @param curRequestDataType 当前网络请求类型
     * @param errorType          错误类型
     */
    override fun onErrorBeforeRequest(curRequestDataType: Int, errorType: Int) {
    }


    /**
     * 当前的请求是否取消了
     * @param dataType
     * @return
     */
    protected fun curRequestCanceled(dataType: Int): Boolean {
        return netRequestStateMarker.curRequestCanceled(dataType)
    }

    /**
     * 查询当前网络请求的状态
     * @param curRequestType
     * @return
     */
    protected fun curNetRequestState(curRequestType: Int): Byte {
        return netRequestStateMarker.curRequestLifeState(curRequestType)
    }

    /**
     * 标记当前网络请求的状态 : 正在请求、已完成、已取消等
     * @see {@link NetRequestLifeMarker.REQUEST_STATE_ING}
     *
     * @param requestDataType
     * @param targetState
     */
    protected fun addRequestStateMark(requestDataType: Int, targetState: Byte) {
        netRequestStateMarker.addRequestToMark(requestDataType, targetState)
    }

    /**
     * 开始追踪、标记一个对应的网络请求类型的请求状态
     * @param curRequestDataType
     */
    protected fun trackARequestState(curRequestDataType: Int) {
        netRequestStateMarker.addRequestToMark(curRequestDataType, NetRequestLifeMarker.REQUEST_STATE_ING)
    }

    protected fun isCurRequestWorking(reqDataType: Int): Boolean {
        return curNetRequestState(reqDataType) == NetRequestLifeMarker.REQUEST_STATE_ING
    }

    /**
     * 某个请求类型的网络请求是否已经完成
     * @param requestDataType
     * @return
     */
    protected fun curRequestFinished(requestDataType: Int): Boolean {
        return NetRequestLifeMarker.REQUEST_STATE_FINISHED == curNetRequestState(requestDataType)
    }

    /***
     * 取消网络请求（注：该方法只适合本框架的Retrofit请求模块才会真正取消网络请求，
     * 如果使用的是本框架的OkGo请求模块，还请各APP的基类再进行处理一下,或本框架再优化来统一处理）
     * @param curRequestType 要取消的网络请求类型
     */
    protected fun cancelNetRequest(curRequestType: Int) {
        netRequestStateMarker.cancelCallRequest(curRequestType)
    }

    /**
     * 判断是否某些网络请求全部完成了
     * @param theRequestTypes 对应要加入判断的网络请求类型
     * @return true:所有参与判断的网络请求都完成了；false:只要有任何一个请求未完成即未完成。
     */
    protected fun isAllRequestFinished(vararg theRequestTypes: Int): Boolean {
        if (theRequestTypes.isEmpty()) {
            return false
        }
        for (oneRequestType in theRequestTypes) {
            if (!curRequestFinished(oneRequestType)) {
                return false //只要有一个请求没有完成，就是未全部完成
            }
        }
        return true
    }


    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     * <p>
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    @CallSuper
    override fun onCleared() {
        super.onCleared()
        if (enableAutoCancelNetReq) {
            aOkGoCallback?.canceled = true
        }
        e(null, " --> onCleared()")
    }





    open fun e(tag: String?, vararg logBodys: Any?) {
        CommonLog.e(tag ?: "$TAG[$extraInfosInDebug]", *logBodys)
    }

    open fun v(tag: String?, vararg logBodys: Any?) {
        CommonLog.fullLog('v', tag ?: "$TAG[$extraInfosInDebug]", CommonLog.getInfo(*logBodys))
    }

    open fun i(tag: String?, vararg logBodys: Any?) {
        CommonLog.iFullLog(tag ?: "$TAG[$extraInfosInDebug]", *logBodys)
    }

    open fun d(logTag: String?, vararg logBodys: Any?) {
        CommonLog.d(logTag ?: "$TAG[$extraInfosInDebug]", *logBodys)
    }

    fun withContext(context: Context?) {
        this.mContext = context
        this.mAppContext = context?.applicationContext
    }
}