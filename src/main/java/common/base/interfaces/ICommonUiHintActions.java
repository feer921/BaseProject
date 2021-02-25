package common.base.interfaces;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-25
 * Time: 16:21
 * DESC: 一些通用的UI提示类方法的接口
 */
public interface ICommonUiHintActions {


    void showCommonLoading(String hintMsg);

    void showCommonLoading(int hintMsgResID);

    void dialogHint(String dialogTitle, String hintMsg, String cancelBtnName, String sureBtnName, int dialogInCase);

    void dialogHint(int titleResID, int hintMsgResID, int cancelBtnNameResID, int sureBtnNameResID, int dialogInCase);

}
