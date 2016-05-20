package common.base.activitys;


import common.base.netAbout.ServerResult;

/**
 * 代理者处理事件回调接口
 * <br/>
 * 2015年9月8日-下午9:28:16
 * @author lifei
 */
public interface IProxyCallback {

    /**
     * 被代理者(雇佣代理者)是否处理服务正常响应结果,如果被代理者处理了,则代理者不处理
     * @param requestDataType 处理的当前是何种请求
     * @param result
     * @return true:[被代理者]处理了 false:交由[代理者]处理
     */
     public boolean ownerDealWithServerResult(int requestDataType, ServerResult result);
    

    /**
     * 被代理者(雇佣代理者)是否处理连接服务端异常结果,如果被代理者处理了,则代理者不处理
     * @param requestDataType 处理的当前是何种请求
     * @param errorInfo
     * @return true:[被代理者]处理了 false:交由[代理者]处理
     */
    public boolean ownerDealWithServerError(int requestDataType, String errorInfo);
    /**
     * 被代理者(宿主)想主动取消(网络)数据的请求,在各自实现中实现各网络请求的取消并标志好该请求已取消
     */
    public void ownerToCanceleRequest();
}
