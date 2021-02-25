package common.base.mvx.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import common.base.R;
import common.base.WeakHandler;
import common.base.activitys.BaseActivity;
import common.base.activitys.UIHintAgent;
import common.base.dialogs.BaseDialog;
import common.base.interfaces.ICommonUiHintActions;
import common.base.utils.CommonLog;

/**
 * User: fee(lifei@cloudtone.com.cn)
 * Date: 2016-05-16
 * Time: 15:21
 * DESC: 碎片的基类
 * <P>生命周期：
 * <P>显示线:
 * {@link #onAttach(Context)}--> {@link #onAttach(Activity)}--> {@link #onCreate(Bundle)}
 * --> {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} -->{@link #onViewCreated(View, Bundle)}
 * --> {@link #onActivityCreated(Bundle)} --> {@link #onStart()} --> {@link #onResume()}</P>
 * <P>
 *     隐藏线：
 *     {@link #onPause()} -->{@link #onStop()}--> {@link #onDestroyView()} -->{@link #onDestroy()}-->{@link #onDetach()}
 *
 *     <P>其中 {@link #onDestroyView() } 可恢复至 {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} </P>
 * </P>
 * </P>
 */
public abstract class BaseFragment extends Fragment implements
                                View.OnClickListener,
                                ICommonUiHintActions,
                                WeakHandler.Handleable {
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
    /**
     * 是否让dialog关心当前界面的可见性
     * def: false
     */
    protected boolean isLetDialogCareAboutVisible = false;
    //生命周期方法

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onActivityCreated() savedInstanceState = " + savedInstanceState);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onViewStateRestored() savedInstanceState = " + savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onSaveInstanceState() outState = " + outState);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onConfigurationChanged() newConfig = " + newConfig);
        }
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        appContext = context.getApplicationContext();
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug + "]", "--> onAttach() context = " + context);
        }
        super.onAttach(context);
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

    /**
     * 非原生生命周期方法，
     * Activity中可以主动调用一下
     */
    public void onReStart() {
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onReStart() ");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onResume() ");
        }
        if (someUiHintAgent != null && isLetDialogCareAboutVisible) {
            someUiHintAgent.setOwnerVisibility(isVisible());
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
        if (someUiHintAgent != null && isLetDialogCareAboutVisible) {
            someUiHintAgent.setOwnerVisibility(false);
        }
    }

    @Override
    public void onDetach() {
        if(someUiHintAgent != null){
            someUiHintAgent.finishAgentFollowUi();
        }
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
    protected View rootView;
    private boolean needReDrawUi = true;
    protected boolean needUpdateData = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (LIFE_DEBUG) {
            String containerId = "";
            String containerTag = "";
            if (container != null) {//增加下面的输出，是为了方便使用者查看(debug)当前Fragment的容器View是哪一个
                containerId = container.getId() + "";
                containerTag = container.getTag() + "";
            }
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug + "]", "--> onCreateView() container id = " + containerId + " savedInstanceState = " + savedInstanceState
                    + " containerTag = " + containerTag
            );
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onCreateView() rootView = " + rootView);
        }
        mLayoutInflater = inflater;
        if (context == null) {
            context = getActivity();
            if (context != null) {
                appContext = context.getApplicationContext();
            }
        }
        if (rootView == null) {
            needReDrawUi = true;
            int curFragmentViewResId = providedFragmentViewResId();
            if (curFragmentViewResId > 0) {
                rootView = inflater.inflate(curFragmentViewResId, null);
            }
            else{
                rootView = providedFragmentView(container,savedInstanceState);
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
    protected abstract View providedFragmentView(ViewGroup container,Bundle savedInstanceState);
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LIFE_DEBUG) {
            CommonLog.i(TAG + "[" + extraInfoInLifeDebug +"]","--> onActivityResult() requestCode = " + requestCode + " resultCode = " + resultCode + " data = " + data);
        }
    }

    protected void needSomeUiHintAgent() {
        if (someUiHintAgent == null) {
            someUiHintAgent = new UIHintAgent(context);
//            someUiHintAgent.setHintDialogOnClickListener(this);
            someUiHintAgent.setExistHintDialog(provideExtraHintDialog());
        }
    }

    protected void switchActivity(boolean finishSelf) {
        if (getActivity() != null) {
            if (finishSelf) {
                getActivity().overridePendingTransition(R.anim.common_part_left_in, R.anim.common_whole_right_out);
            } else {
                getActivity().overridePendingTransition(R.anim.common_whole_right_in, R.anim.common_part_right_out);
            }
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    //一些工具性质的方法
    /**
     * 从xml文件中找到一个Viwe控件的通配方法，将使用方需要的强制转换通用实现
     * @param viewId
     * @param <T> 控件类型
     * @return T类型的视图控件
     */
    protected <T extends View> T findAviewById(int viewId) {

        return findLocalViewById(viewId);//changed by fee 2017-09-13 一个Fragment在宿主Activity中来查找子视图控件不太合适
    }

    /**
     * 在宿主Activity中根据view id 查找控件
     *
     * @param viewId
     * @param <T>
     * @return
     */
    protected <T extends View> T findAviewByIdInHostActivity(int viewId) {
        Activity hostActivity = getActivity();
        if (hostActivity != null) {
            return hostActivity.findViewById(viewId);
        }
        return null;
    }
    /**
     * 在Fragment所加载的视图里查找视图控件
     * @param viewId
     * @param <T>
     * @return
     */
    protected <T extends View> T findLocalViewById(int viewId) {
        if (viewId > 0 && rootView != null) {
            return rootView.findViewById(viewId);
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
            FragmentActivity activity = getActivity();
            if (activity == null || activity.isFinishing()) {
                //android.view.WindowManager$BadTokenException
                //Unable to add window -- token android.os.BinderProxy@89012cd is not valid; is your activity running?
                Log.w(TAG,"dialogHint -> ignore the dialog hint event, because of activity is finishing.");
                return;
            }
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


    protected WeakHandler mHandler;
    /**
     * 各子类按需决定是否需要Handler
     */
    protected void initHandler() {
        if (mHandler == null) {
            mHandler = new WeakHandler<>(this);
        }
    }

    /**
     * 处理消息
     *
     * @param msg
     * @return 被处理了的MessageId
     */
    @Override
    public int handleMessage(Message msg) {
        return 0;
    }

    protected void i(String tag, Object... logBodys) {
        if (tag == null) {
            tag = TAG + "[" + extraInfoInLifeDebug + "]";
        }
        CommonLog.iFullLog(tag, logBodys);
    }

    protected void v(String tag, Object... logBodys) {
        if (tag == null) {
            tag = TAG + "[" + extraInfoInLifeDebug + "]";
        }
        CommonLog.fullLog('v', tag, CommonLog.getInfo(logBodys));
    }
    protected void e(String tag, Object... logBodys) {
        if (tag == null) {
            tag = TAG + "[" + extraInfoInLifeDebug + "]";
        }
        CommonLog.e(tag, logBodys);
    }

    /**
     * 本Fragment是否需要处理返回键事件
     * 如果有需要的话外部Activity直接调用该方法来处理
     * def false
     * @return true:需要处理；false:不需要处理
     */
    public boolean onHandleBackPressed(){
        return false;
    }

    protected void letHostActivityFinish() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    protected void sendBroadcast(Intent actionIntent) {
        if (getActivity() != null) {
            getActivity().sendBroadcast(actionIntent);
        }
    }

    protected void finishHost(boolean needTransAnim) {
        if (mFragmentHost != null) {
            mFragmentHost.finishHost(needTransAnim);
        }
    }

    protected void onFragmentShow() {
        if (mFragmentHost != null) {
            mFragmentHost.onFragmentShow(this);
        }
    }
    protected IFragmentHost mFragmentHost;

    /**
     * 有一些场景，Fragment可能需要请求宿主Activity做某些动作
     * @param optType 操作类型
     */
    protected boolean onFragmentReqHostOpt(String optType,Object reqData) {
        boolean isRespOpt = false;
        if (mFragmentHost != null) {
            isRespOpt = mFragmentHost.onFragmentOptReq(this,optType);
        }
        else {
            Activity hostActivity = getActivity();
            if (hostActivity != null) {
                if (hostActivity instanceof BaseActivity) {
                    isRespOpt = ((BaseActivity) hostActivity).onFragmentOptReq(this,optType,reqData);
                }
            }
        }
        return isRespOpt;
    }

    protected boolean onFragmentReqHostOpt(String optType) {
       return onFragmentReqHostOpt(optType, null);
    }

    /**
     * 定义一个当前Fragment宿主的接口
     */
    public interface IFragmentHost{
        void onFragmentShow(BaseFragment curFragment);

        void finishHost(boolean needTransAnim);

        boolean onFragmentOptReq(Fragment curFragment, String optTypeInFragment);
    }

    /**
     * 将dimen资源id,转换为系统中的px值
     * @param dimenResId 定义的dimen资源 ID
     * @return 转化成的像素值
     */
    protected int dimenResPxValue(@DimenRes int dimenResId) {
        return getResources().getDimensionPixelSize(dimenResId);
    }

    /**
     * 当本Fragment作为父Fragment时，处理来自子Fragment的操作请求
     * @param theSubFragment 当前请求的子Fragment
     * @param optTypeInSubFragment 请求数据 协议自定义
     * @return 返回处理的结果，协议自定义
     */
    protected String onSubFragmentOptReq(Fragment theSubFragment, String optTypeInSubFragment) {
        //这里可以让 当 Parent 的Fragment来实现 处理来自子Fragment的操作请求
        return null;
    }

    /**
     * 当本类作为子Fragment时(绝大部分场景是这样的)，并且身处父Fragment中时，可以调用该方法向父Fragment
     * 执行操作请求
     * @param optTypeInSubFragment 请求操作数据，协议自定义
     * @return 父Fragment响应操作的结果，协议自定义
     */
    protected String subFragmentOptReq(String optTypeInSubFragment) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            if (parentFragment instanceof BaseFragment) {
                return ((BaseFragment) parentFragment).onSubFragmentOptReq(this, optTypeInSubFragment);
            }
        }
        return null;
    }

    /**
     * 子类可以提供一个可以替换掉 {{@link UIHintAgent}} 内部的 提示的Dialog
     * @return 项目内自己的提示Dialog
     */
    protected BaseDialog provideExtraHintDialog() {
        return null;
    }

}
