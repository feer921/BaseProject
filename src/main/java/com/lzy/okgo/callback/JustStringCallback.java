package com.lzy.okgo.callback;

import java.lang.ref.WeakReference;

import common.base.netAbout.INetEvent;
import okhttp3.Response;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/1/17<br>
 * Time: 20:24<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */
public class JustStringCallback extends StringCallback {
    protected WeakReference<INetEvent<String>> weakRefNetEvent;

    public JustStringCallback(INetEvent<String> event) {
        if (event != null) {
            weakRefNetEvent = new WeakReference<>(event);
        }
    }




    @Override
    public void onSuccess(String s, Response response) {
        INetEvent<String> netEvent = getNetEvent();
        if (netEvent != null) {
            netEvent.onResponse(requestType,s);
        }
//        if (getNetEvent() != null) {
//            getNetEvent().onResponse(requestType,s);
//        }
    }

    @Override
    public void onError(Exception e) {
        if (getNetEvent() != null) {
            String errorInfo = INetEvent.ERR_UNKNOW;
            if (e != null) {
                errorInfo = e.toString();
            }
            getNetEvent().onErrorResponse(requestType,errorInfo);
        }
    }

    private INetEvent<String> getNetEvent() {
        if (weakRefNetEvent != null) {
            return weakRefNetEvent.get();
        }
        return null;
    }
}
