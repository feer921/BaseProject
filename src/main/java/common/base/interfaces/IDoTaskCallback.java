package common.base.interfaces;

import android.support.annotation.MainThread;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/12/6<br>
 * Time: 17:18<br>
 * <P>DESC:
 * 关于任务执行的回调接口
 * </p>
 * ******************(^_^)***********************
 */
public interface IDoTaskCallback {

    /**
     * 正在执行任务
     * @param whatTask 要执行的什么类型的任务
     * @param taskObj 要执行的任务体
     */
    boolean onDoTask(int whatTask, Object taskObj);
    /**
     * 任务执行结果
     * 注：回调在主线程
     * @param whatTask 什么任务类型
     * @param taskResult 任务结果
     */
    @MainThread
    void onDoTaskResult(int whatTask, Object taskResult);
}
