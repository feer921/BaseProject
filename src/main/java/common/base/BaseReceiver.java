package common.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;

import java.util.HashSet;

/**
 * ******************(^_^)***********************<br>
 * User: 11776610771@qq.com<br>
 * Date: 2018/6/2<br>
 * Time: 12:40<br>
 * <P>DESC:
 * 广播接收者的通用类，可批量给该接收者添加要接收的对应的actions
 * </p>
 * ******************(^_^)***********************
 */
public abstract class BaseReceiver<I extends BaseReceiver<I>> extends BroadcastReceiver {
    protected final String TAG = getClass().getSimpleName();
    /**
     * 本广播接收者基类，只有一个IntentFilter，但广播接收者是可以有多个IntentFilter的
     */
    protected IntentFilter mIntentFilter;
    protected boolean asLocalReceiver;
    public I withAction(String action,String dataType) {
        if (action != null) {
            if (mIntentFilter == null) {
                mIntentFilter = new IntentFilter();
            }
            mIntentFilter.addAction(action);
            if (!TextUtils.isEmpty(dataType)) {
                try {
                    mIntentFilter.addDataType(dataType);
                } catch (IntentFilter.MalformedMimeTypeException e) {
                    e.printStackTrace();
                }
            }
        }
        return self();
    }

    public I withAction(String action) {
        return withAction(action, null);
    }
    public I withActions(String... actions) {
        if (actions != null && actions.length > 0) {
            for (String theAction : actions) {
                withAction(theAction, null);
            }
        }
        return self();
    }

    /**
     * 将本广播接收者注册
     * @param context Context
     * @param registerAsLocal 是否作为本地广播接收者注册.
     */
    public void register(Context context, boolean registerAsLocal) {
        if (registerAsLocal) {
            LocalBroadcastManager.getInstance(context).registerReceiver(this,mIntentFilter);
        }
        else{
            context.registerReceiver(this, mIntentFilter);
        }
    }
    public void register(Context context) {
        register(context,false);
    }

    public void unRegister(Context context) {
        context.unregisterReceiver(this);
    }

    /**
     * 解注册本广播接收者
     * @param context Context
     * @param unRegisterInLocal 是否解注册本广播为本地广播
     */
    public void unRegister(Context context, boolean unRegisterInLocal) {
        if (unRegisterInLocal) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
        }
        else{
            context.unregisterReceiver(this);
        }
    }
    public IntentFilter getInnerIntentFilter() {
        return mIntentFilter;
    }

    protected final I self() {
        return (I) this;
    }
    protected HashSet<String> ignoreActions;
    /**
     * 添加要忽略的Action
     * @param willIgnoreActions 将要忽略的广播Action
     */
    public void ignoreActions(String... willIgnoreActions) {
        if (willIgnoreActions != null && willIgnoreActions.length > 0) {
            if (ignoreActions == null) {
                ignoreActions = new HashSet<>();
            }
            for (String willIgnoreAction : willIgnoreActions) {
                if (ignoreActions.contains(willIgnoreAction)) {
                    continue;
                }
                ignoreActions.add(willIgnoreAction);
            }
        }
    }

    public boolean isActionIgnored(String theAction) {
        return ignoreActions != null && ignoreActions.contains(theAction);
    }

//    public static void main(String[] args) {
//        HashSet<String> a = new HashSet<>(3);
//        a.add("a");
//        a.add("a");
//        a.add("c");
//        for (String s : a) {
//            System.out.println(s);
//        }
//    }

}
