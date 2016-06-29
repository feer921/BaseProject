package common.base.interfaces;

import android.widget.EditText;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-29
 * Time: 11:35
 * DESC: 针对EditText的文本变化的监听
 */
public interface ITextWatcher {
    /***
     * 文本变化中的回调
     * @param curEditText
     */
    void onTextChanged(EditText curEditText);

    /**
     * 文本变化后的回调
     * @param curEditTextView
     */
    void onAfterTextChanged(EditText curEditTextView);
}
