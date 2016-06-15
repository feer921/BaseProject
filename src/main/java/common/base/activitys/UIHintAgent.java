package common.base.activitys;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;

import common.base.R;
import common.base.dialogs.BaseDialog;
import common.base.dialogs.CommonHintDialog;
import common.base.dialogs.CommonMdDialog;
import common.base.dialogs.CommonMdLoadialog;
import common.base.netAbout.INetEvent;
import common.base.utils.NetHelper;
import common.base.views.HintPopuWindow;


/**
 * UI交互过程一些提醒类型的交互代理者
 * 里面的一些通用交互控件都呆以自定义再对应的替换
 * 2015年9月7日-下午7:49:21
 * @author lifei
 */
public class UIHintAgent {
    private CommonMdLoadialog loadDialog;
    private BaseDialog hintDialog;
    private Context mContext;
    private HintPopuWindow hintPopuWindow;
    private DialogInterface.OnClickListener mClickListenerForDialog;
    private IProxyCallback  mProxyCallback;
    private boolean isOwnerVisible = true;
    private boolean hintDialogCancelable = true;
    private Handler mHandler;
    /**
     * 提示加载对话框是否可按back键取消 默认为不可取消
     */
    private boolean loadingDialogCancelable = false;
    public void setProxyCallback(IProxyCallback curProxyOwner){
        mProxyCallback = curProxyOwner;
    }

    public UIHintAgent(Context curContext) {
        this.mContext = curContext;
    }
    int commonHintDialogWidth,commonHintDialogHeigth;
    public void configCommonHintDialogWH(int toSetHintDialogWidth, int toSetHintDialogHeight) {
        this.commonHintDialogWidth = toSetHintDialogWidth;
        this.commonHintDialogHeigth = toSetHintDialogHeight;
    }
    private void initHintDialog() {
        if (hintDialog == null) {
            if (this.commonHintDialogWidth == 0) {
                DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
                this.commonHintDialogWidth = dm.widthPixels - 80;
                this.commonHintDialogHeigth = dm.heightPixels / 3;
            }
//            hintDialog = new CommonHintDialog(mContext, this.commonHintDialogWidth, this.commonHintDialogHeigth);
            hintDialog = new CommonMdDialog(mContext);
            hintDialog.edtViewCanEdit(false);
            hintDialog.setCancelable(hintDialogCancelable);
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
        }
    }

    public void setHintDialogOnClickListener(DialogInterface.OnClickListener l) {
        mClickListenerForDialog = l;
        if (hintDialog != null) {
            hintDialog.setDialogClickListener(mClickListenerForDialog);
        }
    }
    /**
     * 开关 : 提示用Dialog 是否可按back键取消
     * @param cancelable
     */
    public void toggleHintDialogCancelable(boolean cancelable){
        if(hintDialog != null){
            hintDialog.setCancelable(cancelable);
        }
        hintDialogCancelable = cancelable;
    }
    /**
     * 开关 : 加载对话框 是否可按back键取消
     * @param cancelable
     */
    public void toggleLoadingDialogCancelable(boolean cancelable){
        if(loadDialog != null){
            loadDialog.setCancelable(cancelable);
        }
        loadingDialogCancelable = cancelable;
    }
    public void onClickInDialog(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
//            switch (hintDialog.curDialogInCase) {
//                case ServerResult.CODE_NON_LOGINED:// 未登陆
//                    UiHelper.jumpToLogin(mContext);
//                    switchActivity(false);
//                    break;
//                case ServerResult.CODE_USER_ACCOUNT_NOT_VERTIFIED:// 未实名认证
//                    Intent toIntent = new Intent(mContext, RealNameVertifyActivity.class);
//                    UiHelper.jumpToActivity(mContext, toIntent);
//                    switchActivity(false);
//                    break;
//                case ServerResult.CODE_PHONE_IN_BLACKLIST:// 帐号黑名单了，拨打客服电话
//                    Util.callService(mContext);
//                    break;
//                case ServerResult.CODE_USER_ACCOUNT_NO_ENOUGH_MONEY:// 帐户余额不足，去充值
//                    Intent toReChargeIntent = new Intent(mContext, AccountRechargeActivity.class);
//                    UiHelper.jumpToActivity(mContext, toReChargeIntent);
//                    switchActivity(false);
//                    break;
//                case ServerResult.CODE_ERROR_NO_NET://没有网络,点击则去设置网络
//                    Intent toSettingNetIntent = new Intent(Settings.ACTION_SETTINGS);
//                    UiHelper.jumpToActivity(mContext, toSettingNetIntent);
//                    switchActivity(false);
//                    break;
//                case ServerResult.CODE_NOT_AUTHORISE_AUTO_REPAY://未授权自动还款权限，点击确定后跳转进筹款记录之“还款中”项目列表界面
//                    //如果 提现界面需要点击该确定按钮后关闭自己，则需要自己实现所继承的基类的拦截dialog的点击事件，并作以下处理
//                    Intent toLookFundraiseRecord = new Intent(mContext, FundRaisingRecordActivity.class);
//                    toLookFundraiseRecord.putExtra(TjjConfig.INTENT_KEY_TO_PRODUCT_STATUS, TjjConfig.RECORD_PRODUCT_STATUS_4_RETURNING);
//                    UiHelper.jumpToActivity(mContext, toLookFundraiseRecord);
//                    switchActivity(false);
//                    break;
//                default:
//                    break;
//            }
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
                ((Activity) mContext).overridePendingTransition(R.anim.common_left_in, R.anim.common_left_out);
            } else {
                ((Activity) mContext).overridePendingTransition(R.anim.common_right_in, R.anim.common_right_out);
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
        if(!isOwnerVisible){
            return;
        }
        if (hintDialog == null) {
            initHintDialog();
        }
        hintDialog.setTitle(title);
        hintDialog.setHintMsg(hintMsg);
        hintDialog.curDialogInCase = dialogInCase;
        hintDialog.setCancleBtnName(cancleBtnName);
        hintDialog.setCommitBtnName(sureBtnName);
        hintDialog.show();
    }

    /**
     * 显示加载loading...对话框
     * @param hintMsg
     */
    public void showLoading(String hintMsg) {
        if (loadDialog == null) {
            loadDialog = new CommonMdLoadialog(mContext);
            loadDialog.setCanceledOnTouchOutside(false);
            loadDialog.setCancelable(loadingDialogCancelable);
            loadDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if(mProxyCallback != null){
                        mProxyCallback.ownerToCanceleRequest();
                    }
                }
            });
        }
        loadDialog.setHintMsg(hintMsg);
        if (!loadDialog.isShowing()) {
            loadDialog.show();
        }
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
        if (loadDialog != null) {
            loadDialog.dismiss();
        }
    }
    public boolean isLoadingDialogShowing(){
        return loadDialog!= null && loadDialog.isShowing();
    }
//    public void dealWithServerResult(int requestDataType,ServerResult result) {
//        if (!result.isOk) {
//            if (loadDialog != null) {
//                loadDialog.dismiss();
//            }
//            String serverErrorToLocalInfo = getString(result.getErrorInfoResIdBaseCode());
//            String dialogTitle = "提示";
//            String hintMsg = null;
////            String cancleBtnName = "我知道了";
//            String cancleBtnName = "";
//            String ensureBtnName = "";
//            switch (result.serverResponseCode) {
//                case ServerResult.CODE_NON_LOGINED:// 未登陆或者登陆状态失效
//                    hintMsg = serverErrorToLocalInfo;
//                    ensureBtnName = "确认";
////                    CubeApp.getMe().setLogined(false);
//                    break;
//                case ServerResult.CODE_PHONE_IN_BLACKLIST:// 被加入黑名单
//                    hintMsg = "抱歉,您的手机号码被列为黑名单了.";
//                    ensureBtnName = "咨询客服";
//                    break;
//                case ServerResult.CODE_REGIESTED_ALREADY:// 本号码已经被注册了
//                    hintMsg = "该手机号码已注册";
//                    ensureBtnName = "去登录";
//                    break;
//                case ServerResult.CODE_USER_ACCOUNT_NOT_VERTIFIED:// 未实名认证
//                    hintMsg = "您的帐号未进行实名认证";
//                    ensureBtnName = "去认证";
//                    break;
//                case ServerResult.CODE_USER_ACCOUNT_NO_ENOUGH_MONEY:// 帐户余额不足
//                    hintMsg = "抱歉,当前帐户余额不足";
//                    ensureBtnName = "充值";
//                    break;
//                case ServerResult.CODE_A_PROJECT_AREADY_IN_CHECKING://已经有一个项目在审核中
//                    hintMsg = serverErrorToLocalInfo;
//                    ensureBtnName = "我想去看看";
//                    break;
//                case ServerResult.CODE_NOT_AUTHORISE_AUTO_REPAY://用户未授权自动还款
//                    hintMsg = serverErrorToLocalInfo;
//                    ensureBtnName = "查看项目";
//                    break;
//                default:
//                    if(mProxyCallback != null && mProxyCallback.ownerDealWithServerResult(requestDataType,result)){
//                        break;
//                    }
//                    popupHint(serverErrorToLocalInfo);
//                    break;
//            }
//            if (!Util.isTextEmpty(ensureBtnName)) {
//                dialogHint(dialogTitle, hintMsg, cancleBtnName, ensureBtnName, result.serverResponseCode);
//            }
//        }
//    }
    public void dealWithServerError(int requestDataType,String errorInfo) {
        if (loadDialog != null) {
            loadDialog.dismiss();
        }
        if (!NetHelper.isNetworkConnected(mContext) && !INetEvent.MANULLY_DELAY_OVER.equals(errorInfo)) {
            dialogHint("提示", "当前网络无效,请设置", null, "去设置网络", 0
                    //ServerResult.CODE_ERROR_NO_NET
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
     * 结束代理UI交互
     */
    public void finishAgentFollowUi() {
        if (hintDialog != null) {
            hintDialog.dismiss();
        }
        if (loadDialog != null) {
            loadDialog.dismiss();
        }
        if (hintPopuWindow != null) {
            hintPopuWindow.dismiss();
        }
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
    }

    public int getHintDialogInCase() {
        if (hintDialog != null) {
            return hintDialog.curDialogInCase;
        }
//        return ServerResult.CODE_DEF_NO_MEANING;
        return 0;
    }

    public Dialog getCommonHintDialog() {
        return this.hintDialog;
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
}
