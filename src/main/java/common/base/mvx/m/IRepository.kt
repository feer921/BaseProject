package common.base.mvx.m

/**
 * @author fee
 * <P> DESC:
 * 数据层/业务层/数据仓库 的接口
 * </P>
 */
interface IRepository {

    /**
     * 对仓库的初始化或者初始化需要的数据
     */
    fun init()

}