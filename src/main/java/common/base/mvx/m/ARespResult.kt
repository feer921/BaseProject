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
open class ARespResult<DataType,R>(
    val dataType: DataType,
    val data: R?,
    val isNeedShowLoading: Boolean = false,
    val errorMsg: String? = "",
    val exception: Exception? = null
)
