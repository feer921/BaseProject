package common.base.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import common.base.WeakHandler;
import common.base.utils.CommonLog;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-07-04
 * Time: 20:03
 * DESC:
 */
public class BaseService extends Service implements WeakHandler.Handleable{
    protected final String TAG = getClass().getSimpleName();
    protected boolean LIFE_CIRCLE_DEBUG = false;
    protected Context appContext;
    protected Context mContext;
    @Override
    public void onCreate() {
        mContext = this;
        appContext = getApplicationContext();
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, " ---> onCreate()");
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, " ---> onStart() intent = " + intent + " startId = " + startId);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG,"--> onStartCommand() intent = " + intent + " flags = " + flags + " startID = " + startId);
        }
        return START_NOT_STICKY;//?
    }
    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "--> onBind() intent = " + intent);
        }
        return null;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onUnbind() intent = " + intent);
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG,"-->onConfigurationChanged() newConfig = " + newConfig);
        }
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "--> onRebind() intent = " + intent);
        }
    }

    @Override
    public void onDestroy() {
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "--> onDestroy() ");
        }
        if (weakHandler != null) {
            weakHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    protected WeakHandler weakHandler ;
    protected void initHandler() {
        if (weakHandler == null) {
//            weakHandler = new WeakHandler<>(this){
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    if (getOwner() != null) {
//                        handlerMsg(msg);
//                    }
//                }
//            };
            weakHandler = new WeakHandler<>(this);
        }
    }
//    protected void handlerMsg(Message msg) {
//
//    }

    protected void e(Object... logBody) {
        CommonLog.e(TAG,logBody);
    }

    protected void i(Object... logBody) {
        CommonLog.i(TAG,logBody);
    }

    /**
     * 子类可以重写该方法来处理消息
     * @param msg 要处理的消息
     */
    @Override
    public void handleMessage(Message msg) {

    }
}
