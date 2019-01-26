package common.base.activitys;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.OkgoNetCallback;

import common.base.netAbout.INetEvent;
import common.base.netAbout.NetDataAndErrorListener;
import common.base.netAbout.NetRequestLifeMarker;
import common.base.utils.GenericsParamUtil;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-24
 * Time: 15:13
 * DESC: 有访问请求的Activity基类
 * 注：范型<T>表示网络请求响应的数据类型,eg. T = JsonObject ,则要求网络请求后返回JsonObject类型数据
 */
public abstract class BaseNetCallActivity<T> extends BaseActivity implements INetEvent<T>{

    /**
     * 是否在界面退出的时候需要取消当前所有的网络请求
     * 目前为测试阶段，各子类可根据实际情况决定是否需要，比如：某个子类只有一个网络请求，并且该请求已经返回了，则不需要本基类进行取消全部网络请求的操作
     */
    protected boolean needCancelAllNetRequestWhenExit = true;
    /**
     * 网络请求失败
     *
     * @param requestDataType 当前请求类型
     * @param errorInfo       错误信息
     */
    @Override
    public final void onErrorResponse(int requestDataType, String errorInfo) {
        if (LIFE_CIRCLE_DEBUG) {
            e(null, "--> onErrorResponse() requestDataType = " + requestDataType + " errorInfo = " + errorInfo);
        }
        //如果用户主动取消了当前网络请求即Loading dialog被取消了(实际上该请求已到达服务端,因而会响应回调)
        //则不让各子类处理已被用户取消了的请求
        if (curRequestCanceled(requestDataType)) {
            return;
        }
        addRequestStateMark(requestDataType, NetRequestLifeMarker.REQUEST_STATE_FINISHED);
        dealWithErrorResponse(requestDataType,errorInfo);
    }
    protected void dealWithErrorResponse(int curRequestDataType,String errorInfo) {

    }
    /**
     * 网络请求的响应
     *
     * @param requestDataType 当前网络请求数据类型
     * @param result          响应实体
     */
    @Override
    public final void onResponse(int requestDataType, T result) {
        if (LIFE_CIRCLE_DEBUG) {
            i(null, "--> onResponse() requestDataType = " + requestDataType + " result = " + result);
        }
        if (curRequestCanceled(requestDataType)) {
            return;
        }
        addRequestStateMark(requestDataType, NetRequestLifeMarker.REQUEST_STATE_FINISHED);
        dealWithResponse(requestDataType,result);
    }

    protected void dealWithResponse(int requestDataType, T result) {

    }

    /**
     * 本基类提供一个只适合同步进行网络请求监听的对象
     * 即requestType需要调用时赋值，并且一个请求完成后才能赋值下一个请求类型
     * 所以是只适合网络请求(含多个网络请求)同步进行请求，不然请求后的响应会乱
     * 如果有多个网络请求各自请求的情况下，请使用{@linkplain #createANetListener()}来设置给多个请求的各个请求接口
     */
    protected NetDataAndErrorListener<T> netDataAndErrorListener;

    protected void initNetDataListener() {
        if (netDataAndErrorListener == null) {
            netDataAndErrorListener = createANetListener();
        }
    }

    /**
     * 增加对Okgo网络请求框架关于网络响应回调的监听
     */
    protected OkgoNetCallback<T> okgoRequestCallback;

    protected void initOkgoNetDataListener() {
        if (okgoRequestCallback == null) {
            okgoRequestCallback = createOkgoNetListener();
        }
    }

    /**
     * 按tag 取消 Okgo 的网络请求
     * @param toCancelTag
     */
    protected void cancelOkGoNetWork(Object toCancelTag) {
        OkGo.getInstance().cancelTag(toCancelTag);
    }
    protected OkgoNetCallback<T> createOkgoNetListener() {
        return new OkgoNetCallback<>(getSubClasGenericsParamClass(),this);
    }

    /**
     * 获取本基类的子类所指定的泛型T的具体的对象类型的Class对象
     * 即：最多有效到第一个非泛型参数的本基类的子类
     * changed by fee 2017-04-29:如果本基类的子类已经是一个实体类[非泛型参数类]的子类时，需要自己指定泛型的class类型了
     * eg.: FirstSubClasss extends BaseNetCallActivity<ServerResut>{} 在FirstSubClass是可以自动获取到泛型参数为ServerResult的
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
    /**
     * 创建一个网络请求监听者，适合需求多个网络请求的异步进行的情况下，对每一个单独的网络请求创建一个网络请求监听者
     * @return
     */
    protected NetDataAndErrorListener<T> createANetListener() {
        return new NetDataAndErrorListener<>(this);
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


    @Override
    public void finish() {
        //added by fee 2016-8-19 test now
        if (needCancelAllNetRequestWhenExit) {
            netRequestLifeMarker.cancelCallRequest(-1);
        }
        super.finish();
    }

    //-------- added by fee 2016-12-13 ---------------------------
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
            BaseNetCallActivity.this.onErrorBeforeRequest(curRequestDataType, errorType);
        }
    }

    /***
     * 用来处理非本基类被指定的网络请求响应数据类型T类型，而是其他网络响应类型的结果
     * @param requestDataType 当前网络请求类型
     * @param responseResut 网络请求响应结果 这里为Object对象类型来通用，子类如果处理此回调时，自己强转成预期对象类型
     */
    protected void dealWithETypeResponse(int requestDataType, Object responseResut) {
    }
}
