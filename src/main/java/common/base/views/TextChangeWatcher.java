package common.base.views;

import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.widget.EditText;
import common.base.interfaces.ITextWatcher;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-29
 * Time: 11:38
 * DESC: 对TextWatcher 的封装，主要是想把当前有EditText通过接口ITextWatcher回调出去
 */
public class TextChangeWatcher implements TextWatcher{
    private EditText curEditText;
    private ITextWatcher iTextWatcher;

    public TextChangeWatcher(EditText curEditText, ITextWatcher iTextWatcher) {
        this.curEditText = curEditText;
        this.iTextWatcher = iTextWatcher;
    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (iTextWatcher != null) {
            iTextWatcher.onTextChanged(this.curEditText);
        }
    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     * It is legitimate to make further changes to <code>s</code> from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {
        if (iTextWatcher != null) {
            iTextWatcher.onAfterTextChanged(this.curEditText);
        }
    }
}
