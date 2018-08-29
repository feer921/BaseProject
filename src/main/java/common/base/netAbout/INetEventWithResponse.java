package common.base.netAbout;

import okhttp3.Response;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/8/29<br>
 * Time: 15:06<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */
public interface INetEventWithResponse<T> extends INetEvent<T> {
    /**
     * 网络请求的响应
     *
     * @param requestDataType 当前网络请求数据类型
     * @param result          响应实体
     * @param response       整个请求的响应，一般用来有些接口可能
     */
    void onResponse(int requestDataType, T result, Response response);

}
