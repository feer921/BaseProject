package common.base.activitys;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-17
 * Time: 17:51
 * DESC: 将宿主弱引用的Handler
 */
@Deprecated
public class WeakHandler<T> extends Handler{

    private WeakReference<T> mOwner;
    /**
     * 生成弱引用对象
     * @param owner 被 弱引用对象
     */
    public WeakHandler(T owner) {
        mOwner = new WeakReference<>(owner);
    }

    public T getOwner() {
        return mOwner.get();
    }
}
