package common.base.netAbout;

import android.util.SparseArray;

import common.base.netclients.RetrofitClient;

/**
 * note: 暂时不考虑多线程问题，因为目前所有的网络请求发起时都在主线程
 * 后续如果有在工作线程中发起，则需要考虑多线程问题
 * 本类功能 : 基于网络请求数据类型 的对 各请求进行生命周期的标记、追踪、取消
 * <br/>
 * 2015年9月28日-下午3:57:25
 * @author lifei
 */
public class NetRequestLifeMarker {
    /**
     * 各子类的所有请求类型及请求所处状态
     * 需要在各有进行数据(网络)请求的界面中主动Put进去 
     */
//    final SparseBooleanArray allRequestTypesAndStates = new SparseBooleanArray();
    private final SparseArray<Byte> allRequestTypesAndStates = new SparseArray<Byte>();
    /**
     * 请求状态 :正在进行
     */
    public static final byte REQUEST_STATE_ING = 1;
    /**
     * 请求状态 :发生了异常
     */
    public static final byte REQUEST_STATE_EXCEPTION = 2;
    /**
     * 请求状态 :用户取消
     */
    public static final byte REQUEST_STATE_CANCELED = 3;
    /**
     * 请求状态 :完成
     */
    public static final byte REQUEST_STATE_FINISHED = 4;
    /**
     * 请求状态 :无状态
     */
    public static final byte REQUEST_STATE_NON = 5;
    /**
     * 添加一个当前的请求去标记、追踪
     * @param requestDataType
     * @param requestState
     */
    public void addRequestToMark(int requestDataType,byte requestState){
        if(requestDataType > 0){
            allRequestTypesAndStates.put(requestDataType, requestState);
        }
    }
    /**
     * 判断当前请求是否已被取消
     * @param requestDataType
     * @return
     */
    public boolean curRequestCanceled(int requestDataType){
        Byte state = allRequestTypesAndStates.get(requestDataType);
        if(null == state) return false;
        return REQUEST_STATE_CANCELED == state;
    }
    /**
     * 当前请求类型的状态
     * @param requestDataType
     * @return {@link #REQUEST_STATE_ING} and so on...
     */
    public byte curRequestLifeState(int requestDataType){
        Byte result = allRequestTypesAndStates.get(requestDataType);
        if(result == null){
            return REQUEST_STATE_NON;
        }
        return result;
    }

    //    /**
//     * 标记 请求为取消状态，并作取消
//     * @param requestDataType 数据请求类型，小于0时为取消当前所有请求
//     */
//    public void cancelRequests(int requestDataType){
//        int requestCount = allRequestTypesAndStates.size();
//        if (requestCount == 0) {
//            return;
//        }
//        VolleyClient volleyClient = VolleyClient.getMe();
//        if(requestDataType >= 0){
//            allRequestTypesAndStates.put(requestDataType, REQUEST_STATE_CANCELED);
//            volleyClient.cancelRequestBaseType(requestDataType);
//        }
//        else{
//            for(int i = 0; i < requestCount ; i++){
//                int curRequestDataType = allRequestTypesAndStates.keyAt(i);
//                volleyClient.cancelRequestBaseType(curRequestDataType);
//                allRequestTypesAndStates.put(curRequestDataType, REQUEST_STATE_CANCELED);
//            }
//        }
//    }

    /**
     * 取消相应请求并标记该请求为被取消的
     * @param curCallRequestType >0 时取消对应的请求、否则取消全部
     */
    public void cancelCallRequest(int curCallRequestType) {
        int requestCount = allRequestTypesAndStates.size();
        if(requestCount == 0) return;
        RetrofitClient retrofitClient = RetrofitClient.getMe();
        if (curCallRequestType > 0) {
            retrofitClient.cancelCall(curCallRequestType);
            allRequestTypesAndStates.put(curCallRequestType,REQUEST_STATE_CANCELED);
        }
        else{
            retrofitClient.cancelAllCall();
            for(int i = 0; i < requestCount ; i++) {
                int requestTypeKey = allRequestTypesAndStates.keyAt(i);
                allRequestTypesAndStates.put(requestTypeKey,REQUEST_STATE_CANCELED);
            }
        }
    }
}
