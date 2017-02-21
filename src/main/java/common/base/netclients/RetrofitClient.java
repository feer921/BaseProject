package common.base.netclients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import common.base.retrofitCase.JsonConverterFactory;
import common.base.utils.Util;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
    //added
    private Retrofit.Builder retrofitBuilder;
    private RetrofitClient() {
        if (mRetrofit == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            //屏蔽因为Json字符串中或者Java对象中不认识的属性而导致解析失败
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            //modified here by fee 2016-10-19:修改成使用Builder构建者模式来构建或者重新构建Retrofit对象
//            mRetrofit = new Retrofit.Builder()
//                    .baseUrl(HOST_BASE_URL)
//                    .addConverterFactory(new JsonConverterFactory())
//
////                    .addConverterFactory(GsonConverterFactory.create())
//                    .addConverterFactory(JacksonConverterFactory.create(objectMapper))
//                    .build();
            retrofitBuilder = new Retrofit.Builder();
            retrofitBuilder.baseUrl(HOST_BASE_URL)
                    .addConverterFactory(new JsonConverterFactory())

//                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper));
            mRetrofit = retrofitBuilder.build();//调用这里时，Retrofit类中会新建一个默认的OkhttClient对象
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
//        mRetrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .addConverterFactory(converterFactory)
//                .build();
        if (retrofitBuilder != null) {
            mRetrofit = retrofitBuilder.baseUrl(baseUrl).addConverterFactory(converterFactory).build();
        }
        else{
            mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .build();
        }
    }

    /**
     * 重置自定义的OkHttpClient客户端对象
     * @param customOkHttpClient
     * @return
     */
    public RetrofitClient resetOkHttpClient(OkHttpClient customOkHttpClient) {
        if (retrofitBuilder != null) {
            mRetrofit = retrofitBuilder.callFactory(customOkHttpClient).build();

        }
        return this;
    }

    public Retrofit.Builder getRetrofitBuilder() {
        return this.retrofitBuilder;
    }

    public void resetRetrofit() {
        if (this.retrofitBuilder != null) {
            mRetrofit = this.retrofitBuilder.build();
        }
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

    /**
     * 获取当前的Retrofit中所配置的OkhttClient对象
     * @return
     */
    public OkHttpClient getCurHttpClient() {
        if (mRetrofit != null) {
            return (OkHttpClient) mRetrofit.callFactory();
        }
        return null;
    }

    public void addLog() {

//        getCurHttpClient().interceptors().add()
    }
}
