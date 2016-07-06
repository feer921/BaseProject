package common.base.services;

import common.base.netAbout.INetEvent;
import common.base.netAbout.NetDataAndErrorListener;
import common.base.netAbout.NetRequestLifeMarker;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-07-04
 * Time: 20:08
 * DESC: 有网络请求的Service
 */
public class BaseNetCallService<T> extends BaseService implements INetEvent<T> {
    protected NetRequestLifeMarker netRequestLifeMarker = new NetRequestLifeMarker();

    @Override
    public void onCreate() {
        super.onCreate();
        initANetDataAndErrorListener();
    }

    protected NetDataAndErrorListener<T> mNetDataAndErrorListener;

    protected void initANetDataAndErrorListener() {
        if (mNetDataAndErrorListener == null) {
            mNetDataAndErrorListener = new NetDataAndErrorListener<>(this);
        }
    }
    protected NetDataAndErrorListener<T> createANewNetListener() {
        return new NetDataAndErrorListener<>(this);
    }
    protected boolean curRequestCanceled(int dataType) {
        if(netRequestLifeMarker != null){
            return netRequestLifeMarker.curRequestCanceled(dataType);
        }
        return false;
    }

    /***
     * 取消网络请求
     * @param curRequestType
     */
    protected void cancelNetRequest(int curRequestType) {
        if (netRequestLifeMarker != null) {
            netRequestLifeMarker.cancelCallRequest(curRequestType);
        }
    }
    /**
     * 标记当前网络请求的状态 : 正在请求、已完成、已取消等
     * @see {@link NetRequestLifeMarker#REQUEST_STATE_ING}
     * @param requestDataType
     * @param targetState
     */
    protected void addRequestStateMark(int requestDataType,byte targetState){
        if(netRequestLifeMarker != null){
            netRequestLifeMarker.addRequestToMark(requestDataType, targetState);
        }
    }
    @Override
    public final void onErrorResponse(int requestDataType, String errorInfo) {
        if (curRequestCanceled(requestDataType)) {
            return;
        }
        addRequestStateMark(requestDataType, NetRequestLifeMarker.REQUEST_STATE_FINISHED);
        dealWithErrorResponse(requestDataType, errorInfo);
    }

    /**
     * 各子类处理
     * @param requestDataType
     * @param errorInfo
     */
    protected void dealWithErrorResponse(int requestDataType, String errorInfo) {

    }

    @Override
    public final void onResponse(int requestDataType, T result) {
        if (curRequestCanceled(requestDataType)) {
            return;
        }
        addRequestStateMark(requestDataType, NetRequestLifeMarker.REQUEST_STATE_FINISHED);
        dealWithResponse(requestDataType, result);
    }

    /**
     * 子类处理服务器的正常响应
     * @param requestDataType
     * @param result
     */
    protected void dealWithResponse(int requestDataType, T result) {
    }

    @Override
    public void onErrorBeforeRequest(int curRequestDataType, int errorType) {

    }
}
