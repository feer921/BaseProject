package common.base.mvx.m

import androidx.lifecycle.Observer

/**
 ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2021/4/4<br>
 * Time: 23:05<br>
 * <P>DESC:
 * 接口：[Observer] 的子接口，默认重写 [Observer]的接口方法
 * </p>
 * ******************(^_^)***********************
 */
interface IObserver<D> : Observer<D> {

    override fun onChanged(t: D?) {
        onDataChange(t)
    }

    /**
     * 当前数据/响应数据 的回调
     * @param data nullable;
     */
    fun onDataChange(data: D?)

}