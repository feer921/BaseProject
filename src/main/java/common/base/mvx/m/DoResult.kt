package common.base.mvx.m

import java.lang.Exception
import javax.xml.transform.dom.DOMResult

/**
 * @author fee
 * <P> DESC:
 * 一次业务执行的结果对象
 * </P>
 */
sealed class DoResult<out R>{

    fun isError() = this is Error

    fun isSuccess() = this is Success

    fun isLoading() = this is Loading
    data class Success<out R>(val resultData: R?) : DoResult<R>()

    data class Error(val exceptionMsg: String?, val exception: Exception?) : DoResult<Nothing>()

    data class Loading(val isNeedShowLoading: Boolean = true) : DoResult<Nothing>() {

    }

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data = $resultData]"
            is Error -> "Error[errorMsg = $exceptionMsg,exception = $exception]"
            is Loading -> "Loading[isNeedShowLoading = $isNeedShowLoading]"
        }
    }
}
