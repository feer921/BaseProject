package common.base.netAbout;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-17
 * Time: 15:25
 * DESC: 网络事件接口；主要指网络请求后的结果回调
 */
public interface INetEvent<T>{
     static final String UNKNOW_ERROR = "unknow_error";
    /**
     * 手动设置的超时错误
     */
     static final String MANULLY_TIME_OUT = "manully_set_time_out";
    /**
     * 人工延迟 结束(Loading)
     */
     static final String MANULLY_DELAY_OVER = "manully_delay_over";
    /**
     * 网络请求失败
     * @param requestDataType 当前请求类型
     * @param errorInfo
     */
    abstract void onErrorResponse(int requestDataType,String errorInfo);
//    abstract void initNetEventListener();
    /**
     * 网络请求的响应
     * @param requestDataType
     * @param result
     */
    abstract void onResponse(int requestDataType,T result);
}
