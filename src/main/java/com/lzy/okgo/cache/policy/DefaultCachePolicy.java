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
package com.lzy.okgo.cache.policy;

import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.exception.CacheException;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import okhttp3.Call;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DefaultCachePolicy<T> extends BaseCachePolicy<T> {

    public DefaultCachePolicy(Request<T, ? extends Request> request) {
        super(request);
    }

    @Override
    public void onSuccess(final Response<T> success) {
        if (mCallback != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callbackOnSuccess(success);
                    callbackOnFinish();
//                    mCallback.onSuccess(success);
//                    mCallback.onFinish();
                }
            });
        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mCallback.onSuccess(success);
//                mCallback.onFinish();
//            }
//        });
    }

    @Override
    public void onError(final Response<T> error) {
        if (mCallback != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callbackOnError(error);
                    callbackOnFinish();
//                    mCallback.onError(error);
//                    mCallback.onFinish();
                }
            });
        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mCallback.onError(error);
//                mCallback.onFinish();
//            }
//        });
    }

    @Override
    public boolean onAnalysisResponse(final Call call, final okhttp3.Response response) {
        if (response.code() != 304) return false;

        if (cacheEntity == null) {
            final Response<T> error = Response.error(true, call, response, CacheException.NON_AND_304(request.getCacheKey()));
            if (mCallback != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callbackOnError(error);
                        callbackOnFinish();
//                        mCallback.onError(error);
//                        mCallback.onFinish();
                    }
                });
            }
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mCallback.onError(error);
//                    mCallback.onFinish();
//                }
//            });
        } else {
            final Response<T> success = Response.success(true, cacheEntity.getData(), call, response);
            if (mCallback != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callbackOnCacheSuccess(success);
                        callbackOnFinish();
//                        mCallback.onCacheSuccess(success);
//                        mCallback.onFinish();
                    }
                });
            }
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mCallback.onCacheSuccess(success);
//                    mCallback.onFinish();
//                }
//            });
        }
        return true;
    }

    @Override
    public Response<T> requestSync(CacheEntity<T> cacheEntity) {
        try {
            prepareRawCall();
        } catch (Throwable throwable) {
            return Response.error(false, rawCall, null, throwable);
        }
        Response<T> response = requestNetworkSync();
        //HTTP cache protocol
        if (response.isSuccessful() && response.code() == 304) {
            if (cacheEntity == null) {
                response = Response.error(true, rawCall, response.getRawResponse(), CacheException.NON_AND_304(request.getCacheKey()));
            } else {
                response = Response.success(true, cacheEntity.getData(), rawCall, response.getRawResponse());
            }
        }
        return response;
    }

    @Override
    public void requestAsync(CacheEntity<T> cacheEntity, Callback<T> callback) {
        mCallback = callback;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callbackOnStart(request);
//                mCallback.onStart(request);

                try {
                    prepareRawCall();
                } catch (Throwable throwable) {
                    Response<T> error = Response.error(false, rawCall, null, throwable);
                    callbackOnError(error);
//                    mCallback.onError(error);
                    return;
                }
                requestNetworkAsync();
            }
        });
    }
}
