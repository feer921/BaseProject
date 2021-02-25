package common.base.mvx.m

import common.base.mvx.v.IView

/**
 * @author fee
 * <P> DESC:
 * 视图层绑定 数据层
 * </P>
 */
interface IDataBinder<V : IView, M : IModel> {
    fun viewBindData(v: V, data: M?)
}