package common.base.views;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * 可点击的文本Span
 * </p>
 * ******************(^_^)***********************
 */
public class ExtraClickableSpan extends ClickableSpan {

    private boolean isNeedUnderLine = true;

    /**
     * 当前包裹的Span文本
     */
    private CharSequence wrappedSpanText;

    /**
     * 当前用来区分Span类型的，点击的时候可用来区分点到哪里
     */
    private int spanType;
    private OnClickableSpanClickListener clickableSpanClickListener;

    private @ColorInt int linkTextColor;

    public ExtraClickableSpan(CharSequence canClickableSpanText) {
        this.wrappedSpanText = canClickableSpanText;
    }


    public ExtraClickableSpan() {

    }

    /**
     * Performs the click action associated with this span.
     * @param widget
     */
    @Override
    public void onClick(@NonNull View widget) {
        if (clickableSpanClickListener != null) {
            clickableSpanClickListener.onSpanClick(this, widget, spanType, wrappedSpanText);
        }
    }

    /**
     * Makes the text underlined and in the link color.
     *
     * @param ds
     */
    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        if (!isNeedUnderLine) {//下划线
            ds.setUnderlineText(false);
        }
        if (linkTextColor != 0) {
            ds.setColor(linkTextColor);//超链接文本颜色,还有一个点击时的高亮背景颜色，是属于TextView的textColorHighlight属性
        }
    }

    public void setNeedUnderLine(boolean needUnderLine) {
        isNeedUnderLine = needUnderLine;
    }

    public void setWrappedSpanText(CharSequence wrappedSpanText) {
        this.wrappedSpanText = wrappedSpanText;
    }

    public void setSpanType(int spanType) {
        this.spanType = spanType;
    }

    public void setLinkTextColor(@ColorInt int linkTextColor) {
        this.linkTextColor = linkTextColor;
    }

    public void setClickableSpanClickListener(OnClickableSpanClickListener clickableSpanClickListener) {
        this.clickableSpanClickListener = clickableSpanClickListener;
    }

    public interface OnClickableSpanClickListener{
        void onSpanClick(ExtraClickableSpan clickableSpan, View widget, int spanType, CharSequence wrappedClickableText);

    }
}
