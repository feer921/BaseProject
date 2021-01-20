package common.base.netAbout

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * 相关数据结果 对象的包裹类
 * </p>
 * ******************(^_^)***********************
 */
open class WrapperRespData<out D>(val data: D?, val dataType: Int = 0, val errorInfos: String? = null) {

//    val dataType: Int = 0
//    val errorInfos: String? = null
}