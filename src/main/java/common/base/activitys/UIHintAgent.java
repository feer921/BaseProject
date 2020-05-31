package common.base.activitys;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.View;

import common.base.R;
import common.base.dialogs.BaseDialog;
import common.base.dialogs.SimpleHintDialog;
import common.base.netAbout.BaseServerResult;
import common.base.netAbout.INetEvent;
import common.base.utils.NetHelper;
import common.base.views.HintPopuWindow;


/**
 * UI交互过程一些提醒类型的交互代理者
 * 里面的一些通用交互控件都可以自定义再对应的替换
 * 2015年9月7日-下午7:49:21
 * @author lifei
 */
public class UIHintAgent {
//    private CommonMdLoadialog loadingDialog;
    private BaseDialog hintDialog;
    private Context mContext;
    private HintPopuWindow hintPopuWindow;
    /**
     * 对话框Dialog中的("确定“、”取消“按钮)的点击事件的监听者,供回调给外部
     */
    private DialogInterface.OnClickListener mClickListenerForDialog;
    private IProxyCallback  mProxyCallback;


    /**
     * 当前宿主(Activity)是否可见，一般不可见的情况(比如执行了onStop())不应该弹出提示性Dialog
     */
    private boolean isOwnerVisible = true;
    /**
     * 提示性对话框Dialog显示show时是否按back键可取消
     * 默认为可取消
     */
    private boolean isHintDialogCancelable = true;
    /**
     * 提示性对话框Dialog显示show时是否可点击外部取消
     * 默认为点击外部不可取消
     */
    private boolean isHintDialogCancelableOutSide = false;
    /**
     * 是否需要监听提示性Dialog对话框的被取消显示(dismiss)事件
     */
    private boolean isNeedListenHintDialogCancel = false;
    private Handler mHandler;
    /**
     * 提示加载对话框是否可按back键取消 默认为不可取消
     */
    private boolean isLoadingDialogCancelable = false;

    /**
     * 设置提示性的Dialog的背景的透明度
     * add by fee 2017-10-21
     */
    private float hintDialogBgAlpha = -1;

    /**
     * 可额外配置 提示 Dialog的布局 的方案
     * 注：该布局内的各控件ID需要和 {@link #hintDialog}原配布局中View的 ID一致
     */
    private @LayoutRes int extraHintDialogLayoutRes;
    private boolean isDialogCompatPadUi;
    public void setProxyCallback(IProxyCallback curProxyOwner){
        mProxyCallback = curProxyOwner;
    }

    public UIHintAgent(Context curContext) {
        this.mContext = curContext;
    }

    private void initHintDialog() {
        if (hintDialog == null) {
//            hintDialog = new CommonMdDialog(mContext);
            if (extraHintDialogLayoutRes != 0) {
                hintDialog = new SimpleHintDialog(mContext){
                    @Override
                    protected int getDialogViewResID() {
                        return extraHintDialogLayoutRes;
                    }
                };
            }
            else {
                hintDialog = new SimpleHintDialog(mContext);
                isDialogCompatPadUi = false;
            }
            extraConfigHintDialog(isDialogCompatPadUi);
            //added
            hintDialog.setDialogBgBehindAlpha(hintDialogBgAlpha);
            hintDialog.edtViewCanEdit(false);
            hintDialog.setCancelable(isHintDialogCancelable);
            hintDialog.setCanceledOnTouchOutside(isHintDialogCancelableOutSide);
            if (mClickListenerForDialog != null) {
                hintDialog.setDialogClickListener(mClickListenerForDialog);
            } 
            else {
                hintDialog.setDialogClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onClickInDialog(dialog, which);
                    }
                });
            }
           setUpHintDialogCancelListenerInfo();
        }
    }

    /**
     * 提升至外部可调用
     * @param isCompatPadUi 是否 提示 Dialog要兼容 pad的显示布局
     */
    public void extraConfigHintDialog(boolean isCompatPadUi) {
        if (hintDialog != null && hintDialog instanceof SimpleHintDialog) {
            SimpleHintDialog simpleHintDialog = (SimpleHintDialog) hintDialog;
            simpleHintDialog.defConfigDialogViews(isCompatPadUi);
        }
    }
    public void setHintDialogOnClickListener(DialogInterface.OnClickListener l) {
        mClickListenerForDialog = l;
        if (hintDialog != null) {
            hintDialog.setDialogClickListener(mClickListenerForDialog);
        }
    }

    /**
     * 是否需要监听本类中hintDialog被用户取消展示的动作，一般来讲是不需要监听提示性的Dialog的被取消的动作的。
     *
     * 但由用户选定一个数据体并弹出需要对数据体进行某种操作而弹出提示性Dialog时，一般程序会使用临时变量存放该数据体，eg.:Object temp2OptObj;
     * 而当用户在所弹出的提示性Dialog中取消了对所选定的数据体的操作时，应该将临时存放的数据体给置空(因为用户取消了该数据体的操作，所以程序应该也取消对该数据体的引用，不然
     * 可能会造成一些bug，当然如果其他程序块中不会对上次所选定的数据体进行操作，也不会出什么问题)，上面这种场景下，则有一定必要来监听用户的取消操作
     * so:此处提供该功能，设置了监听后，请在当前界面中的实现{@linkplain IProxyCallback#ownerToCancelHintDialog()}中处理.
     * @param isNeed true:需要; false:不需要
     */
    public void needListenHintDialogCancelCase(boolean isNeed) {
        isNeedListenHintDialogCancel = isNeed;
        setUpHintDialogCancelListenerInfo();
    }

    public void setHintDialogBgAlpha(float targetAlpha) {
        this.hintDialogBgAlpha = targetAlpha;
    }
    /**
     * 开关 : 提示用Dialog 是否可按back键取消
     * @param cancelable
     */
    public void toggleHintDialogCancelable(boolean cancelable){
        isHintDialogCancelable = cancelable;
        if(hintDialog != null){
            hintDialog.setCancelable(cancelable);
        }
//        if (sweetAlertDialog != null) {
//            sweetAlertDialog.setCancelable(cancelable);
//        }
        setUpHintDialogCancelListenerInfo();
    }

    /**
     * 开关/触发：提示用Dialog是否可点击自身的外围空间而取消显示(dismiss)
     * @param hintDialogCancelable
     */
    public void toggleHintDialogCancelableOutSide(boolean hintDialogCancelable) {
        isHintDialogCancelableOutSide = hintDialogCancelable;
        if (hintDialog != null) {
            hintDialog.setCanceledOnTouchOutside(hintDialogCancelable);
        }
//        //added 2016-10-31
//        if (sweetAlertDialog != null) {
//            sweetAlertDialog.setCanceledOnTouchOutside(hintDialogCancelable);
//        }
    }
    /**
     * 开关 : 加载对话框 是否可按back键取消
     * @param cancelable
     */
    public void toggleLoadingDialogCancelable(boolean cancelable){
        isLoadingDialogCancelable = cancelable;
//        if(loadingDialog != null){//之所以需要主动再调用一次，是因为，如果使用者先调用#showLoading()时loadDialog已经设置了是否可取消显示为loadingDialogCancelable的默认值
//            //而中间想改变是否可取消的值时，如果不主动调用一次，则会无效,故凡是临时改变Dialog的是否可取消的状态值时都需要主动再调用一次
//            loadingDialog.setCancelable(cancelable);
//        }
//        //added 2016-10-31
//        if (null != sweetLoadingDialog) {
//            sweetLoadingDialog.setCancelable(cancelable);
//        }
        setUpLoadingDialogCancelListenerInfo();
    }
    public void onClickInDialog(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
           //本来一些通用的点击提示对话框的肯定性按钮时 的通用处理，比如：对话框提示了未登陆，点击确定，本意可以统一在这处理，但由于框架不知道对话框所处的显示情景，所以不能在此处理了
            //可以交给各APP的统一基类来处理
        }
        dialog.dismiss();
    }

    /**
     * Activity界面之间的切换效果
     * @param finishSelf
     */
    protected void switchActivity(boolean finishSelf) {
        if(mContext instanceof Activity){
            if (finishSelf) {
                ((Activity) mContext).overridePendingTransition(R.anim.common_part_left_in, R.anim.common_whole_right_out);
            } else {
                ((Activity) mContext).overridePendingTransition(R.anim.common_whole_right_in, R.anim.common_part_right_out);
            }
        }
    }

    /**
     * 对话框提示
     * @param title
     * @param hintMsg
     * @param cancleBtnName
     * @param sureBtnName
     * @param dialogInCase
     */
    public void dialogHint(String title, String hintMsg, String cancleBtnName, String sureBtnName, int dialogInCase) {
        dialogHint(title, hintMsg, Gravity.CENTER_HORIZONTAL, cancleBtnName, sureBtnName, dialogInCase);
    }
    public void dialogHint(String title,CharSequence hintMsg,int hintMsgGravity,String cancleBtnName, String sureBtnName, int dialogInCase) {
        if (!isOwnerVisible) {
            return;
        }
//        SimpleHintDialog hintDialog;
        if (hintDialog == null) {
            initHintDialog();
        }
        hintDialog.setTitle(title);
        hintDialog.setHintMsg(hintMsg);
        hintDialog.setHintMsgGravity(hintMsgGravity);
        hintDialog.curDialogInCase = dialogInCase;
        hintDialogInWhichCase = dialogInCase;
        hintDialog.setCancleBtnName(cancleBtnName);
        hintDialog.setCommitBtnName(sureBtnName);
        hintDialog.show();
    }
    /**
     * 显示加载loading...对话框
     * @param hintMsg
     */
    @Deprecated
    public void showLoading(String hintMsg) {
//        if (loadingDialog == null) {
//            loadingDialog = new CommonMdLoadialog(mContext);
//            loadingDialog.setCanceledOnTouchOutside(false);
//            loadingDialog.setCancelable(isLoadingDialogCancelable);
//            setUpLoadingDialogCancelListenerInfo();
//        }
//        loadingDialog.setHintMsg(hintMsg);
//        if (!loadingDialog.isShowing()) {
//            loadingDialog.show();
//        }
    }

    /**
     * 配置Loading类Dialog的被取消显示时的监听者信息
     */
    private void setUpLoadingDialogCancelListenerInfo() {
        if (isLoadingDialogCancelable) {//只有Loading类的Dialog在可取消(按back键)时才有意义去设置取消的监听事件
            if (loadingDialogCancelListener == null) {
                loadingDialogCancelListener = new LoadingDialogCancelListener();
            }
        }
//        if (loadingDialog != null) {
//            loadingDialog.setOnCancelListener(isLoadingDialogCancelable ? loadingDialogCancelListener : null);
//        }

//        if (sweetLoadingDialog != null) {
//            sweetLoadingDialog.setOnCancelListener(isLoadingDialogCancelable ? loadingDialogCancelListener : null);
//        }
    }

    /**
     * 配置提示类Dialog的被取消显示时的监听者信息
     */
    private void setUpHintDialogCancelListenerInfo() {
        boolean isNeedToConfigCancelListener = isNeedListenHintDialogCancel && isHintDialogCancelable;
        if (isNeedToConfigCancelListener) {
            if (hintDialogCancelListener == null) {
                hintDialogCancelListener = new HintDialogCancelListener();
            }
        }
        if (hintDialog != null) {
            hintDialog.setOnCancelListener(isNeedToConfigCancelListener ? hintDialogCancelListener : null);
        }

//        if (sweetAlertDialog != null) {
//            sweetAlertDialog.setOnCancelListener(isNeedToConfigCancelListener ? hintDialogCancelListener : null);
//        }
    }
    /**
     * @deprecated 目前loading dialog已经可以取消
     * @param hintMsg
     * @param timeOutMills
     */
    public void showLoadingAndTriggerTimer(String hintMsg,int timeOutMills){
        showLoading(hintMsg);
        if(timeOutMills > 0)
        triggerLoadingCaseTimer(timeOutMills, INetEvent.MANULLY_TIME_OUT);
    }
    public void loadDialogDismiss() {
//        if (loadingDialog != null) {
//            loadingDialog.dismiss();
//        }

//        //added by fee 2016-07-28
//        if (sweetLoadingDialog != null) {
//            sweetLoadingDialog.dismissWithAnimation();
//        }
    }

    public void dismissLoadingDialog() {
        loadDialogDismiss();
    }
    public boolean isLoadingDialogShowing(){
//        return loadingDialog != null && loadingDialog.isShowing();
        return false;
    }
    public void dealWithServerResult(int requestDataType,BaseServerResult result) {
        if (!result.isResponseOk()) {
            //本意为针对所有的网络请求，服务器返回请求不成功时的 各种原因的统一通用处理(弹出提示对话框)
            //但由于框架无法得知具体的APP的服务器返回请求不成功的原因，所以目前无法在此统一处理,可以交给各APP的统一基类来处理
//            if (loadingDialog != null) {
//                loadingDialog.dismiss();
//            }
        }
    }
    public void dealWithServerError(int requestDataType,String errorInfo) {
//        if (loadingDialog != null) {
//            loadingDialog.dismiss();
//        }
        if (!NetHelper.isNetworkConnected(mContext) && !INetEvent.MANULLY_DELAY_OVER.equals(errorInfo)) {
            dialogHint("提示", "当前网络无效,请设置", null, "去设置网络",
                    BaseServerResult.ERROR_CODE_NO_NET
                    );
        } 
        else {
            if(mProxyCallback != null && mProxyCallback.ownerDealWithServerError(requestDataType,errorInfo)){
                return;
            }
            popupHint("连接服务器失败,请检查网络或稍候重试");
        }
    }
    private String getString(int errorInfoResIdBaseCode) {
        return mContext.getResources().getString(errorInfoResIdBaseCode);
    }

    /**
     * 初始化提示用的PopupWindow控件,用于显示提示信息
     * @param anchorView
     * @param xOffset
     * @param yOffset
     */
    public void initHintPopuWindow(View anchorView, int xOffset, int yOffset) {
        if (hintPopuWindow == null) {
            hintPopuWindow = new HintPopuWindow(mContext);
        }
        hintPopuWindow.setAnchorView(anchorView, xOffset, yOffset);
    }

    public void popupHint(String hintMsg) {
        if(!isOwnerVisible) return;
        if (hintPopuWindow != null) {
            hintPopuWindow.hintMsg(hintMsg);
        }
    }
    public void setOwnerVisibility(boolean isVisible){
        this.isOwnerVisible = isVisible;
    }

    /**
     * 查询宿主是否可见状态
     * @return
     */
    public boolean isOwnerVisible() {
        return isOwnerVisible;
    }

    public void dismissHintDialog() {
        if (hintDialog != null && hintDialog.isShowing()) {
            hintDialog.dismiss();
        }
    }
    /**
     * 结束代理UI交互
     */
    public void finishAgentFollowUi() {
        if (hintDialog != null) {
            hintDialog.dismiss();
        }
//        if (loadingDialog != null) {
//            loadingDialog.dismiss();
//        }
        if (hintPopuWindow != null) {
            hintPopuWindow.dismiss();
        }
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;

//        if (sweetAlertDialog != null) {
//            sweetAlertDialog.dismiss();
//        }
//        if (sweetLoadingDialog != null) {
//            sweetLoadingDialog.dismiss();
//        }
    }

    public int getHintDialogInCase() {
        //modified 2016-10-31 现在统一返回该变量，不管是在sweetAlertDialog中还是在hintDialog中
        //hintDialogInWhichCase 该变量会在hintDialog以及sweetAlertDialog show时赋值当前的Case值
        return hintDialogInWhichCase;
    }

    public void compatSetHintDialogCase(int hintDialogInWhichCase) {
        this.hintDialogInWhichCase = hintDialogInWhichCase;
    }
    public Dialog getCommonHintDialog() {
        return this.hintDialog;
    }

    /**
     * added by fee 2017-07-20 : 可以外部设置HintDialog对象进来
     * @param theExistDialog
     */
    public void setExistHintDialog(BaseDialog theExistDialog) {
        if (theExistDialog != null) {
            this.hintDialog = theExistDialog;
            this.hintDialog.setCanceledOnTouchOut(isHintDialogCancelableOutSide)
                    .setDialogClickListener(mClickListenerForDialog)
                    .setCancelable(isHintDialogCancelable);
            setUpHintDialogCancelListenerInfo();
        }
    }
    public void popupWindowDismiss() {
        if (hintPopuWindow != null && hintPopuWindow.isShowing()) {
            hintPopuWindow.dismiss();
        }
    }
    /**
     * @deprecated 
     * @param timeOutMills
     * @param triggerReason 触发原因，或者说计时完成后是什么原因要进行处理,见dealWithServerError()
     */
    public void triggerLoadingCaseTimer(int timeOutMills,final String triggerReason){
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    dealWithServerError(0,triggerReason);
                }
            };
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(0, timeOutMills);
    }
    /**
     * @deprecated
     */
    public void cancelLoadingCaseTimer(){
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 提示性Dialog当前显示show时所处的哪种(提示性)情况
     */
    private int hintDialogInWhichCase = 0;


    /**
     * Loading类的Dialog被取消显示(在设置了可被取消,一般按back键)时的回调监听
     * 注：主动调用dialog.dismiss()是不会触发的(不信可验证)
     */
    private LoadingDialogCancelListener loadingDialogCancelListener;
    private class LoadingDialogCancelListener implements OnCancelListener{
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mProxyCallback != null) {//当Loading类Dialog被取消(在可取消状态下，按back键)显示时回调给外部
                mProxyCallback.ownerToCancelLoadingRequest();
            }
        }
    }

    /**
     * 提示性Dialog被取消显示(在设置了可被取消,一般按back键)时的回调监听
     * 注：主动调用dialog.dismiss()是不会触发的(不信可验证)
     */
    private HintDialogCancelListener hintDialogCancelListener;
    private class HintDialogCancelListener implements OnCancelListener{
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mProxyCallback != null) {//当提示类Dialog被取消(在需要监听该类Dialog取消状态且可取消状态下，按back键)显示时回调给外部
                mProxyCallback.ownerToCancelHintDialog();
            }
        }
    }

    /**
     * 给统一的提示 Dialog 设置自定义的dialog 布局资源
     * @param extraHintDialogLayoutRes 自定义的Dialog布局资源，注：
     */
    public void setExtraHintDialogLayoutRes(@LayoutRes int extraHintDialogLayoutRes) {
        this.extraHintDialogLayoutRes = extraHintDialogLayoutRes;
    }

    public void setDialogCompatPadUi(boolean dialogCompatPadUi) {
        isDialogCompatPadUi = dialogCompatPadUi;
    }
}
