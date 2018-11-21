package common.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseIntArray;

import java.lang.ref.WeakReference;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/5/22<br>
 * Time: 14:40<br>
 * <P>DESC:
 * 弱引用外部需要Handler Message的对象，用于避免外部(Activity/Service)等直接引用Handler可能引起的内存泄漏或者Lint提示
 * 另增加了可把不想处理的消息类型添加进忽略数组
 * </p>
 * ******************(^_^)***********************
 */
public class WeakHandler<T extends WeakHandler.Handleable> extends Handler {
    private WeakReference<T> handleableObj;

    private SparseIntArray ignoreMsgWhats;
    public WeakHandler(T toHandleMsgObj) {
        handleableObj = new WeakReference<>(toHandleMsgObj);
    }

    /**
     * 增加可以指定关联到不同looper的Handler
     * @param toHandleMsgObj
     * @param looper
     */
    public WeakHandler(T toHandleMsgObj, Looper looper) {
        super(looper);
        handleableObj = new WeakReference<>(toHandleMsgObj);
    }
    /**
     * 添加需要忽略的消息类型
     * @param toIgnoreMsgWhat 需要忽略的消息类型，可多个
     * @return self
     */
    public WeakHandler addIgnoreMsgWhat(int... toIgnoreMsgWhat) {
        if (toIgnoreMsgWhat != null && toIgnoreMsgWhat.length > 0) {
            if (ignoreMsgWhats == null) {
                ignoreMsgWhats = new SparseIntArray();
            }
            for (int theMsgWhat : toIgnoreMsgWhat) {
                ignoreMsgWhats.put(theMsgWhat, 0);
            }
        }
        return this;
    }

    /**
     * 移除被添加到忽略数组的消息类型
     * @param toRemoveIgnoredMsgWhat 要移除的被忽略的消息类型，可以多个
     * @return self
     */
    public WeakHandler removeIgnoredMsgWhat(int... toRemoveIgnoredMsgWhat) {
        if (toRemoveIgnoredMsgWhat == null || toRemoveIgnoredMsgWhat.length < 1) {
            return this;
        }
        if (ignoreMsgWhats != null && ignoreMsgWhats.size() > 0) {
            for (int theMsgWhat : toRemoveIgnoredMsgWhat) {
                ignoreMsgWhats.delete(theMsgWhat);
            }
        }
        return this;
    }

    public void clearIgnoredMsgWhats() {
        if (ignoreMsgWhats != null) {
            ignoreMsgWhats.clear();
        }
    }

    public boolean isTheMsgWhatIgnored(int theMsgWhat) {
        if (ignoreMsgWhats != null) {
            return ignoreMsgWhats.indexOfKey(theMsgWhat) >= 0;
        }
        return false;
    }
    //changed by fee 2018-11-07:去除final修饰，让子类可以直接重写
    @Override
    public void handleMessage(Message msg) {
        int msgWhat = msg.what;
        //判断该Message 被忽略了
        boolean isIgnored = false;
        if (ignoreMsgWhats != null) {//？？？会存在同步问题吗？
            isIgnored = isTheMsgWhatIgnored(msgWhat);
        }
        if (isIgnored) {//该类型的消息被忽略了
            return;
        }
        if (handleableObj != null) {
            T theHandleableOne = handleableObj.get();
            if (theHandleableOne != null) {
                theHandleableOne.handleMessage(msg);
            }
        }
    }

    public T getReferenceOjb() {
        return handleableObj != null ? handleableObj.get() : null;
    }
    public void release() {
//        if (ignoreMsgWhats != null) {
//            ignoreMsgWhats.clear();
//        }
        ignoreMsgWhats = null;
        removeCallbacksAndMessages(null);
    }
   public interface Handleable{
       /**
        * 处理消息
        * @param msg
        * @return 被处理了的MessageId
        */
        int handleMessage(Message msg);
    }
}
