package common.base.netclients;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import common.base.retrofitCase.JsonConverterFactory;
import common.base.utils.Util;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * User: fee(lifei@cloudtone.com.cn)
 * Date: 2016-05-16
 * Time: 16:19
 * DESC: 基于Retrofit的网络请求客户端
 * retrofit2官网地址：https://github.com/square/retrofit/
 */
public class RetrofitClient {
    private static RetrofitClient retrofitClient;
    private Retrofit mRetrofit;
    /**
     * 使用之前先配置一下要访问的服务器的基础地址
     */
    public static String HOST_BASE_URL;
    private WeakHashMap<Call,Integer> cachedCalls;
    private final Object syncLockObj = new Object();
    private RetrofitClient() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(HOST_BASE_URL)
                    .addConverterFactory(new JsonConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    /**
     * 如果有自定义的ConverterFactory，则依设置进来的对象重新构建Retrofit
     *
     * @param baseUrl
     * @param converterFactory
     */
    public void initRetrofitModule(String baseUrl, Converter.Factory converterFactory) {
        if (Util.isEmpty(baseUrl)) {
            return;
        }
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .build();
    }

    public static RetrofitClient getMe() {
        if (Util.isEmpty(HOST_BASE_URL)) {
            throw new NullPointerException("please config the host base url first");
        }
        if (retrofitClient == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitClient == null) {
                    retrofitClient = new RetrofitClient();
                }
            }
        }
        return retrofitClient;
    }

    /**
     * 通过Retrofit 创建访问服务器的接口
     *
     * @param iServiceClass
     * @param <T>
     * @return
     */
    public <T> T createNetService(Class<T> iServiceClass) {
        if (mRetrofit != null) {
            return mRetrofit.create(iServiceClass);
        }
        return null;
    }

    /**
     * 缓存当前的一个网络请求
     * @param curCallRequest 当前网络请求
     * @param curRequestType 网络请求类型
     */
    public void cacheCall(Call curCallRequest,int curRequestType) {
        if (cachedCalls == null) {
            cachedCalls = new WeakHashMap<>();
        }
        synchronized (syncLockObj) {
            cachedCalls.put(curCallRequest,curRequestType);
        }
    }

    /**
     * 取消对应的一个网络请求
     * @param callRequestType 要对应取消的网络请求类型
     */
    public void cancelCall(int callRequestType) {
        if (cachedCalls == null || cachedCalls.isEmpty()) {
            return;
        }
        Call toCancelCall = null;
        synchronized (syncLockObj) {
            Iterator entrySetIterator = cachedCalls.entrySet().iterator();
            while (entrySetIterator.hasNext()) {
                Map.Entry<Call,Integer> entry = (Map.Entry<Call, Integer>) entrySetIterator.next();
                int curValue = entry.getValue();
                if (curValue == callRequestType) {
                    toCancelCall = entry.getKey();
                    break;
                }
            }
            if (toCancelCall != null) {
                cachedCalls.remove(toCancelCall);
                toCancelCall.cancel();
            }
        }
    }

    /**
     * 取消所以添加过的网络请求
     */
    public void cancelAllCall() {
        if (cachedCalls == null || cachedCalls.isEmpty()) {
            return;
        }
        synchronized (syncLockObj) {
            Iterator<Call> callIterator = cachedCalls.keySet().iterator();
            while (callIterator.hasNext()) {
                Call curCall = callIterator.next();
                if (curCall != null) {
                    curCall.cancel();
                }
            }
            cachedCalls.clear();
        }
    }
}
