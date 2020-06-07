package common.base.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.util.concurrent.ExecutorService;

import common.base.WeakHandler;
import common.base.interfaces.IDoTaskCallback;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/11/30<br>
 * Time: 19:38<br>
 * <P>DESC:
 * 抽象的可持续执行任务(工作线程中执行)的任务Handler
 * 注：适合使用线程池来运行此类
 * </p>
 * ******************(^_^)***********************
 */
public class AbsTaskHandlerRunner<I extends AbsTaskHandlerRunner> implements WeakHandler.Handleable,Runnable{
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

    protected Looper mLooper;
    private volatile int mTid = -1;
    protected WeakHandler taskDispatchHandler;

    protected WeakHandler mHandler4MainThread;
    protected String theTaskName = "";

    private volatile boolean isQuit;

    protected IDoTaskCallback doTaskCallback;

    public I withTaskCallback(IDoTaskCallback taskCallback) {
        this.doTaskCallback = taskCallback;
        return self();
    }

    public I withTaskPriority(int taskPriority) {
        this.taskPriority = taskPriority;
        return self();
    }
    public I withNeedMainThreadHandler(boolean need) {
        this.needMainThreadHandler = need;
        return self();
    }
    public I prepare(@NonNull String taskName) {
        if (null == taskName || "".equals(taskName.trim())) {
            taskName = TAG;
        }
        theTaskName = taskName;
        if (needMainThreadHandler) {
            mHandler4MainThread = new WeakHandler<>(this, Looper.getMainLooper());
        }
        return self();
    }

    public I prepare() {
        return prepare(TAG);
    }

    public void start(ExecutorService executorService) {
        if (mLooper != null) {
            return;
        }
        if (executorService != null) {
            executorService.execute(this);
        }
    }
    /**
     * Call back method that can be explicitly overridden if needed to execute some
     * setup before Looper loops.
     */
    @WorkerThread
    protected void onLooperPrepared() {
    }

    @Override
    public final void run() {
        if (isQuit) {
            return;
        }
        Thread.currentThread().setName(theTaskName);
        mTid = Process.myTid();
        Looper.prepare();
        if (isQuit) {
            return;
        }
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(taskPriority);
        taskDispatchHandler = new WeakHandler<>(this, mLooper);
        onLooperPrepared();
        Looper.loop();
        mTid = -1;
    }
//    /**
//     * This method returns the Looper associated with this thread. If this thread not been started
//     * or for any reason isAlive() returns false, this method will return null. If this thread
//     * has been started, this method will block until the looper has been initialized.
//     * @return The looper.
//     */
//    public Looper getLooper() {
//        if (!isAlive()) {
//            return null;
//        }
//
//        // If the thread has been started, wait until the looper has been created.
//        synchronized (this) {
//            while (isAlive() && mLooper == null) {
//                try {
//                    wait();
//                } catch (InterruptedException e) {
//                }
//            }
//        }
//        return mLooper;
//    }
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
            if (handlerInMsg== taskDispatchHandler) {
                handleTask(msg);
                if (isQuit) {
                    if (mLooper != null) {
                        mLooper.quit();
                    }
                }
            }
            else if (handlerInMsg == mHandler4MainThread) {
                handleMsgOnMainThread(msg);
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
    protected boolean handleTask(Message msg){
        if (msg != null) {
            onDoTask(msg.what, msg.obj);
        }
        return false;
    }

    /**
     * 主线程处理消息
     * @param msg 消息(可能为在工作任务中执行结束的任务结果)
     * @return true:处理了
     */
    @MainThread
    protected boolean handleMsgOnMainThread(Message msg){
        if (msg != null) {
            onTaskResult(msg.what,msg.obj);
        }
        return false;
    }

    public boolean doTask(int whatTask, Object taskObj) {
        return doTask(whatTask, taskObj, false);
    }

    public boolean doTask(int whatTask, Object taskObj, boolean needRemoveLast) {
        boolean sendSuc = false;
        WeakHandler theHandler = taskDispatchHandler;
        if (theHandler != null) {
            if (needRemoveLast) {
                theHandler.removeMessages(whatTask);
            }
            boolean canSend = mTid != -1;//防止线程已经退出了，报sending message to a Handler on a dead thread
            if (canSend) {
                Message taskMessage = Message.obtain(theHandler, whatTask);
                taskMessage.obj = taskObj;
                sendSuc = theHandler.sendMessage(taskMessage);
            }
        }
        else{//task Handler还没初始化好
            sendSuc = doTaskUseOtherHandler(whatTask, taskObj, false, 2000);
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
        isQuit = true;
        doTaskCallback = null;
        if (taskDispatchHandler != null) {
            taskDispatchHandler.release();
        }
        taskDispatchHandler = null;
        if (mLooper != null) {
            mLooper.quit();//则线程在run()方法中会执行完成
        }
        mLooper = null;
        if (mHandler4MainThread != null) {
            mHandler4MainThread.release();
        }
        mHandler4MainThread = null;
    }

    protected boolean onDoTask(int whatTask, Object taskObj) {
        if (doTaskCallback != null) {
            return doTaskCallback.onDoTask(whatTask, taskObj);
        }
        return false;
    }

    protected void onTaskResult(int whatTask, Object taskResult) {
        if (doTaskCallback != null) {
            doTaskCallback.onDoTaskResult(whatTask, taskResult);
        }
    }
    /**
     * Returns the identifier of this thread. See Process.myTid().
     */
    public int getThreadId() {
        return mTid;
    }
}
