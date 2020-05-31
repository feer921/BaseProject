/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.okgo.callback;

import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.lzy.okgo.utils.OkLogger;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版   本：1.0
 * 创建日期：2016/1/14
 * 描   述：抽象的回调接口
 * 修订历史：
 * ================================================
 */
public abstract class AbsCallback<T> implements Callback<T> {
    protected final String TAG = getClass().getSimpleName();
    /**
     * 默认请求类型为-1，表示没有赋值请求类型
     */
    public int requestType = -1;
    public Object callBackTag;
    /**
     * 取消了
     */
    public boolean canceled;
    @Override
    public void onStart(Request<T, ? extends Request> request) {
    }

    @Override
    public void onSuccess(Response<T> response) {
        T respBean = null;
        okhttp3.Response rawResponse = null;
        if (response != null) {
            respBean = response.body();
            rawResponse = response.getRawResponse();
        }
        onSuccess(respBean,rawResponse);
    }

    protected void onSuccess(T respBean, okhttp3.Response rawResponse) {

    }
    @Override
    public void onCacheSuccess(Response<T> response) {
    }

    @Override
    public void onError(Response<T> response) {
//        OkLogger.printStackTrace(response.getException());
        Exception e = null;
        if (response != null) {
            e = new Exception(response.getException());
        }
        onError(e);
    }

    public void onError(Exception ex) {
        OkLogger.printStackTrace(ex);
    }
    @Override
    public void onFinish() {
    }

    @Override
    public void uploadProgress(Progress progress) {
    }

    @Override
    public void downloadProgress(Progress progress) {
    }
    /**
     * added by fee 2019-01-26: 网络请求的取消标志
     * def: null;
     */
    protected Object obj4CancelTag = null;
    public Object getObj4CancelTag() {
        return obj4CancelTag;
    }

    public void setObj4CancelTag(Object obj4CancelTag) {
        this.obj4CancelTag = obj4CancelTag;
    }
}
