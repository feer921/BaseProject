package common.base.mvx.m

import java.lang.ref.WeakReference

/**
 ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2021/4/4<br>
 * Time: 11:52<br>
 * <P>DESC:
 * [IDataObserver] 的一个实现类
 * 可赋值 被观察的 数据类型；
 * 可赋值，外部的 [IDataObserver]接口实现类，目的为再回调外部
 * </p>
 * ******************(^_^)***********************
 */
open class AbsDataObserver<DataType, Data>(
    var curDataType: DataType?,
    iDataObserver: IDataObserver<DataType, Data>?
) : IDataObserver<DataType, Data> {

    //todo 这里是否需要变成 弱引用？？
    private var weakRefDataObserver: WeakReference<IDataObserver<DataType, Data>>? = null

    init {
        if (iDataObserver != null) {
            weakRefDataObserver = WeakReference(iDataObserver)
        }
    }
    override fun onChanged(t: Data?) {
        onDataChanged(curDataType,t)
    }

    override fun onDataChanged(dataType: DataType?, theData: Data?) {
//        iDataObserver?.onDataChanged(dataType, theData)
        weakRefDataObserver?.get()?.onDataChanged(dataType, theData)

    }

}