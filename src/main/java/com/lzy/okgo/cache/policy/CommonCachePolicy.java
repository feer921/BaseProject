package com.lzy.okgo.cache.policy;

import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2020/4/7<br>
 * Time: 20:00<br>
 * <P>DESC:
 * 通用的缓存策略
 * </p>
 * ******************(^_^)***********************
 */
public abstract class CommonCachePolicy<T> extends BaseCachePolicy<T> {

    public CommonCachePolicy(Request<T, ? extends Request> request) {
        super(request);
    }

    /**
     * 获取数据成功的回调
     *
     * @param success 获取的数据，可是是缓存或者网络
     */
    @Override
    public void onSuccess(final Response<T> success) {
        if (mCallback != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   callbackOnSuccess(success);
                   callbackOnFinish();
                }
            });
        }
    }

    /**
     * 获取数据失败的回调
     *
     * @param error 失败的信息，可是是缓存或者网络
     */
    @Override
    public void onError(final Response<T> error) {
        if (mCallback != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callbackOnError(error);
                    callbackOnFinish();
                }
            });
        }
    }


}
