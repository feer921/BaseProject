package common.base.fragments;

import common.base.netAbout.INetEvent;
import common.base.netAbout.NetDataAndErrorListener;
import common.base.netAbout.NetRequestLifeMarker;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-25
 * Time: 15:18
 * DESC: 有网络请求的碎片的基类
 */
public abstract class BaseNetCallFragment<T> extends BaseFragment implements INetEvent<T> {
    /**
     * 网络请求失败
     * 之所以本基类把该重载变为final型的，是因为本基类需要做统一的逻辑，即对用户主动取消了的请求给以拒绝处理
     * @param requestDataType 当前请求类型
     * @param errorInfo       错误信息
     */
    @Override
    public final void onErrorResponse(int requestDataType, String errorInfo) {
        //如果用户主动取消了当前网络请求即Loading dialog被取消了(实际上该请求已到达服务端,因而会响应回调)
        //则不让各子类处理已被用户取消了的请求
        if (curRequestCanceled(requestDataType)) {
            return;
        }
        addRequestStateMark(requestDataType, NetRequestLifeMarker.REQUEST_STATE_FINISHED);
        dealWithErrorResponse(requestDataType, errorInfo);
    }

    protected void dealWithErrorResponse(int requestDataType, String errorInfo) {
    }

    /**
     * 网络请求的响应
     *
     * @param requestDataType 当前网络请求数据类型
     * @param result          响应实体
     */
    @Override
    public final void onResponse(int requestDataType, T result) {
        if (curRequestCanceled(requestDataType)) {
            return;
        }
        addRequestStateMark(requestDataType, NetRequestLifeMarker.REQUEST_STATE_FINISHED);
        dealWithResponse(requestDataType,result);
    }

    /**
     * 子类在此处理网络请求结果的回调
     * @param requestDataType
     * @param result
     */
    protected void dealWithResponse(int requestDataType, T result) {
    }

    /**
     * 错误回调，在还没有开始请求之前，比如：一些参数错误
     *
     * @param curRequestDataType 当前网络请求类型
     * @param errorType          错误类型
     */
    @Override
    public void onErrorBeforeRequest(int curRequestDataType, int errorType) {

    }

    /**
     * 本基类提供一个只适合同步进行网络请求监听的对象
     * 即requestType需要调用时赋值，并且一个请求完成后才能赋值下一个请求类型
     * 所以是只适合同步请求，不然请求后的响应会乱
     */
    protected NetDataAndErrorListener<T> netDataAndErrorListener;

    protected void initNetDataListener() {
        if (netDataAndErrorListener == null) {
            netDataAndErrorListener = new NetDataAndErrorListener<T>(this);
        }
    }

    protected NetDataAndErrorListener createANetListener() {
        return new NetDataAndErrorListener<T>(this);
    }
}
