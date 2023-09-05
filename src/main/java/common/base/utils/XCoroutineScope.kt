package common.base.utils

import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService


object XCoroutineScope {

    /**
     * 主线程中的　CoroutineScope
     */
    val mainCoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main)
    }

    /**
     * 需要外部指定线程执行器
     */
    var mExecutor: ExecutorService? = null

    val ioCoroutineScope by lazy {
        val asCoroutineDispatcher = mExecutor?.asCoroutineDispatcher() ?: return@lazy null
        CoroutineScope(asCoroutineDispatcher)
    }

    fun excute(task: Runnable) {
        mExecutor?.execute(task)
    }

    fun excuteOnMain(task: Runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()){//已经在主线程上了
            task.run()
            return
        }
        mainCoroutineScope.launch {
            task.run()
        }
    }
}