package common.base.netBaseMvp;

/**
 * User: fee(lifei@cloudtone.com.cn)
 * Date: 2016-05-16
 * Time: 17:18
 * DESC: 网络操作的MVP中的P
 */
public interface INetBasePresenter<T> {
    void cancelRequest();

    void onResponse(T result);

    void onFailure(String failReasons);

    void requestStart(String requestHintMsg);
}
