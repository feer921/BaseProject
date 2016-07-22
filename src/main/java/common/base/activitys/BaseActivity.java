package common.base.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import common.base.R;
import common.base.netAbout.BaseServerResult;
import common.base.netAbout.NetRequestLifeMarker;
import common.base.utils.CommonLog;
import common.base.views.ToastUtil;

/**
 * User: fee(lifei@cloudtone.com.cn)
 * Date: 2016-05-16
 * Time: 15:21
 * DESC: 通用Activity基类
 */
public abstract class BaseActivity extends AppCompatActivity implements
                                                             View.OnClickListener,
                                                             IProxyCallback,
                                                             DialogInterface.OnClickListener{
    protected final String TAG = getClass().getSimpleName();
    protected static boolean LIFE_CIRCLE_DEBUG = false;
    /**
     * 一些简单交互类的通用UI；如：Loading类的Dialog；提示性带取消、确定按钮的Dialog
     * 以及一个提示性的PopupWindow，由于一般在进行耗时操作的时候都会有Loading类的交互UI展示，所以当开发的APP有在Loading状态时即允许用户取消请求的话，则该UI代理者
     * 需要设置代理回调接口即IProxyCallback来回调给Activity提示UI交互时请求被取消了,然后作对应的处理
     */
    protected UIHintAgent uiHintAgent;
    protected Context appContext;
    protected Context mContext;
    protected NetRequestLifeMarker netRequestLifeMarker = new NetRequestLifeMarker();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onCreate()");
        }
        appContext = getApplicationContext();
        ToastUtil.setContext(appContext);
        mContext = this;
        uiHintAgent = new UIHintAgent(mContext);
        uiHintAgent.setHintDialogOnClickListener(this);
        uiHintAgent.setProxyCallback(this);
        boolean needInitAuto = false;
        int subActivityContentViewResID = getProvideContentViewResID();
        if (subActivityContentViewResID > 0) {//子类有提供当前Activity的内容视图，则父类来调用初始化方法
            setContentView(subActivityContentViewResID);
            needInitAuto = true;
        }
        else{
            View providedContentView = providedContentView();
            if(providedContentView != null){
                setContentView(providedContentView);
                needInitAuto = true;
            }
        }
        if (needInitAuto) {
            initViews();
            initData();
        }
    }

    /**
     * 提供的内容视图
     * @return
     */
    protected View providedContentView() {
        return null;
    }
    /**
     * 获取当前Activity需要填充、展示的内容视图，如果各子类提供，则由基类来填充，如果不提供，各子类也可自行处理
     * @return 当前Activity需要展示的内容视图资源ID
     */
    protected abstract int getProvideContentViewResID();

    /**
     * 如果子类在getProvideContentViewResID()方法提供了视图资源，那么子类的初始化视图可在此方法中完成
     */
    protected abstract void initViews();

    /**
     * 初始化数据
     */
    protected void initData() {

    }
    protected StaticHandler mHandler;

    /**
     * 各子类按需决定是否需要Handler
     */
    protected void initHandler() {
        if (mHandler == null) {
            mHandler = new StaticHandler(this);
        }
    }

    /**
     * 避免内存泄漏的Handler以供各子类需要使用Handler时调用initHandler()方法进行初始化并重载handlerMessage()方法对发送的消息进行处理
     */
    protected static class StaticHandler extends WeakHandler{
        public StaticHandler(Activity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseActivity activity = (BaseActivity) getOwner();
            if (activity != null) {
                activity.handlerMessage(msg);
            }
        }
    }
    /**
     * 鉴于可能许多Activity中需要处理Handler发送的消息，则可使用{@link #mHandler}来发送
     * 在发送之前先调用initHandler()方法进行初始化
     * @param msg
     */
    protected void handlerMessage(Message msg) {

    }

    /**
     * 被代理者(宿主)想主动取消(网络)数据的请求,在各自实现中实现各网络请求的取消并标志好该请求已取消
     */
    @Override
    public void ownerToCanceleRequest() {

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
     * Activity之间的切换转场动画，本基类使用普通动画，各子类可自行实现
     * @param finishSelf
     */
    protected void switchActivity(boolean finishSelf) {
        if (finishSelf) {
            //被关闭的本Activity 往右方向整个渐出,返回到前台的Activity 从左方向短距离(部分的)的进来
            overridePendingTransition(R.anim.common_part_left_in, R.anim.common_whole_right_out);
        } else {
            //要启动的Activity完整的从右侧进来，而运行至后台的本Activity 部分地、短距离的从右侧类似的出
            overridePendingTransition(R.anim.common_whole_right_in, R.anim.common_part_right_out);
        }
    }
    /**
     * 从xml文件中找到一个Viwe控件的通配方法，将使用方需要的强制转换通用实现
     * @param viewId
     * @param <T> 控件类型
     * @return T类型的视图控件
     */
    protected <T extends View> T findAviewById(int viewId) {
        if (viewId > 0) {
            return (T) findViewById(viewId);
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
        Intent startIntent = new Intent(mContext, targetActivityClass);
        jumpToActivity(startIntent,0,false);
    }

    protected void jumpToActivity(Class<?> targetActiviyClass, int requestCode, boolean needReturnResult) {
        Intent startIntent = new Intent(mContext,targetActiviyClass);
        jumpToActivity(startIntent,requestCode,needReturnResult);
    }
    protected boolean curRequestCanceled(int dataType) {
        if(netRequestLifeMarker != null){
            return netRequestLifeMarker.curRequestCanceled(dataType);
        }
        return false;
    }

    /***
     * 取消网络请求
     * @param curRequestType
     */
    protected void cancelNetRequest(int curRequestType) {
        if (netRequestLifeMarker != null) {
            netRequestLifeMarker.cancelCallRequest(curRequestType);
        }
    }

    /**
     * 查询当前网络请求的状态
     * @param curRequestType
     * @return
     */
    protected byte curNetRequestState(int curRequestType) {
        if (netRequestLifeMarker != null) {
            return netRequestLifeMarker.curRequestLifeState(curRequestType);
        }
        return NetRequestLifeMarker.REQUEST_STATE_NON;
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
     * @param anchorView 一般为顶部的一个控件
     * @param xOffset X方向的偏移量
     * @param yOffset Y方向的偏移量
     */
    protected void initCommonHintPopuWindow(View anchorView,int xOffset,int yOffset){
        uiHintAgent.initHintPopuWindow(anchorView, xOffset, yOffset);
    }
    protected void showCommonLoading(String hintMsg) {
        uiHintAgent.showLoading(hintMsg);
    }
    protected void showCommonLoading(int hintMsgResID){
        showCommonLoading(getString(hintMsgResID));
    }
    protected void dialogHint(String dialogTitle,String hintMsg,String cancelBtnName,String sureBtnName,int dialogInCase){
        uiHintAgent.dialogHint(dialogTitle, hintMsg, cancelBtnName, sureBtnName, dialogInCase);
    }
    protected void dialogHint(int titleResID,int hintMsgResID,int cancelBtnNameResID,int sureBtnNameResID,int dialogInCase){
        String dialogTitle = getString(titleResID);
        String hintMsg = getString(hintMsgResID);
        String cancelBtnName = getString(cancelBtnNameResID);
        String sureBtnName = getString(sureBtnNameResID);
        dialogHint(dialogTitle, hintMsg, cancelBtnName, sureBtnName, dialogInCase);
    }
    protected void popupHint(String hintMsg){
        uiHintAgent.popupHint(hintMsg);
    }
    protected void popupHint(int hintMsgResID){
        popupHint(getString(hintMsgResID));
    }

    protected void topToast(String msg) {
        ToastUtil.topShow(msg);
    }

    protected void topToast(int msgResId) {
        topToast(getString(msgResId));
    }
    //------------------------- 生命周期方法----------(我是不漂亮的分隔线)------------------
    @Override
    protected void onRestart() {
        super.onRestart();
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onRestart()");
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onStart()");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onResume()");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG,"--> onNewIntent() intent = " + intent);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onRestoreInstanceState()");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onPause()");
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onStop()");
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onSaveInstanceState()");
        }
    }

    /**
     * 结束/finish()自已
     * @param needTransitionAnim 是否需要过场动画
     */
    protected void finishSelf(boolean needTransitionAnim) {
        finish();
        if (needTransitionAnim) {
            switchActivity(true);
        }
    }
    @Override
    public void finish() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if(uiHintAgent != null){
            uiHintAgent.finishAgentFollowUi();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onDestroy()");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onActivityResult() requestCode = " + requestCode +" resultCode = " + resultCode + " data = " + data);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onBackPressed()");
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(LIFE_CIRCLE_DEBUG){
            CommonLog.i(TAG,"---> onConfigurationChanged()");
        }
    }
    //---------------------up up up 生命周期方法 up up up ----------(我是不漂亮的分隔线)---------------
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
    public final void onClick(DialogInterface dialog, int which){
        onClickInDialog(dialog,which);
    }
    /**
     * 提示用的dialog 处理“确定"按钮的点击事件，"取消“按钮已经在Dialog类中统一为消失Dialog处理
     * 如果各子类对“确定”,“取消”按钮有不一样的处理，则应实现该方法并做相应逻辑
     * @param dialog
     * @param which 按钮类型 eg:{@link DialogInterface#BUTTON_POSITIVE}
     */
    protected void onClickInDialog(DialogInterface dialog, int which) {
        if (dialog == uiHintAgent.getCommonHintDialog()) {
            uiHintAgent.onClickInDialog(dialog,which);
        }
    }

    /**
     * 基类提供普通Log输出之error级信息输出
     * 注意一点：因为第二个参数是可变参数，该方法允许只传一个参数eg.: e("")
     * @param logTag log的TAG，如果为null,会使用{@link #TAG}
     * @param logBody
     */
    protected void e(String logTag, Object... logBody) {
        CommonLog.e(null == logTag ? TAG : logTag, logBody);
    }

    /**
     * 基类提供普通Log输出之info级信息输出
     * 注意一点：因为第二个参数是可变参数，该方法允许只传一个参数eg.: e("")
     * @param logTag log的TAG，如果为null,会使用{@link #TAG}
     * @param logBody Log内的具体要打印的信息
     */
    protected void i(String logTag,Object... logBody) {
        CommonLog.i(null == logTag ? TAG : logTag,logBody);
    }
}
