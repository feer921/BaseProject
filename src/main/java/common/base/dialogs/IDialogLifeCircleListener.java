package common.base.dialogs;

import android.app.Dialog;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2020/5/28<br>
 * Time: 21:25<br>
 * <P>DESC:
 * 一个Dialog的生命周期的监听者
 * 一般生命周期流程为：显示线： show() --> [如果没创建过则-->onCreate()] --> onStart()
 *                  消失线： [cancel()] --> dismiss() --> onStop()
 *                "[]"表示可有无
 * ******************(^_^)***********************
 */
public interface IDialogLifeCircleListener {

    /**
     * 当前Dialog 将要创建
     * 一般只有一次
     * @param theDialog 当前Dialog
     */
    void onDialogCreate(Dialog theDialog/*,Bundle savedInstanceState*/);

    /**
     * 当前Dialog 将要show()
     * @param theDialog 当前Dialog
     * @param titleHintOtherTexts [0] title; [1] hint text;[2] cancel btn text; [3] submit btn text... 使用者需要判断长度 ^~^
     */
    void onDialogShow(Dialog theDialog, CharSequence... titleHintOtherTexts);

    /**
     * 当前Dialog 将要消失
     * @param theDialog 当前Dialog
     * //         * @param dueToCancel true:是由于调用了 cancel()消失的；false:一般为 dismiss()方法的调用
     */
    void onDialogDismiss(Dialog theDialog/*, boolean dueToCancel*/);
}
