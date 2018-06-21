package common.base.netAbout;

import common.base.utils.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-17
 * Time: 15:48
 * DESC: 网络请求的回调监听者,其中的范型T代表当前的请求具体要返回何种类型的数据
 */
public class NetDataAndErrorListener<T> implements Callback<T>{
    protected final String TAG = getClass().getSimpleName();
    /**
     * 默认请求类型为-1，表示没有赋值请求类型
     */
    public int requestType = -1;
    protected INetEvent<T> netEvent;
    public NetDataAndErrorListener(INetEvent<T> netEventCallback) {
        this.netEvent = netEventCallback;
    }

    /**
     * Invoked for a received HTTP response.
     * <p>
     * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
     * Call {@link Response#isSuccessful()} to determine if the response indicates success.
     *
     * @param call
     * @param response
     */
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            if (netEvent != null) {
                netEvent.onResponse(this.requestType,response.body());
            }
            callbackExtraInfo(response);
        }
    }

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     *
     * @param call
     * @param t
     */
    @Override
    public void onFailure(Call<T> call, Throwable t) {
        String errorMsg = INetEvent.ERR_UNKNOW;
        if (t != null) {
            if (!Util.isEmpty(t.getMessage())) {
                //                errorMsg = t.getMessage();
                //modified here by fee 2017-4-1:这样可以拿到更详细的异常信息
                errorMsg = t.toString();
            }
        }
        if (netEvent != null) {
            netEvent.onErrorResponse(this.requestType,errorMsg);
        }
    }

    /**
     * 回调额外的信息，各子类按需可重写此方法来获取额外的信息，比如Headers中的信息
     * @param response
     */
    protected void callbackExtraInfo(Response<T> response) {
        //do nothing here
    }

    /**
     * 在网络请求之前的回调错误,比如调用API时参数传入有误之类的
     * @param paramsErrorType 参数错误类型
     */
    public void callBackFailureBeforeRequest(int paramsErrorType) {
        if (netEvent != null) {
            netEvent.onErrorBeforeRequest(requestType,paramsErrorType);
        }
    }
}
