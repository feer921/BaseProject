package common.base.netAbout;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-17
 * Time: 15:25
 * DESC: 网络事件接口；主要指网络请求后的结果回调
 */
public interface INetEvent<T> {
    /**
     * 错误之：未知
     */
    String ERR_UNKNOW = "unknow_error";
    /**
     * 手动设置的超时错误
     */
    String MANULLY_TIME_OUT = "manully_set_time_out";
    /**
     * 人工延迟 结束(Loading)
     */
    String MANULLY_DELAY_OVER = "manully_delay_over";
    /**
     * 错误之：未知host(解析不出主机地址),可能是由于没有网络引起
     */
    String ERR_UNKNOW_HOST = "unknow_host";
    /**
     * 网络请求失败
     *
     * @param requestDataType 当前请求类型
     * @param errorInfo       错误信息
     */
    void onErrorResponse(int requestDataType, String errorInfo);
    //    abstract void initNetEventListener();

    /**
     * 网络请求的响应
     *
     * @param requestDataType 当前网络请求数据类型
     * @param result          响应实体:即表示将网络响应的结果转化成指定的数据实体bean
     */
    void onResponse(int requestDataType, T result);

    /**
     * 错误回调，在还没有开始请求之前，比如：一些参数错误
     *
     * @param curRequestDataType 当前网络请求类型
     * @param errorType          错误类型
     */
    void onErrorBeforeRequest(int curRequestDataType, int errorType);

//    /**
//     * 当前请求是否可以回调结果
//     * 目的：为了避免Activity已经finish的情况下，仍有网络回调的问题
//     * 注：一般要为true
//     * @param curRequestDataType 当前请求类型，为了兼容可以某个请求有特殊需求
//     * @return true:正常响应、异常响应，可以回调；false:不能回调
//     */
//    boolean canCallbackResult(int curRequestDataType);
}
