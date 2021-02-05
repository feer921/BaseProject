package common.base

import android.os.Looper
import kotlinx.coroutines.*

/**
 * @author fee
 * <P>DESC:
 * 针对Kotlin的扩展函数
 * </p>
 */


/**
 * 执行代码块，可选为在工作线程中
 * @param isExecuteOnWorkThread true: 在工作线程中执行；
 * @param block 要执行的代码块
 * @return Deferred 当且仅当在 工作线程中执行时还有该返回值，调用处可以再调用[Deferred#await()] 得到 [block]中的返回值
 */
fun <R> executeBlock(isExecuteOnWorkThread: Boolean, block: () -> R): Deferred<R>? {
    return if (isExecuteOnWorkThread) {
        CoroutineScope(Dispatchers.Default).async {
            block()
        }
    } else{
        val s =  block()
        null
    }
}

/**
 * 将在UI线程中执行代码块
 * @param block 要执行的代码块
 */
fun <R> executeBlockOnUiThread(block: () -> R){
    val isAlreadyOnMainThread = Looper.getMainLooper() == Looper.myLooper()
    if (isAlreadyOnMainThread) {
        block()
        return
    }
    MainScope().launch {
        block()
    }
}

/**
 *
 */
fun <R> executeBlockOnUiThreadAlways(block: () -> R) {
    MainScope().launch {
        block()
    }
}

/**
 * @param isExecuteOnThreadOrUiThread true:将运行在工作线程；false:将运行的UI线程
 * @param block 将运行的代码块
 * @return [R] 所运行的代码块将返回的结果类型
 */
fun <R> executeBlockWith(isExecuteOnThreadOrUiThread: Boolean,block: () -> R){
    val isAlreadyOnMainThread = Looper.getMainLooper() == Looper.myLooper()
    if (isExecuteOnThreadOrUiThread) {//需要在工作线程中执行
        if (isAlreadyOnMainThread) {
            CoroutineScope(Dispatchers.Default).launch {
                block()
            }
        }
        else{
            block()
        }
    }
    else{//需要在UI线程中执行
        if (isAlreadyOnMainThread) {
            block()
        }
        else{
            MainScope().launch {
                block()
            }
        }
    }
}

