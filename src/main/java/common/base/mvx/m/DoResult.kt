package common.base.mvx.m

import java.lang.Exception

/**
 * @author fee
 * <P> DESC:
 * 一次业务执行的结果对象
 * </P>
 */
sealed class DoResult<out R>{

    data class Success<out R>(val resultData: R) : DoResult<R>()

    data class Error(val exception: Exception) : DoResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data = $resultData]"
            is Error -> "Error[exception = $exception]"
        }
    }
}
