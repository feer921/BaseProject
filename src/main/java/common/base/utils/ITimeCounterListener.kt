package common.base.utils

import androidx.annotation.WorkerThread


interface ITimeCounterListener {
    /**
     * 计时回调
     * @param theCounter 当前的计时者
     * @param curTime 计时后的当前时间
     * @param isComplete true:完成（一般是倒计时场景）;
     */
    @WorkerThread
    fun onTimeCount(theCounter: TimeCounter, curTime: Long, isComplete: Boolean)
}