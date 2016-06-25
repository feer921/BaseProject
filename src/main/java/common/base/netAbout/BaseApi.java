package common.base.netAbout;

import common.base.netclients.RetrofitClient;
import retrofit2.Call;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-24
 * Time: 13:20
 * DESC: 网络请求的基础API相关，各APP可继承，也可以按这思路自己写(因为不是通用)
 */
public class BaseApi{
    /**
     * 与用户相关的API分类
     */
    protected static final int API_CATEGORY_ABOUT_USER = 1;
    /**
     * 与账户相关的API分类
     */
    protected static final int API_CATEGORY_ABOUT_ACCOUNT = API_CATEGORY_ABOUT_USER + 1;
    /**
     * 与产品相关的API分类
     */
    protected static final int API_CATEGORY_ABOUT_PRODUCT = API_CATEGORY_ABOUT_ACCOUNT + 1;
    //and so on.

    protected static <T> T getApiService(Class<T> serviceClass) {
        return RetrofitClient.getMe().createNetService(serviceClass);
    }

    /**
     * 缓存一个已经执行的请求
     * @param curCallRequest
     * @param callRequestType
     */
    protected static void cacheRequestCall(Call curCallRequest,int callRequestType) {
        if (curCallRequest == null) {
            return;
        }
        RetrofitClient.getMe().cacheCall(curCallRequest,callRequestType);
    }

    /**
     * 取消对应的请求
     * @param toCancelCallRequestType 对应的网络请求类型
     */
    public void cacelCurCall(int toCancelCallRequestType) {
        RetrofitClient.getMe().cancelCall(toCancelCallRequestType);
    }
    protected static void doCall(Call curCall, NetDataAndErrorListener curCallBack,boolean needCache) {
        if (curCall != null) {
            curCall.enqueue(curCallBack);
            if(needCache){
                cacheRequestCall(curCall,curCallBack.requestType);
            }
        }
    }
}
