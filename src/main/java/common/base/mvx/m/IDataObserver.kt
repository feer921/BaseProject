package common.base.mvx.m

import androidx.lifecycle.Observer

/**
 ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2021/4/4<br>
 * Time: 11:46<br>
 * <P>DESC:
 * [Observer] 的子接口，目的为增加 当前数据被观察到改变时增加回调当前的数据类型
 * </p>
 * ******************(^_^)***********************
 */
interface IDataObserver<DataType, D> : Observer<D> {

    override fun onChanged(t: D?) {
        onDataChanged(null,t)
    }

    fun onDataChanged(dataType: DataType?, theData: D?)

}