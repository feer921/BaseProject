package common.base.activitys;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import cn.pedant.SweetAlert.SweetAlertDialog;
import common.base.R;
import common.base.dialogs.BaseDialog;
import common.base.dialogs.CommonMdDialog;
import common.base.dialogs.CommonMdLoadialog;
import common.base.netAbout.BaseServerResult;
import common.base.netAbout.INetEvent;
import common.base.utils.NetHelper;
import common.base.utils.Util;
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
    private boolean isHintDialogCancelable = true;
    private boolean isHintDialogCancelableOutSide = false;
    private boolean isNeedListenHintDialogCancel = false;
    private Handler mHandler;
    /**
     * 提示加载对话框是否可按back键取消 默认为不可取消
     */
    private boolean loadingDialogCancelable = false;
    //added by fee 2016-07-28
    private SweetAlertDialog sweetAlertDialog;
    private SweetAlertDialog sweetLoadingDialog;
    public void setProxyCallback(IProxyCallback curProxyOwner){
        mProxyCallback = curProxyOwner;
    }

    public UIHintAgent(Context curContext) {
        this.mContext = curContext.getApplicationContext();//为了预防因Dialog造成的内存泄漏，此处上下文对象改为Application级
    }

    private void initHintDialog() {
        if (hintDialog == null) {
//            if (this.commonHintDialogWidth == 0) {
//                DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
//                this.commonHintDialogWidth = dm.widthPixels - 80;
//                this.commonHintDialogHeigth = dm.heightPixels / 3;
//            }
//            hintDialog = new CommonHintDialog(mContext, this.commonHintDialogWidth, this.commonHintDialogHeigth);
            hintDialog = new CommonMdDialog(mContext);
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
            if (isNeedListenHintDialogCancel) {
                if (hintDialogCancelListener == null) {
                    hintDialogCancelListener = new HintDialogCancelListener();
                }
                hintDialog.setOnCancelListener(hintDialogCancelListener);
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
        if (hintDialog != null) {
            if (isNeed) {
                if (hintDialogCancelListener == null) {
                    hintDialogCancelListener = new HintDialogCancelListener();
                }
                hintDialog.setOnCancelListener(hintDialogCancelListener);
            }
            else{
                hintDialog.setOnCancelListener(null);
            }
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
        if (sweetAlertDialog != null) {
            sweetAlertDialog.setCancelable(cancelable);
        }
        isHintDialogCancelable = cancelable;
    }

    public void toggleHintDialogCancelableOutSide(boolean hintDialogCancelable) {
        if (hintDialog != null) {
            hintDialog.setCanceledOnTouchOutside(hintDialogCancelable);
        }
        isHintDialogCancelableOutSide = hintDialogCancelable;
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
    public void dialogHint(String title,String hintMsg,int hintMsgGravity,String cancleBtnName, String sureBtnName, int dialogInCase) {
        if (!isOwnerVisible) {
            return;
        }
        if (hintDialog == null) {
            initHintDialog();
        }
        hintDialog.setTitle(title);
        hintDialog.setHintMsg(hintMsg);
        hintDialog.setHintMsgGravity(hintMsgGravity);
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
            if (cancelDialogListener == null) {
                cancelDialogListener = new LoadingDialogCancelListener();
            }
            loadDialog.setOnCancelListener(cancelDialogListener);
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
        //added by fee 2016-07-28
        if (sweetLoadingDialog != null) {
            sweetLoadingDialog.dismissWithAnimation();
        }
    }
    public boolean isLoadingDialogShowing(){
        return loadDialog!= null && loadDialog.isShowing();
    }
    public void dealWithServerResult(int requestDataType,BaseServerResult result) {
        if (!result.isOk) {
            //本意为针对所有的网络请求，服务器返回请求不成功时的 各种原因的统一通用处理(弹出提示对话框)
            //但由于框架无法得知具体的APP的服务器返回请求不成功的原因，所以目前无法在此统一处理,可以交给各APP的统一基类来处理
            if (loadDialog != null) {
                loadDialog.dismiss();
            }
        }
    }
    public void dealWithServerError(int requestDataType,String errorInfo) {
        if (loadDialog != null) {
            loadDialog.dismiss();
        }
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

        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismiss();
        }
        if (sweetLoadingDialog != null) {
            sweetLoadingDialog.dismiss();
        }
    }

    public int getHintDialogInCase() {
        if (hintDialog != null) {
            return hintDialog.curDialogInCase;
        }
//        return BaseServerResult.CODE_DEF_NO_MEANING;
        return sweetDialogInCase;//changed to get the sweet dialog in case,def also is 0
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

    public int getSweetDialogInCase() {
        return sweetDialogInCase;
    }

    //added sweetAlertDialog codes by fee 2016-07-28
    private int sweetDialogInCase = 0;
    public void sweetLoading(String loadhintMsg) {
        if (sweetLoadingDialog == null) {
            sweetLoadingDialog = new SweetAlertDialog(mContext);
            sweetLoadingDialog.setCancelable(loadingDialogCancelable);
            sweetLoadingDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
            if (cancelDialogListener == null) {
                cancelDialogListener = new LoadingDialogCancelListener();
            }
            sweetLoadingDialog.setOnCancelListener(cancelDialogListener);
        }
        sweetLoadingDialog.setTitleText(loadhintMsg);
        if (!sweetLoadingDialog.isShowing()) {
            sweetLoadingDialog.show();
        }
    }

    public void sweetHintSuc(String successInfo,String comfimInfo,int sweetDialogInCase) {
        sweetDialogHint(successInfo, null, null, comfimInfo, sweetDialogInCase, SweetAlertDialog.SUCCESS_TYPE);
    }

    public void sweetHintFail(String failHintInfo, String comfimInfo, int sweetDialogInCase) {
        sweetDialogHint(failHintInfo, null, null, comfimInfo, sweetDialogInCase, SweetAlertDialog.ERROR_TYPE);
    }
    public void sweetDialogHint(String titleInfo, String hintInfo, String cancelInfo, String comfimInfo, int curDialogInCase,int sweetDialogContentCase) {
        if (!isOwnerVisible) {
            return;
        }
        sweetDialogInCase = curDialogInCase;
        initSweetAlertDialog();
//        sweetAlertDialog.setOnCancelListener(null);
        sweetAlertDialog.setTitleText(titleInfo)
                .setContentText(hintInfo)
                .setCancelText(cancelInfo)
                .setConfirmText(comfimInfo)
                .setConfirmClickListener(comfimBtnClickListener)
                .changeAlertType(sweetDialogContentCase);
        sweetAlertDialog.showCancelButton(!Util.isEmpty(cancelInfo));
        sweetAlertDialog.showContentText(!Util.isEmpty(hintInfo));
        sweetAlertDialog.show();
    }
    private void initSweetAlertDialog() {
        if (sweetAlertDialog == null) {
            sweetAlertDialog = new SweetAlertDialog(mContext);
            sweetAlertDialog.setCancelable(isHintDialogCancelable);
        }
    }
    private LoadingDialogCancelListener cancelDialogListener;
    private class LoadingDialogCancelListener implements OnCancelListener{
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mProxyCallback != null) {
                mProxyCallback.ownerToCancelLoadingRequest();
            }
        }
    }
    private HintDialogCancelListener hintDialogCancelListener;
    private class HintDialogCancelListener implements OnCancelListener{
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mProxyCallback != null) {
                mProxyCallback.ownerToCancelHintDialog();
            }
        }
    }
    private SweetAlertDialog.OnSweetClickListener comfimBtnClickListener = new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            if (mClickListenerForDialog != null) {
                mClickListenerForDialog.onClick(sweetAlertDialog,DialogInterface.BUTTON_POSITIVE);
            }
        }
    };
}
