package common.base.interfaces;
import com.chad.library.adapter.base.BaseQuickAdapter;
import java.util.List;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-25
 * Time: 15:03
 * DESC: 对于有列表数据界面的共同方法的接口
 */
public interface ICommonActionsInListUi<TListData> {
    /**
     * 获取或者初始化RecyclerView的适配器
     * @return
     */
    BaseQuickAdapter<TListData> getRecyclerViewAdapter();

    /**
     * 列表数据请求成功,在此方法中做填充新增数据的逻辑
     * @param newData
     */
    void listDataRequestSuccess(List<TListData> newData);

    /**
     * 通过比较要新添加的数据与老列表数据，判断是否重复的数据
     * 如果重复的数据一般就不添加进原来的列表
     * @param oldDatas
     * @param willAddedOne
     * @return
     */
    boolean compareData(List<TListData> oldDatas, TListData willAddedOne);

    /**
     * 列表数据请求结束，实现此方法，通过判断一次请求数据后是否有列表数据来决定UI的显示逻辑等
     * @param hasListDataResult
     * @param errorInfoIfRequestFail
     */
    void listDataRequestFinish(boolean hasListDataResult, String errorInfoIfRequestFail);
}
