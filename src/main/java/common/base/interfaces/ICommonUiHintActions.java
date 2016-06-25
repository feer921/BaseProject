package common.base.interfaces;

import android.view.View;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-25
 * Time: 16:21
 * DESC: 一些通用的UI提示类方法的接口
 */
public interface ICommonUiHintActions {
    /**
     * 注意：该提示性PopupWindow适用与在一个界面的顶部经由上至下的动画弹出
     *
     * @param anchorView 一般为顶部的一个控件
     * @param xOffset    X方向的偏移量
     * @param yOffset    Y方向的偏移量
     */
    void initCommonHintPopuWindow(View anchorView, int xOffset, int yOffset);

    void showCommonLoading(String hintMsg);

    void showCommonLoading(int hintMsgResID);

    void dialogHint(String dialogTitle, String hintMsg, String cancelBtnName, String sureBtnName, int dialogInCase);

    void dialogHint(int titleResID, int hintMsgResID, int cancelBtnNameResID, int sureBtnNameResID, int dialogInCase);

    void popupHint(String hintMsg);

    void popupHint(int hintMsgResID);
}
