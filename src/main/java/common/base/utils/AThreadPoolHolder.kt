package common.base.utils

import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author fee
 * <P> DESC:
 * 单例、并且线程安全的 一个线程池持有对象；
 * 如果配置 的核心线程数为0，则可以不用关心线程池的释放问题
 * 注：建议一个项目中通用一个线程池
 * </P>
 */
class AThreadPoolHolder private constructor(coreThreadCount: Int = 0, maxThreadCount: Int = Int.MAX_VALUE,
                                            keepLiveSeconds: Long
){

    private val threadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(coreThreadCount, maxThreadCount,
            keepLiveSeconds, TimeUnit.SECONDS,
            SynchronousQueue()
    )

    companion object Builder{
        /**
         * 线程池中核心线程数量
         * def = 0: 当线程池中所有线程工作完后，自动退出线程池
         * 注：在调用[peekTheThreadPoolExecutor] 方法前配置有效
         */
        var coreThreadCount: Int = 0

        /**
         * 配置的最大 线程数量
         * def = [Int.MAX_VALUE]
         * 注：在调用[peekTheThreadPoolExecutor] 方法前配置有效
         */
        var maxThreadCount: Int = Int.MAX_VALUE

        /**
         * 线程池中空闲出来的线程在多长时间(单位：秒)后被释放/销毁
         * 注：在调用[peekTheThreadPoolExecutor] 方法前配置有效
         */
        var keepLiveSeconds: Long = 60

        /**
         * 线程安全并且 延迟初步化，并且单例
         */
        private val theThreadPoolHolder: AThreadPoolHolder by lazy {
            AThreadPoolHolder(coreThreadCount, maxThreadCount, keepLiveSeconds)
        }


        fun peekTheThreadPoolExecutor(): ThreadPoolExecutor = theThreadPoolHolder.threadPoolExecutor

        fun execute(theTask: Runnable) {
            peekTheThreadPoolExecutor().execute(theTask)
        }

        /**
         * 在工作线程中执行 [block] 代码块
         */
        fun execute(block: () -> Unit) {
            peekTheThreadPoolExecutor().execute {
                block()
            }
        }
    }
}