package common.base.mvx.v;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import common.base.R;
import common.base.annotations.InvokeStep;
import common.base.netAbout.BaseServerResult;
import common.base.utils.NetHelper;
import common.base.utils.Util;
import common.base.views.SuperEmptyLoadingView;
import common.base.views.VRefreshLayout;


/**
 * *****************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2021/3/6<br>
 * Time: 22:11<br>
 * <P>DESC:
 * 通用的列表视图层
 * <ul>
 *     <li>可下拉刷新,由下拉刷新View提供功能</li>
 *     <li>可再添加头部视图，由子类自行使用根View的addView()实现</li>
 *     <li>可加载更多，由BaseQuickAdapter实现</li>
 *     <li>可显示空视图，由{@link #emptyLoadingView}实现</li>
 *     <li>可重写提供本视图布局，但需要保证本基类使用的View的ID一致</li>
 * </ul>
 * 一般子类需要关注的：
 * 1、如果加载列表数据；
 * 2、差异化的视图配置；
 * 3、如果解析列表数据
 * 等
 * <D> 为列表加载的 Item数据对象
 * <VH>为 适配器 ViewHolder的类型
 * </p>
 * ******************(^_^)***********************
 */
 public abstract class CommonPullRefreshRecyclerViewDelegate<D, VH extends BaseViewHolder> extends BaseViewDelegate implements
                                                                    VRefreshLayout.OnRefreshListener,
                                                                    BaseQuickAdapter.OnItemClickListener,
                                                                    BaseQuickAdapter.RequestLoadMoreListener ,
                                                                    SuperEmptyLoadingView.IoptCallback{
    /**
     * 下拉刷新View
     */
    protected VRefreshLayout vRefreshLayout;

    /**
     *
     */
    protected RecyclerView recyclerView;

    protected BaseQuickAdapter<D, VH> dataAdapter;

    public CommonPullRefreshRecyclerViewDelegate(@NonNull Context mContext) {
        super(mContext);
    }

    /**
     * 注意，如果RecyclerView使用的为自定义的LayoutManager来加载整个列表布局，则emptyLoadingView可能不能
     * 填充RecyclerView的宽、高来显示，会造成界面不好看
     */
    protected SuperEmptyLoadingView emptyLoadingView;
    /**
     * 当前页
     * def: 1
     */
    protected int curPage = 1;

    /**
     * 每页需要加载的数量
     * def: 20
     */
    protected int perPageDataCount = 20;
    /**
     * 总页数
     * def = 0
     */
    protected int totalPage = 0;
    /**
     * 是否需要加载更多功能
     * def: true 开启加载更多功能
     */
    protected boolean needLoadMore = true;

    /**
     * RecyclerView 列表的数据对应的请求类型
     * def: 0, 基类不可定
     */
    protected int theListDataRequestType;

    /**
     * 获取到数据后是否需要去重处理
     * def: false,默认对加载下来的网络数据不作去重处理
     */
    protected boolean isNeedPickOutRepeated;

    /**
     * 是否能下拉刷新数据，当初始化加载数据时
     * 因为子类现在都没有禁止能下拉刷新数据当初始化加载时，所以本基数默认禁止一下，以免初始化加载时用户能下拉刷新，会造成两次加载
     * def: false, 默认基类当初始化(第一次)加载数据时禁止掉下拉刷新控件的下拉刷新功能
     */
    protected boolean canPulldownWhenInitLoadingData = false;
    /**
     * 是否需要下拉刷新
     * 增加此变量以应对有些子类界面不需要下拉刷新功能,即相当于直接禁止掉下拉刷新
     * def:true：需要下拉刷新
     */
    protected boolean isNeedPulldownRefresh = true;
    /**
     * 使用{@link #emptyLoadingView}显示加载数据时的，当加载失败(没有网络等各异常)情况且列表中没有数据的情况下，是否
     * 需要让SuperEmptyLoadingView内的ExtraOpt 来显示errorInfo
     * def:false,不需要显示errorInfo
     */
    protected boolean emptyLoadFailNeedShowDebugInfo = false;

    /**
     * 加载失败、无数据的情况下，是否需要关心没有网络的情况并让emptyLoadingView显示没有网络的Case
     * def:false;
     */
    protected boolean emptyLoadFailNeedCareNoNet = false;

    /**
     * 是否需要当初始化时自动请求数据
     * def:true
     * 因为有些界面可能需要在 宿主[Activity]或者[Fragment]的[onResume]方法时才去请求数据，此时请把该属性置为false
     * 并且自行在相关位置请求数据
     */
    protected boolean isNeedAutoReqDataInit = true;

    /**
     * 当前界面是否需要页面上的默认loading
     * 该变量为了兼容设置为false时，一些界面需要使用阻塞式的loading(且避免本基类也出现loading的情况)来阻止用户可以点击其他位置
     * def:true
     */
    protected boolean isNeedDefPageLoading = true;

    /**
     * 是否将自动调用请求数据的方法，当宿主[Activity]或者[Fragment]的[onResume]方法时
     */
    protected boolean isWillAutoLoadDataOnResume = false;

    /**
     * 子类还是可以重写以提供更贴合项目的列表布局
     * @return
     */
    @Override
    public int provideVLayoutRes() {
        return R.layout.common_pull_refresh_recyclerview_list;
    }

    @Override
    public final void initViews(boolean isInitState, @Nullable Intent dataIntent, @Nullable Bundle extraData) {
        if (isInitState) {
            initHeaderViews();
            vRefreshLayout = findView(R.id.refreshLayout);
            vRefreshLayout.addOnRefreshListener(this);
            //添加下拉刷新头部 todo
//            vRefreshLayout.setHeaderView(new CustomPullDownHeaderView(this));

            recyclerView = findView(R.id.recycleview);

            vRefreshLayout.setBelongTag(getTAG());
            vRefreshLayout.setNestedCanScrolableView(recyclerView);

            initRefreshLayout(vRefreshLayout);

            initRecyclerView(recyclerView);

            dataAdapter = providedAdapterAndInit();

            emptyLoadingView = new SuperEmptyLoadingView(getMContext());
            initEmptyLoadingView(emptyLoadingView);

            emptyLoadFailNeedCareNoNet = true;

            dontShowNoMoreDataView = true;

            emptyLoadingView.withOptCallback(this);
//        if (isNeedDefPageLoading) {
//            emptyLoadingView.loading();
//        }
            dataAdapter.setEmptyView(emptyLoadingView);
            dataAdapter.setOnItemClickListener(this);
            if (needLoadMore) {//放在providedAdapterAndInit()是否更好
                //            dataAdapter.setEnableLoadMore(true);//下面已经设置开启加载更多功能了
                dataAdapter.setOnLoadMoreListener(this, recyclerView);
            }
            recyclerView.setAdapter(dataAdapter);
            initData();
        }
    }

    /**
     * 初始化 当前视图的头部视图
     */
    @InvokeStep(value = 1,desc = "invoke in initViews()")
    protected abstract void initHeaderViews();

    /**
     * 初始化VRefreshLayout： 设置自定义的刷新头部等
     *
     * @param vRefreshLayout
     */
    @InvokeStep(value = 2,desc = "invoke in initViews()")
    protected abstract void initRefreshLayout(VRefreshLayout vRefreshLayout);

    /**
     * 初始化RecyclerView：设置divider; LayoutManager等
     *
     * @param recyclerView
     */
    @InvokeStep(value = 3,desc = "invoke in initViews()")
    protected abstract void initRecyclerView(RecyclerView recyclerView);

    /**
     * 子类提供RecyclerView具体的适配器并且初始化: 设置加载更多视图、设置开启上拉加载更多功能、设置加载更多的监听者、加载初始化数据等
     *
     * @return BaseQuickAdapter的子类，必须返回
     */
    @InvokeStep(value = 4,desc = "invoke in initViews()")
    protected abstract BaseQuickAdapter<D, VH> providedAdapterAndInit();

    /**
     * 初始化SuperEmptyLoadingView：设置空内容时的图片、提示信息等
     *
     * @param emptyLoadingView
     */
    @InvokeStep(value = 5,desc = "invoke in initViews()")
    protected abstract void initEmptyLoadingView(SuperEmptyLoadingView emptyLoadingView);


    @InvokeStep(value = 6,desc = "invoke in initViews()")
    protected void initData() {
        prepare2LoadData();
        if (!canPulldownWhenInitLoadingData
                || !isNeedPulldownRefresh//added by fee : 子类不需要下拉刷新功能，但不影响下拉加载更多功能
        ) {
            vRefreshLayout.setEnabled(false);
        }

        //changed: 把初次加载数据是否需要空布局的loading逻辑放在这里，如果不需要，则清除空布局的默认显示数据
        if (isNeedDefPageLoading) {
            emptyLoadingView.loading();
        }
        else {
            emptyLoadingView.reset().andHintMsg("");
        }
        //@Note: 如果子类不想在initData()时自动去请求当前列表数据(比如想在onResume()回调时-因为本来该界面在回到前台时需要自动刷新) 则最好在onResume()回调时，调用
        //onRefresh()
        if (isNeedAutoReqDataInit) {
            loadTheListDatas();
        }
    }

    @Override
    public void onRefresh() {
        curPage = 1;
        if (isNeedDefPageLoading) {
            emptyLoadingView.loading();
        }
        loadTheListDatas();
    }

    @Override
    public void onLoadMoreRequested() {
        curPage++;
        loadTheListDatas();
    }


    /**
     * 准备去加载数据了，子类在此方法可以设置 本列表数据的请求类型、每页需要加载的数量等信息
     * 可以设置{@link #perPageDataCount}、{@link #theListDataRequestType}、
     * {@link #isNeedPickOutRepeated}
     *
     * @see #isRepeatedAfterCompareData(List, D)
     * 也可以设置{@link #needLoadMore},设置此属性时如果设置成false，需要调用一下adapter.setEnableLoadMore(needLoadMore);
     * dataAdapter.setOnLoadMoreListener(this, recyclerView);
     */
    protected abstract void prepare2LoadData();

    /**
     * 调用获取列表数据方法
     * 注：这里没有处理 curPage
     */
    private void loadTheListDatas() {
        loadListData();
    }

    /**
     * 子类提供加载当前列表数据的逻辑
     * 注：因为本基类为 视图层，所以加载数据子类自行处理，可调用[ViewModel]层来加载数据
     */
    protected abstract void loadListData();


    @Override
    public void onResume() {
        super.onResume();
        if (!isNeedAutoReqDataInit && isWillAutoLoadDataOnResume) {
            onRefresh();
        }
    }

    /**
     * Callback method to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param adapter  the adpater
     * @param view     The itemView within the RecyclerView that was clicked (this
     *                 will be a view provided by the dataAdapter)
     * @param position The position of the view in the dataAdapter.
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
    }

    /**
     * 子类实现解析出当前的列表数据
     * 如果有分页功能并且总页数服务端控制的情况下，在此方法实现时需要赋值{@link #totalPage}
     *
     * @param result 响应实体
     * @return 列表数据集
     */
    protected abstract List<D> parseOutListData(BaseServerResult result);

    protected final void dealWithResponse(int requestDataType, BaseServerResult result) {
        if (requestDataType == theListDataRequestType) {
            List<D> dataList = parseOutListData(result);
            if (dataList != null) {
                listDataRequestSuccess(dataList);
            }
            listDataRequestFinish(dataList, null);
        } else {
            dealWithOtherResponse(requestDataType, result);
        }
    }

    protected final void dealWithErrorResponse(int curRequestDataType, String errorInfo) {
        if (!handleErrorResponse(curRequestDataType, errorInfo)) {//如果子类不处理，则交由父类统一处理
            superDealWithErrorResponse(curRequestDataType,errorInfo);
        }
        if (curRequestDataType == theListDataRequestType) {
            listDataRequestFinish(null, errorInfo);
        } else {
            dealWithOtherErrorResp(curRequestDataType, errorInfo);
        }
    }

    protected void superDealWithErrorResponse(int curRequestDataType, String errorInfo) {
        // TODO: 2021/3/6
    }
    /**
     * 在网络请求回调错误方法时，给子类一个机会自己处理错误情况
     * @param curRequestDataType
     * @param errorInfo
     * @return false:父类统一处理；true:子类自己处理
     */
    protected boolean handleErrorResponse(int curRequestDataType, String errorInfo) {
        return false;
    }

    /**
     * 列表数据请求成功，可能需要去重处理
     * 该方法只处理请求成功并且解析列表数据集合成功,的添加数据功能逻辑
     *
     * @param newDataFromNetWork 来自服务器上的数据
     */
    protected void listDataRequestSuccess(List<D> newDataFromNetWork) {
        if (newDataFromNetWork == null || newDataFromNetWork.isEmpty()) {
            return;
        }
        if (curPage == 1) {//仍然是在第一页请求数据，比如下拉刷新
            dataAdapter.clearData();//第一页时是否要清空呢？？或者提供给子类处理
        }
        //处理可能重复的数据
        if (isNeedPickOutRepeated) {
            //            int newDataCount = newDataFromNetWork.size();
            for (int i = 0; i < newDataFromNetWork.size(); i++) {
                if (isRepeatedAfterCompareData(dataAdapter.getData(), newDataFromNetWork.get(i))) {
                    newDataFromNetWork.remove(i);
                    i--;
                }
            }
        }
        if (!newDataFromNetWork.isEmpty()) {
            dataAdapter.addData(newDataFromNetWork);
        }
    }

    /**
     * 下发的服务器数据与已存在的数据是否有重复
     * 子类可重写进行对应的比较
     *
     * @param existedInAdapteDatas 已经存在适配器中的数据
     * @param preToAddOne          将要添加的数据
     * @return true:重复了；false:未重复/则添加进来
     */
    protected boolean isRepeatedAfterCompareData(List<D> existedInAdapteDatas, D preToAddOne) {
        return false;
    }

    /**
     * 列表数据请求完成，不管是失败还是成功时都调用
     * 子类也可以重写里面的具体逻辑，本基类提供通用处理方式
     *
     * @param pareseResult 解析出的数据，可能会为null,为null时为数据加载失败；非null为加载数据成功但可能为empty[因为所加载的数据完全重复被剔除了]
     * @param errorInfos   异常信息,如果有的话
     */
    protected void listDataRequestFinish(@Nullable List<D> pareseResult, @Nullable String errorInfos) {
        boolean isErrorResp = pareseResult == null && !isEmpty(errorInfos);
        if (curPage == 1) {
            if (!canPulldownWhenInitLoadingData) {//added by fee 2017-10-10 初始化加载数据时(此时直接是SuperEmptyLoadingView在Loading),禁用了
                if (isNeedPulldownRefresh) {//added by fee 2018-03-19:增加，只有子类在需要下拉刷新功能时才让该控件有效
                    vRefreshLayout.setEnabled(true);
                }
                canPulldownWhenInitLoadingData = true;
            }
            emptyLoadingView.showCase(SuperEmptyLoadingView.LayoutStatus.NoData);
            vRefreshLayout.refreshComplete();//如果这里是初始化加载时，vRefreshLayout没有在refreshing...??
            //如果pareseResult==null情况需要提示刷新失败？？？

            //            if (needLoadMore) {
            //                //可能之前已经翻页到底而禁用了loadMore功能，如果又重新从第1页下拉数据时，则需要再打开
            //                dataAdapter.setEnableLoadMore(curPage < totalPage);
            //            }
            loadFirstPageDataOver(dataAdapter.getJustDataCount() == 0, errorInfos);
            if (isErrorResp && needLoadMore && totalPage == 0) {//也有可能，服务端就下发了 totalPage == 0所以要加上isErrorResp
                totalPage = 2;
            }
        } else {//非第一页，加载更多结束,这里能执行即说明开启了加载更多功能
            dataAdapter.loadMoreComplete();
            if (pareseResult == null) {
                //加载更多(翻页)失败
                curPage--;//回退一页
                dataAdapter.loadMoreFail();//刷新失败,点击刷新时，如果当前翻页到3，由于上而回退到2，加载第3页失败后，点击重新加载仍然是3
            }
            //            dealWithNoMorePages(curPage >= totalPage);
        }
        if (needLoadMore) {
            dealWithEitherNoMorePage(!isErrorResp);
        }
        //
        //        //只有加载第一页数据的情况下以下逻辑才有意义，因为第2页以后，不可能没有数据
        //        boolean noDataInAdapter = dataAdapter.getJustDataCount() == 0;
        //        //列表没有数据且有异常
        //        if (noDataInAdapter) {
        //            if (!Util.isEmpty(errorInfos)) {//有异常
        //                //test
        //                emptyLoadingView.withExtraHintMsg(errorInfos);
        //            }
        //        }
        goOnDealWithTheData(pareseResult);
    }

    /**
     * 当没有下一页数据的时候是否要显示"没有更多数据"的提示view
     * 不要显示 true:不要显示；false: 不是不要显示即要显示
     * def:false 要显示
     */
    protected boolean dontShowNoMoreDataView = false;
    /**
     * 在开启了需要加载更多功能情况下：处理是否没有更多页了
     * 不管是第一页加载完成,还是加载更多页时的加载完成都需要判断
     * 开启了加载更多功能，可能存在一个隐患，如果在加载第一页前就已经给Adapter设置了数据，可能会触发加载下一页？？碰到再说吧
     * 子类不服可重写该通用处理逻辑
     * @param isRespOk 增加是否请求正常响应的参数判断，因为如果是请求异常了，来判断有没有更多页不合理
     */
    protected void dealWithEitherNoMorePage(boolean isRespOk) {
        if (needLoadMore && isRespOk) {
            //??如果totalPage==0,即第一页就没加载成功
            boolean hasMorePage = curPage < totalPage;
            if (curPage == 1) {//第一页时：第一次加载、下拉刷新时情况
                if (hasMorePage) {
                    dataAdapter.setEnableLoadMore(true);//如果之前已经加载到最后一页了，再下拉刷新第一页后 又需要启用加载更多功能
                }
                else {//在加载第一页就判断了没有更多页，可能的情况为：totalPage == 0，或者 totalPage == 1
                    if (isRespOk) {//服务端正常响应的情况下，第1页就没有更多页了,(纠正请求异常情况下 totalPage压根没有赋值,而显示没有更多数据)
                        dataAdapter.loadMoreEnd(dontShowNoMoreDataView);//提示没有更多数据
                    }
                }
            } else {//第2页...第N页时处理逻辑,此时totalPage > 1
                //加载更多
                if (!hasMorePage) {
                    dataAdapter.loadMoreEnd(dontShowNoMoreDataView);//提示没有更多数据
                }
            }
        }
    }

    protected boolean needCareLoadFail = false;

    /**
     * 加载第一页(也可能子类的界面就是一页，无分页功能)完成的处理
     * 本基类只对 第一面、并且没有数据并且异常信息不为空的情况处理
     * 子类可重写该方法，处理差异化的功能逻辑
     *
     * @param noDataInAdapter 加载完成后，是否在Adapter中没有数据
     * @param errorInfos      异常信息：为null时为无异常，不为null时子类视具体情况处理,eg.:提示刷新失败、提示什么原因导致的加载异常等
     */
    protected void loadFirstPageDataOver(boolean noDataInAdapter, @Nullable String errorInfos) {
        if (curPage == 1) {//该条件可不写，因为该方法即在curPage==1时才调用
            //列表没有数据且有异常
            if (noDataInAdapter) {
                if (!Util.isEmpty(errorInfos)) {//有异常
                    if (needCareLoadFail) {
                        emptyLoadingView.showCase(SuperEmptyLoadingView.LayoutStatus.LoadFailure);
                    }
                    if (emptyLoadFailNeedCareNoNet) {
                        if (!NetHelper.isNetworkConnected(peekAppInstance())) {//没有网络的情况下
                            emptyLoadingView.showCase(SuperEmptyLoadingView.LayoutStatus.NoNetWork);
                        }
                    }
                    if (emptyLoadFailNeedShowDebugInfo) {
                        emptyLoadingView.andExtraHintMsg(errorInfos);
                    }

                    //                    if (!NetHelper.isNetworkConnected(appContext)) {//没有网络的情况下
                    //                        if (emptyLoadFailNeedCareNoNet) {
                    //                            emptyLoadingView.showCase(SuperEmptyLoadingView.LayoutStatus.NoNetWork);
                    //                        }
                    //                    }
                    //                    else{//有网络连接的情况下
                    //                        if (emptyLoadFailNeedShowDebugInfo) {
                    //                            emptyLoadingView.andExtraHintMsg(errorInfos);
                    //                        }
                    //                    }
                }
            }
        }
    }

    /**
     * 加载完成数据后，继续处理所添加的数据，例如：是否保存到本地
     *
     * @param paresedResult 解析出的数据，可能会为null,为null时为数据加载失败；
     *                      非null为加载数据成功但可能为empty[因为所加载的数据完全重复被剔除了]
     */
    protected void goOnDealWithTheData(@Nullable List<D> paresedResult) {

    }

    /**
     * 如果子类还有其他的网络请求,重写此方法来处理正常的响应
     *
     * @param requestDataType 当前请求的类型
     * @param result          响应结果
     */
    protected void dealWithOtherResponse(int requestDataType, BaseServerResult result) {

    }

    /**
     * 如果子类还有其他的网络请求时，重写此方法来处理异常的响应
     *
     * @param curRequestDataType 当前请求的类型
     * @param errorInfo          异常信息
     */
    protected void dealWithOtherErrorResp(int curRequestDataType, String errorInfo) {
    }
    //added by fee 2018-10-16:统一处理SuperEmptyLoadingView的点击事件
    //统一为 :除没有数据时，不管是加载失败还是没有网络，点击后都继续加载
    @Override
    public void optCallback(SuperEmptyLoadingView theEmptyLoadingView, SuperEmptyLoadingView.LayoutStatus curLayoutStatus) {
        if (curLayoutStatus != SuperEmptyLoadingView.LayoutStatus.NoData) {
            theEmptyLoadingView.loading();
            onRefresh();
        }
    }
}
