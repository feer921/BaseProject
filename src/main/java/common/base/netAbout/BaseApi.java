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

    protected static <T> void cacheRequestCall(Call<T> curCallRequest) {
        if (curCallRequest == null) {
            return;
        }

    }
}
