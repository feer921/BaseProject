package common.base.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import common.base.WeakHandler;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/11/30<br>
 * Time: 19:38<br>
 * <P>DESC:
 * 抽象的可持续执行任务(工作线程中执行)的任务Handler
 * 注：不适合大量使用，因为会构造HandlerThread 即创建一个Thread
 * </p>
 * ******************(^_^)***********************
 */
public abstract class AbsTaskHandler<I extends AbsTaskHandler> implements WeakHandler.Handleable{
    protected final String TAG = getClass().getSimpleName();

    protected static final int MSG_WHAT_DO_TASK = 0x11;
    /**
     * 任务的优先级
     */
    protected int taskPriority;
    /**
     * 是否需要初始化工作在主线程上的Handler
     */
    protected boolean needMainThreadHandler;

    protected HandlerThread taskThread;

    protected WeakHandler taskDispatchHandler;

    protected WeakHandler mHandler4MainThread;

    public I prepare(@NonNull String taskName) {
        if (null == taskName || "".equals(taskName.trim())) {
            taskName = TAG;
        }
        if (null == taskThread) {
            taskThread = new HandlerThread(taskName, taskPriority){
                @Override
                protected void onLooperPrepared() {
                    if (taskDispatchHandler == null) {
                        taskDispatchHandler = new WeakHandler<>(AbsTaskHandler.this, taskThread.getLooper());
                    }
                }
            };
        }
        taskThread.start();
        if (needMainThreadHandler) {
            mHandler4MainThread = new WeakHandler<>(this, Looper.getMainLooper());
        }
        return self();
    }

    public I prepare() {
        return prepare(TAG);
    }
    /**
     * 处理消息
     *
     * @param msg
     * @return 被处理了的MessageId
     */
    @Override
    public final int handleMessage(Message msg) {
        if (msg != null) {
            Handler handlerInMsg = msg.getTarget();
            if (handlerInMsg == taskDispatchHandler) {
                handleTask(msg);
            }
            else if (handlerInMsg == mHandler4MainThread) {
                int msgWhat = msg.what;
                if (MSG_WHAT_DO_TASK == msgWhat) {//这里表示之前发送的要执行的任务(因为taskDispatchHandler未初始化好)被延迟执行了
                    doTask(msg.obj);
                }
                else{
                    handleMsgOnMainThread(msg);
                }
            }
        }
        return 0;
    }

    /**
     * 在这里来处理任务
     * 注：在工作线程中
     * @param msg 任务消息
     * @return true:处理完了
     */
    @WorkerThread
    protected abstract boolean handleTask(Message msg);

    /**
     * 主线程处理消息
     * @param msg 消息(可能为在工作任务中执行结束的任务结果)
     * @return true:处理了
     */
    @MainThread
    protected abstract boolean handleMsgOnMainThread(Message msg);

    public boolean doTask(int whatTask, Object taskObj) {
        return doTask(whatTask, taskObj, false);
    }

    public boolean doTask(int whatTask, Object taskObj, boolean needRemoveLast) {
        boolean sendSuc = false;
        WeakHandler theHandler = taskDispatchHandler;
        if (theHandler != null) {
            Message taskMessage = null;
            if (needRemoveLast) {
                theHandler.removeMessages(whatTask);
                // TODO: 2018/12/4  需要重新new????
//                taskMessage = new Message();
            }
            else{

            }
            taskMessage = Message.obtain(theHandler, whatTask);

            taskMessage.obj = taskObj;
            sendSuc = theHandler.sendMessage(taskMessage);
        }
        else{
            sendSuc = doTaskUseOtherHandler(whatTask, taskObj, needRemoveLast, 2000);
        }
        return sendSuc;
    }

    public boolean doTask(Object taskOjb) {
        return doTask(MSG_WHAT_DO_TASK, taskOjb);
    }
    public boolean doTaskUseOtherHandler(int whatTask, Object taskObj, boolean needRemoveLastOne, long delaySendTimeMills) {
        WeakHandler theHandler = mHandler4MainThread;
        boolean sendSuc = false;
        if (theHandler != null) {
            if (needRemoveLastOne) {
                theHandler.removeMessages(whatTask);
            }
            Message msg = Message.obtain(theHandler, whatTask);
            msg.obj = taskObj;
            sendSuc = theHandler.sendMessageDelayed(msg, delaySendTimeMills);
        }
        return sendSuc;
    }

    protected I self() {
        return (I) this;
    }
    public void workOver() {
        if (taskDispatchHandler != null) {
            taskDispatchHandler.release();
        }
        taskDispatchHandler = null;
        if (taskThread != null) {
            taskThread.quit();
        }
        taskThread = null;
        if (mHandler4MainThread != null) {
            mHandler4MainThread.release();
        }
        mHandler4MainThread = null;
    }

}
