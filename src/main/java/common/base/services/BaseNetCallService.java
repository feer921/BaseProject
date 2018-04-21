package common.base.services;

import com.lzy.okgo.callback.OkgoNetCallback;

import common.base.netAbout.INetEvent;
import common.base.netAbout.NetDataAndErrorListener;
import common.base.netAbout.NetRequestLifeMarker;
import common.base.utils.GenericsParamUtil;

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
            mNetDataAndErrorListener = createANewNetListener();
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
     * 取消网络请求（注：该方法只适合本框架的Retrofit请求模块才会真正取消网络请求，
     * 如果使用的是本框架的OkGo请求模块，还请各APP的基类再进行处理一下,或本框架再优化来统一处理）
     * @param curRequestType 要取消的网络请求类型
     */
    protected void cancelNetRequest(int curRequestType) {
        if (netRequestLifeMarker != null) {
            netRequestLifeMarker.cancelCallRequest(curRequestType);
        }
    }
    /**
     * 某个请求类型的网络请求是否已经完成
     * @param requestDataType
     * @return
     */
    protected boolean curRequestFinished(int requestDataType) {
        if (netRequestLifeMarker != null) {
            return netRequestLifeMarker.curRequestLifeState(requestDataType) == NetRequestLifeMarker.REQUEST_STATE_FINISHED;
        }
        return false;
    }
    /**
     * 判断是否某些网络请求全部完成了
     * @param theRequestTypes 对应要加入判断的网络请求类型
     * @return true:所有参与判断的网络请求都完成了；false:只要有任何一个请求未完成即未完成。
     */
    protected boolean isAllRequestFinished(int... theRequestTypes) {
        if (theRequestTypes == null || theRequestTypes.length == 0) {
            return false;
        }
        for (int oneRequestType : theRequestTypes) {
            if (!curRequestFinished(oneRequestType)) {
                return false;//只要有一个请求没有完成，就是未全部完成
            }
        }
        return true;
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
    /**
     * 开始追踪、标记一个对应的网络请求类型的请求状态
     * @param curRequestDataType
     */
    protected void trackARequestState(int curRequestDataType) {
        if (netRequestLifeMarker != null) {
            netRequestLifeMarker.addRequestToMark(curRequestDataType, NetRequestLifeMarker.REQUEST_STATE_ING);
        }
    }
    @Override
    public final void onErrorResponse(int requestDataType, String errorInfo) {
        if (LIFE_CIRCLE_DEBUG) {
            e( "--> onErrorResponse() requestDataType = " + requestDataType + " errorInfo = " + errorInfo);
        }
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
        if (LIFE_CIRCLE_DEBUG) {
            i( "--> onResponse() requestDataType = " + requestDataType + " result = " + result);
        }
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

    //-------- added by fee 2016-12-14 ---------------------------
    /**
     *由于本基类的网络请求响应类型T一旦指定后，就只能响应一种类型的，而一些使用场景可能存在不同网络请求接口
     响应的数据类型可能和本基类所指定的T类型不相同(比如：如果本基类T被指定成JSONObject类型的，则要求所请求的网络接口都是满足是JsonObject类型数据，
     这时，如果有一个网络请求响应的不是JsonObject类型的，如github api接口就有响应的数据为JSONArray类型的，
     则这时子类就不能使用基类的netDataAndErrorListener
     以及createANetListener()了),所以为了通用各种响应类型，增加以下代码
     使用方法：如果本基类的网络请求响应类型被指定为用户自定义的对象如{@linkplain common.base.netAbout.BaseServerResult},而如果有一个网络请求
     假设为 BaseApi.getRespoOfAUser(String curGithubUser,NetDataAndErrorListener<List<Respo>>callback);此时直接调用该API并且传入本基类的
     netDataAndErrorListener或者createANetListener()都不匹配了，则需要这样使用
     NetDataAndErrorListener<List<Respo> listenner = createETypeListener();
     再调用BaseApi.getRespoOfAUser("feer921",listenner);
     * @param <E>
     * @return 满足此次网络请求响应的数据类型的监听者
     */
    protected <E> NetDataAndErrorListener<E> createETypeListener() {
        return new NetDataAndErrorListener<>(new ETypeNetEvent<E>());
    }
    /**
     * 子类如果有不同于本基类BaseNetCallActivity所指定的网络响应类型T
     * 的网络请求响应数据类型，那么可以直接new{@linkplain NetDataAndErrorListener}时传入本类New ETypeNetEvent(E)
     * @param <E>
     */
    public final class ETypeNetEvent<E> implements INetEvent<E>{
        private final static String LOG_TAG = "ETypeNetEvent";
        /**
         * 网络请求失败
         *
         * @param requestDataType 当前请求类型
         * @param errorInfo       错误信息
         */
        @Override
        public void onErrorResponse(int requestDataType, String errorInfo) {
            if (LIFE_CIRCLE_DEBUG) {
                e(TAG, LOG_TAG + "--> onErrorResponse() requestDataType = " + requestDataType + " errorInfo = " + errorInfo);
            }
            if (requestDataType <= 0) {
                //是否需要加此判断呢？？
                return;
            }
            //如果用户主动取消了当前网络请求如Loading dialog被取消了(实际上该请求已到达服务端,因而会响应回调)
            //则不让各子类处理已被用户取消了的请求
            if (curRequestCanceled(requestDataType)) {
                return;
            }
            addRequestStateMark(requestDataType, NetRequestLifeMarker.REQUEST_STATE_FINISHED);
            dealWithErrorResponse(requestDataType,errorInfo);
        }

        /**
         * 网络请求的响应
         *
         * @param requestDataType 当前网络请求数据类型
         * @param result          响应实体
         */
        @Override
        public void onResponse(int requestDataType, E result) {
            if (LIFE_CIRCLE_DEBUG) {
                i(TAG, LOG_TAG + "--> onResponse() requestDataType = " + requestDataType + " result = " + result);
            }
            if (requestDataType <= 0) {
                //是否要加此判断呢？？本框架使用者，都应该用一个整数值区分是哪个网络请求接口吧
                return;
            }
            if (curRequestCanceled(requestDataType)) {
                return;
            }
            addRequestStateMark(requestDataType, NetRequestLifeMarker.REQUEST_STATE_FINISHED);
            dealWithETypeResponse(requestDataType,result);
        }

        /**
         * 错误回调，在还没有开始请求之前，比如：一些参数错误
         *
         * @param curRequestDataType 当前网络请求类型
         * @param errorType          错误类型
         */
        @Override
        public void onErrorBeforeRequest(int curRequestDataType, int errorType) {
            BaseNetCallService.this.onErrorBeforeRequest(curRequestDataType, errorType);
        }
    }

    /***
     * 用来处理非本基类被指定的网络请求响应数据类型T类型，而是其他网络响应类型的结果
     * @param requestDataType 当前网络请求类型
     * @param responseResut 网络请求响应结果 这里为Object对象类型来通用，子类如果处理此回调时，自己强转成预期对象类型
     */
    protected void dealWithETypeResponse(int requestDataType, Object responseResut) {
    }


    /**
     * 2017-4-1
     * 增加对Okgo网络请求框架关于网络响应回调的监听
     */
    protected OkgoNetCallback<T> okgoRequestCallback;

    protected void initOkgoNetDataListener() {
        if (okgoRequestCallback == null) {
            okgoRequestCallback = createOkgoNetListener();
        }
    }

    protected OkgoNetCallback<T> createOkgoNetListener() {
        return new OkgoNetCallback<>(getSubClasGenericsParamClass(),this);
    }

    /**
     * 获取本基类的子类所指定的泛型T的具体的对象类型的Class对象
     * 即：最多有效到第一个非泛型参数的本基类的子类
     * changed by fee 2017-04-29:如果本基类的子类已经是一个实体类[非泛型参数类]的子类时，需要自己指定泛型的class类型了
     * eg.: FirstSubClasss extends BaseNetCallService<ServerResut>{} 在FirstSubClass是可以自动获取到泛型参数为ServerResult的
     * 但是如果 FirstSubClass 再有子类，则自动获取不到了，需要重写本方法，直接指定ServerResut.class
     * @return
     */
    protected Class<T> getSubClasGenericsParamClass(){
        return GenericsParamUtil.getGenericsParamCalss(getClass().getGenericSuperclass(),0);
    }
    /**
     * 意义参见：{@linkplain #createETypeListener()}
     * @param <E>
     * @return
     */
    protected <E> OkgoNetCallback<E> createETypeOkgoListener(Class<E> eTypeClass) {
        return OkgoNetCallback.create(eTypeClass,new ETypeNetEvent<E>());
    }
}
