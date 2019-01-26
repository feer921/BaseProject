package com.lzy.okgo.callback;

import java.lang.ref.WeakReference;
import java.net.UnknownHostException;

import common.base.netAbout.INetEvent;
import common.base.netAbout.INetEventWithResponse;
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
//        this.netEvent = netEvent;
        weakRefNetEvent = new WeakReference<>(netEvent);
        //added by fee 2019-01-26: 网络请求的取消标记默认设置为 netEent
        obj4CancelTag = weakRefNetEvent.get();
    }
    protected WeakReference<INetEvent<T>> weakRefNetEvent;
    protected INetEvent<T> netEvent;

    public void onSuccess(T repData) {
        if (canceled) {
            return;
        }
        if (null != netEvent) {
            netEvent.onResponse(requestType, repData);
        }
        INetEvent<T> netEvent = theNetEvent();
        if (netEvent != null) {
            netEvent.onResponse(requestType, repData);
        }
    }

    @Override
    public void onSuccess(T t, Call call, Response response) {
        if (response != null) {
            if (netEvent != null) {
                if (netEvent instanceof INetEventWithResponse) {
                    ((INetEventWithResponse) netEvent).onResponse(requestType, t, response);
                }
            }
            INetEvent<T> netEvent = theNetEvent();
            if (netEvent != null) {
                if (netEvent instanceof INetEventWithResponse) {
                    ((INetEventWithResponse) netEvent).onResponse(requestType, t, response);
                }
            }
        }
        onSuccess(t);
    }

    @Override
    public void onError(Call call, Response response, Exception e) {
        if (canceled) {
            return;
        }
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
        INetEvent<T> netEvent = theNetEvent();
        if (netEvent != null) {
            netEvent.onErrorResponse(requestType,errorMsg);
        }
    }

    protected INetEvent<T> theNetEvent() {
        if (weakRefNetEvent != null) {
            return weakRefNetEvent.get();
        }
        return null;
    }

}
