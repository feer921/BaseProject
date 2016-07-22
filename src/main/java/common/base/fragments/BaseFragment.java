package common.base.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import common.base.R;
import common.base.activitys.IProxyCallback;
import common.base.activitys.UIHintAgent;
import common.base.interfaces.ICommonUiHintActions;
import common.base.netAbout.BaseServerResult;
import common.base.netAbout.NetRequestLifeMarker;
import common.base.utils.CommonLog;

/**
 * User: fee(lifei@cloudtone.com.cn)
 * Date: 2016-05-16
 * Time: 15:21
 * DESC: 碎片的基类
 */
public abstract class BaseFragment extends Fragment implements
                                View.OnClickListener,
                                IProxyCallback,
                                DialogInterface.OnClickListener,
                                ICommonUiHintActions{
    protected final String TAG = getClass().getSimpleName();
    protected LayoutInflater mLayoutInflater;
    protected Context context;
    protected Context appContext;
    /**
     * 是否开启生命周期打印调试
     */
    protected boolean LIFE_DEBUG = false;
    protected String extraInfoInLifeDebug = "";
    protected UIHintAgent someUiHintAgent;
    protected NetRequestLifeMarker netRequestLifeMarker = new NetRequestLifeMarker();

    //生命周期方法

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onActivityCreated() savedInstanceState = " + savedInstanceState);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onAttach() ");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onCreate() savedInstanceState = " + savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onStart() ");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onResume() ");
        }
        if (someUiHintAgent != null) {
            someUiHintAgent.setOwnerVisibility(true);
        }
        if (needUpdateData) {
            initData();
            needUpdateData = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onPause() ");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onStop() ");
        }
        if (someUiHintAgent != null) {
            someUiHintAgent.setOwnerVisibility(false);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onDetach() ");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onDestroyView() ");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onHiddenChanged() hidden = " + hidden);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onDestroy() ");
        }
    }
    private View rootView;
    private boolean needReDrawUi = true;
    protected boolean needUpdateData = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onCreateView() container id = " + container.getId() +" savedInstanceState = " + savedInstanceState);
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onCreateView() rootView = " + rootView);
        }
        mLayoutInflater = inflater;
        context = getActivity();
        appContext = context.getApplicationContext();
        if (rootView == null) {
            needReDrawUi = true;
            int curFragmentViewResId = providedFragmentViewResId();
            if (curFragmentViewResId > 0) {
                rootView = inflater.inflate(curFragmentViewResId, null);
            }
            else{
                rootView = providedFragmentView();
            }
        }
        else{
            needReDrawUi = false;
        }

        if (rootView == null) {
            needReDrawUi = false;
            return null;
        }
        ViewGroup rootViewOldParentView = (ViewGroup) rootView.getParent();
        if (rootViewOldParentView != null) {
            rootViewOldParentView.removeView(rootView);
        }
        return rootView;
    }

    /**
     * 一般在这个生命周期方法中初始化视图
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (needReDrawUi) {
            initViews(view);
            initData();
            initEvent();
        }
    }
    /**
     * 对所加载的视图进行初始化工作
     * @param contentView
     */
    protected abstract void initViews(View contentView);

    /**
     * 初始化数据
     */
    protected abstract void initData();
    protected void initEvent() {

    }

    protected void fixPageHeaderView(View headerView) {

    }
    /**
     * 提供当前碎片的内容视图布局的资源ID
     * @return
     */
    protected abstract int providedFragmentViewResId();

    /**
     * 提供当前碎片的内容视图View
     * @return
     */
    protected abstract View providedFragmentView();
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onActivityResult() requestCode = " + requestCode + " resultCode = " + resultCode + " data = " + data);
        }
    }
    protected void addRequestMark(int requestDataType,byte requestLifeState){
        if(netRequestLifeMarker != null){
            netRequestLifeMarker.addRequestToMark(requestDataType, requestLifeState);
        }
    }

    protected void needSomeUiHintAgent() {
        if (someUiHintAgent == null) {
            someUiHintAgent = new UIHintAgent(context);
            someUiHintAgent.setProxyCallback(this);
            someUiHintAgent.setHintDialogOnClickListener(this);
        }
    }
    protected void switchActivity(boolean finishSelf) {
        if (finishSelf) {
            getActivity().overridePendingTransition(R.anim.common_part_left_in, R.anim.common_whole_right_out);
        } else {
            getActivity().overridePendingTransition(R.anim.common_whole_right_in, R.anim.common_part_right_out);
        }
    }
    /**
     * 被代理者(雇佣代理者)是否处理服务正常响应结果,如果被代理者处理了,则代理者不处理
     *
     * @param requestDataType 处理的当前是何种请求
     * @param result
     * @return true:[被代理者]处理了 false:交由[代理者]处理
     */
    @Override
    public boolean ownerDealWithServerResult(int requestDataType, BaseServerResult result) {
        return false;
    }

    /**
     * 被代理者(雇佣代理者)是否处理连接服务端异常结果,如果被代理者处理了,则代理者不处理
     *
     * @param requestDataType 处理的当前是何种请求
     * @param errorInfo
     * @return true:[被代理者]处理了 false:交由[代理者]处理
     */
    @Override
    public boolean ownerDealWithServerError(int requestDataType, String errorInfo) {
        return false;
    }

    /**
     * 被代理者(宿主)想主动取消(网络)数据的请求,在各自实现中实现各网络请求的取消并标志好该请求已取消
     */
    @Override
    public void ownerToCanceleRequest() {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * This method will be invoked when a button in the dialog is clicked.
     *
     * @param dialog The dialog that received the click.
     * @param which  The button that was clicked (e.g.
     *               {@link DialogInterface#BUTTON1}) or the position
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    //一些工具性质的方法
    /**
     * 从xml文件中找到一个Viwe控件的通配方法，将使用方需要的强制转换通用实现
     * @param viewId
     * @param <T> 控件类型
     * @return T类型的视图控件
     */
    protected <T extends View> T findAviewById(int viewId) {
        if (viewId > 0) {
            return (T) getActivity().findViewById(viewId);
        }
        return null;
    }

    /**
     * 在一个容器视图中依据View ID查找子视图
     * @param containerView 容器View
     * @param childViewId 子View ID
     * @param <T>
     * @return
     */
    protected <T extends View> T findAviewInContainer(ViewGroup containerView, int childViewId) {
        if (containerView == null || childViewId <= 0) {
            return null;
        }
        return (T) containerView.findViewById(childViewId);
    }
    protected void jumpToActivity(Intent startIntent, int requestCode,boolean needReturnResult) {
        if (startIntent != null) {
            if (!needReturnResult) {
                startActivity(startIntent);
            }
            else{
                startActivityForResult(startIntent,requestCode);
            }
        }
    }
    protected void jumpToActivity(Class<?> targetActivityClass) {
        Intent startIntent = new Intent(context, targetActivityClass);
        jumpToActivity(startIntent,0,false);
    }

    protected void jumpToActivity(Class<?> targetActiviyClass, int requestCode, boolean needReturnResult) {
        Intent startIntent = new Intent(context,targetActiviyClass);
        jumpToActivity(startIntent,requestCode,needReturnResult);
    }

    /**
     * 当前的请求是否取消了
     * @param dataType
     * @return
     */
    protected boolean curRequestCanceled(int dataType) {
        if(netRequestLifeMarker != null){
            return netRequestLifeMarker.curRequestCanceled(dataType);
        }
        return false;
    }
    /**
     * 标记当前网络请求的状态 : 正在请求、已完成、已取消等
     * @see {@link NetRequestLifeMarker#REQUEST_STATE_ING}
     * @param requestDataType
     * @param targetState
     */
    protected void addRequestStateMark(int requestDataType,byte targetState){
        if(netRequestLifeMarker != null){
            netRequestLifeMarker.addRequestToMark(requestDataType, targetState);
        }
    }
    /**
     * 注意：该提示性PopupWindow适用与在一个界面的顶部经由上至下的动画弹出
     *
     * @param anchorView 一般为顶部的一个控件
     * @param xOffset    X方向的偏移量
     * @param yOffset    Y方向的偏移量
     */
    @Override
    public void initCommonHintPopuWindow(View anchorView, int xOffset, int yOffset) {
        if (someUiHintAgent != null) {
            someUiHintAgent.initHintPopuWindow(anchorView,xOffset,yOffset);
        }
    }

    @Override
    public void showCommonLoading(String hintMsg) {
        if (someUiHintAgent != null) {
            someUiHintAgent.showLoading(hintMsg);
        }
    }

    @Override
    public void showCommonLoading(int hintMsgResID) {
        showCommonLoading(getString(hintMsgResID));
    }

    @Override
    public void dialogHint(String dialogTitle, String hintMsg, String cancelBtnName, String sureBtnName, int dialogInCase) {
        if (someUiHintAgent != null) {
            someUiHintAgent.dialogHint(dialogTitle, hintMsg, cancelBtnName, sureBtnName, dialogInCase);
        }
    }

    @Override
    public void dialogHint(int titleResID, int hintMsgResID, int cancelBtnNameResID, int sureBtnNameResID, int dialogInCase) {
        String dialogTitle = getString(titleResID);
        String hintMsg = getString(hintMsgResID);
        String cancelBtnName = getString(cancelBtnNameResID);
        String sureBtnName = getString(sureBtnNameResID);
        dialogHint(dialogTitle, hintMsg, cancelBtnName, sureBtnName, dialogInCase);
    }

    @Override
    public void popupHint(String hintMsg) {
        if(someUiHintAgent != null)
        someUiHintAgent.popupHint(hintMsg);
    }

    @Override
    public void popupHint(int hintMsgResID) {
        popupHint(getString(hintMsgResID));
    }

    protected void i(String tag,Object... logBodys) {
        if (tag == null) {
            tag = TAG + "[" + extraInfoInLifeDebug + "]";
        }
        CommonLog.i(tag, logBodys);
    }

    protected void e(String tag, Object... logBodys) {
        if (tag == null) {
            tag = TAG + "[" + extraInfoInLifeDebug + "]";
        }
        CommonLog.e(tag, logBodys);
    }
}
