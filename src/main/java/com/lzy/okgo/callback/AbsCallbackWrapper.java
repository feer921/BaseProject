package com.lzy.okgo.callback;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：所有回调的包装类,空实现
 * 修订历史：
 * ================================================
 */
public class AbsCallbackWrapper<T> extends AbsCallback<T> {
    /**
     * 拿到响应后，将数据转换成需要的格式，子线程中执行，可以是耗时操作
     *
     * @param response 需要转换的对象
     * @return 转换后的结果
     * @throws Exception 转换过程发生的异常
     */
    @Override
    public T convertResponse(Response response) throws Throwable {
        if (response != null) {
            response.close();
            return (T) response;
        }
        return null;
    }

    //deleted since OkGo ver 3.0
//    @Override
//    public T convertSuccess(Response value) throws Exception {
//        value.close();
//        return (T) value;
//    }

    //deleted since OkGo ver 3.0
//    @Override
//    public void onSuccess(T t, Call call, Response response) {
//    }

    /**
     * 对返回数据进行操作的回调， UI线程
     *
     * @param response
     */
    @Override
    public void onSuccess(com.lzy.okgo.model.Response<T> response) {

    }


}