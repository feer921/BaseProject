package common.base.netBaseMvp;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-18
 * Time: 16:45
 * DESC:
 */
public class BaseNetPresenter<T> implements INetBasePresenter<T> {
    private INetBaseView netBaseView;

    @Override
    public void cancelRequest() {

    }

    @Override
    public void onResponse(T result) {

    }

    @Override
    public void onFailure(String failReasons) {

    }

    @Override
    public void requestStart(String requestHintMsg) {

    }
}
