package com.lzy.okgo.callback;

import java.net.UnknownHostException;

import common.base.netAbout.INetEvent;
import okhttp3.Call;
import okhttp3.Response;

/**
 * ================================================
 * User: 11776610771@qq.com
 * Date: 2017/4/1
 * Time: 15:24
 * DESC: 兼容Okgo的网络请求以及BaseProject中的网络请求框架
 * ================================================
 */

public class OkgoNetCallback<T> extends XtypeCallback<T>{
    public static <E> OkgoNetCallback<E> create(Class<E> genericsParamClass, INetEvent<E> netEvent) {
        return new OkgoNetCallback<>(genericsParamClass, netEvent);
    }
    public OkgoNetCallback(Class<T> tTypeClass,INetEvent<T> netEvent){
        genericsTypeClass = tTypeClass;
        this.netEvent = netEvent;
    }
    protected INetEvent<T> netEvent;

    public void onSuccess(T repData) {
        if (null != netEvent) {
            netEvent.onResponse(requestType, repData);
        }
    }


    @Override
    public void onError(Call call, Response response, Exception e) {
        String errorMsg = INetEvent.ERR_UNKNOW;
        if (null != e) {
            errorMsg = e.toString();
            if (e instanceof UnknownHostException) {
                errorMsg = INetEvent.ERR_UNKNOW_HOST;
            }
        }
        if (null != netEvent) {
            netEvent.onErrorResponse(requestType,errorMsg);
        }
    }
}
