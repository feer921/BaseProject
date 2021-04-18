package common.base.mvx.m


/**
 ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2021/4/4<br>
 * Time: 22:08<br>
 * <P>DESC:
 * 通用的一个响应 结果 实体对象
 * 注：可用于 [MutableLiveData]的 value
 * </p>
 * ******************(^_^)***********************
 */
open class ABizResult<DataType,R>(
    var dataType: DataType,
    var data: R?,
    var isNeedShowLoading: Boolean = false,
    var errorMsg: String? = null,
    var exception: Exception? = null,
    var extraInfo:Any? = null
)
