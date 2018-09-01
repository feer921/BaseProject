package common.base.interfaces;

import android.widget.TextView;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-29
 * Time: 11:35
 * DESC: 针对EditText的文本变化的监听
 */
public interface ITextWatcher {
    /***
     * 文本变化中的回调
     * @param curEditText //changed by fee 2017-08-23 TextWatcher是TextView中的接口，故把参数对象提升到TextView
     */
    void onTextChanged(TextView curEditText);

    /**
     * 文本变化后的回调
     * @param curEditTextView
     */
    void onAfterTextChanged(TextView curEditTextView);
}
