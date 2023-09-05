package common.base.utils

import android.os.SystemClock
import androidx.annotation.WorkerThread
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TimeCounter {

    companion object Builder {
        fun aCounter() = TimeCounter()
    }
    @Volatile
    private var mStartTime: Long = 0

    /**
     * 步进间隔时间
     */
    private var mStepForwardTimeMills: Long = 1000

    /**
     * true: 倒计时;false：累加计时
     * def = true: 倒计时
     */
    private var mIsCountDownOrAddTime = true

    /**
     * 计时任务
     */
    @Volatile
    private var mJobOfTimer: Job? = null

    /**
     *　监听者
     */
    private var mCounterListener: ITimeCounterListener? = null

    private var mTag = ""

    var extraData: Any? = null

    /**
     * 是否需要　计算时间(加或减)
     * def = true
     */
    @Volatile
    var isNeedCalculateTime = true

    /**
     * 开始计时
     */
    fun start() {
        if (mJobOfTimer != null) {
            return
        }
        runTask()
    }

    private fun runTask() {
        mJobOfTimer = XCoroutineScope.ioCoroutineScope?.launch {
            val gapTime = mStepForwardTimeMills
            delay(gapTime)
            if (mIsCountDownOrAddTime){
                mStartTime -= gapTime
                if (mStartTime <= 0){
                    mJobOfTimer = null
                    onComplete()
                    return@launch
                }
            } else {
                if (isNeedCalculateTime){
                    mStartTime += gapTime
                }
            }
            onTimeUpdated(mStartTime)
            runTask()
        }
    }

    /**
     * 上次暂停时的　系统时间戳
     */
    private var mLastPausedTimeMills: Long = 0

    /**
     * 暂停计时
     */
    fun pauseCountTime() {
        mLastPausedTimeMills = SystemClock.elapsedRealtime()
        stop()
    }

    fun stop() {
        mJobOfTimer?.cancel()
        mJobOfTimer = null
        cancelDelayTask()
    }

    /**
     * @param startTime Long 开始的时间(戳),一般为毫秒
     */
    fun withStartTime(startTime: Long): TimeCounter{
        this.mStartTime = startTime
        return this
    }

    /**
     * 计时的　步进/间隔　时间（戳）
     * @param　stepForwardTime　一般为毫秒
     */
    fun withStepForwardTime(stepForwardTime: Long): TimeCounter {
        this.mStepForwardTimeMills = stepForwardTime
        return this
    }

    /**
     * @param isToCountdownOrToAdd true:去倒计时;　false: 去累加计时
     */
    fun withCountdownOrAdd(isToCountdownOrToAdd: Boolean):TimeCounter {
        this.mIsCountDownOrAddTime = isToCountdownOrToAdd
        return this
    }

    fun withListener(l: ITimeCounterListener?): TimeCounter{
        this.mCounterListener = l
        return this
    }

    fun withTag(tag: String):TimeCounter {
        this.mTag = tag
        return this
    }

    /**
     * 恢复计时
     */
    fun resumeCountTime(){
        if (mLastPausedTimeMills <= 0) {//没有暂停过
            return
        }
        val elapsedTimeMills = SystemClock.elapsedRealtime() - mLastPausedTimeMills
        mLastPausedTimeMills = 0
        if (mIsCountDownOrAddTime){
            mStartTime -= elapsedTimeMills
        } else{
            mStartTime += elapsedTimeMills
        }
        if (mIsCountDownOrAddTime){
            if (mStartTime <= 0){
                onComplete()
                return
            }
        }
        onTimeUpdated(mStartTime)
        start()
    }

    @WorkerThread
    open fun onTimeUpdated(theTime: Long){
        mCounterListener?.onTimeCount(this,theTime,false)
    }

    @WorkerThread
    open fun onComplete() {
        mCounterListener?.onTimeCount(this,0,true)
    }

    fun isRunnig() = mJobOfTimer != null


    private var mJobOfDelay: Job? = null

    fun delayTask(delayTs: Long) {
        mJobOfDelay = XCoroutineScope.ioCoroutineScope?.launch {
            delay(delayTs)
            onTimeUpdated(0)
        }
    }

    fun cancelDelayTask(){
        mJobOfDelay?.cancel()
    }

    fun getCurTime() = mStartTime
    override fun toString(): String {
        return "TimeCounter(mTag='$mTag')"
    }


}