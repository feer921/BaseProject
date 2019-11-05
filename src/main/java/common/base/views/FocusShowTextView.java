package common.base.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import common.base.R;
import common.base.utils.CommonLog;
import common.base.utils.Util;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/9/20<br>
 * Time: 17:03<br>
 * <P>DESC:
 * 把输入法输入变成焦点状态的外部显示的控件
 * 适合场景：验证码输入 以及其他的看不到输入框但是所输入的文本在指定的显示控件上来显示的场景
 * </p>
 * ******************(^_^)***********************
 */
public class FocusShowTextView extends FrameLayout implements TextWatcher, InputFilter,View.OnClickListener{

    private final String TAG = "FocusShowTextView";
//    /**
//     * 外部来提供显示文本的控件提供者
//     */
//    private IShowTextViewProvider showTextViewProvider;

    private LinearLayout llShowTextViewsContainer;

    private TextView[] showTextViews;

    private ICanShowTextView[] canShowTextViews;

    private EditText innerEditText;

    private boolean isDebugShowInputText;

    /**
     * 显示文本的控件间的间距,px
     */
    private int showTextViewGapWidth;
    /**
     * 承载显示文本的控件数量
     * def = 4,如4位数的验证码
     */
    private int countOfShowTextViews = 4;

    /**
     * 是否需要自动测量showTextViews 的宽、高
     * def = false
     */
    private boolean isNeedAutoMeasureViewWH;

    public FocusShowTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FocusShowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FocusShowTextView, defStyleAttr, 0);
        countOfShowTextViews = ta.getInteger(R.styleable.FocusShowTextView_showTextViewCount, countOfShowTextViews);
//        int attrsIndexCount = ta.getIndexCount();
//        for (int i = 0; i < attrsIndexCount; i++) {
//            int curAttr = ta.getIndex(i);
//            switch (curAttr) {
//                case common.base.R.styleable.FocusShowTextView_showTextViewCount:
//                    countOfShowTextViews = ta.getInteger(curAttr, countOfShowTextViews);
//                    break;
//                default:
//                    break;
//            }
//        }
        isDebugShowInputText = ta.getBoolean(R.styleable.FocusShowTextView_showDebugInputText, false);
        //读取出对内部EditText的配置参数
        int inputType = InputType.TYPE_CLASS_NUMBER;
        inputType = ta.getInt(R.styleable.FocusShowTextView_android_inputType, inputType);
        innerEditText = new EditText(context);
        innerEditText.setCursorVisible(false);
        innerEditText.setTextColor(0);
        innerEditText.setTextSize(0.1f);
        innerEditText.setInputType(inputType);
        int imeOptionsAttr = R.styleable.FocusShowTextView_android_imeOptions;
        if (ta.hasValue(imeOptionsAttr)) {
            int curImeOptions = ta.getInt(imeOptionsAttr, innerEditText.getImeOptions());
            innerEditText.setImeOptions(curImeOptions);
        }
        int imeActionIdAttr = R.styleable.FocusShowTextView_android_imeActionId;
        int imeActionLabelAttr = R.styleable.FocusShowTextView_android_imeActionLabel;
        if (ta.hasValue(imeActionLabelAttr) && ta.hasValue(imeActionIdAttr)) {
            int oldActionId = innerEditText.getImeActionId();
            innerEditText.setImeActionLabel(ta.getText(imeActionLabelAttr), ta.getInt(imeActionIdAttr, oldActionId));
        }
        int editBackgroundAttr = R.styleable.FocusShowTextView_editTextBg;
        if (ta.hasValue(editBackgroundAttr)) {
            innerEditText.setBackgroundDrawable(ta.getDrawable(editBackgroundAttr));
        }
        else {
            innerEditText.setBackgroundDrawable(null);
        }
        if (countOfShowTextViews <= 0) {
            countOfShowTextViews = 4;
        }

        //每个showTextView之间的间距
        showTextViewGapWidth = ta.getDimensionPixelSize(R.styleable.FocusShowTextView_showTextViewGapWidth, Util.getDimenResPixelSize(context, R.dimen.dp_20));
        int showTextSize = ta.getDimensionPixelSize(R.styleable.FocusShowTextView_showTextSize, Util.getDimenResPixelSize(context, R.dimen.sp_25));
        ColorStateList textColor = ta.getColorStateList(R.styleable.FocusShowTextView_showTextColor);
        Drawable showTextViewBg = ta.getDrawable(R.styleable.FocusShowTextView_showTextViewBackground);
        int defWH = Util.getDimenResPixelSize(context, R.dimen.dp_60);

        int showTextViewWidth = defWH, showTextViewHeight = defWH;
        int showTextViewWidthAttr = R.styleable.FocusShowTextView_showTextViewWidth;
        boolean isHasWidthAttr = ta.hasValue(showTextViewWidthAttr);

        int showTextViewHeightAttr = R.styleable.FocusShowTextView_showTextViewHeight;
        boolean isHasHeightAttr = ta.hasValue(showTextViewHeightAttr);
        
        isNeedAutoMeasureViewWH = !isHasWidthAttr && !isHasHeightAttr;//没有给 showTextView 设置宽、高

        if (isHasWidthAttr) {
            showTextViewWidth = ta.getDimensionPixelSize(showTextViewWidthAttr, defWH);
            if (!isHasHeightAttr) {
                showTextViewHeight = showTextViewWidth;
            }
        }

        if (isHasHeightAttr) {
            showTextViewHeight = ta.getDimensionPixelSize(showTextViewHeightAttr, defWH);
            if (!isHasWidthAttr) {
                showTextViewWidth = showTextViewHeight;
            }
        }


        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(showTextViewWidth, showTextViewHeight);
        llp.rightMargin = showTextViewGapWidth;

        showTextViews = new TextView[countOfShowTextViews];
        for (int i = 0; i < countOfShowTextViews; i++) {
            TextView aShowTextView = new TextView(context);
            showTextViews[i] = aShowTextView;
            aShowTextView.setGravity(Gravity.CENTER);
            aShowTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, showTextSize);
            if (textColor != null) {
                aShowTextView.setTextColor(textColor);
            }
            else {
                aShowTextView.setTextColor(0xff333333);
            }
            if (showTextViewBg != null) {
                aShowTextView.setBackgroundDrawable(showTextViewBg);
            }
            else {
                aShowTextView.setBackgroundResource(R.drawable.shape_stroke_1_radius_10_bg_white);
            }

            if (i == countOfShowTextViews - 1) {//最后一个不要右边距
                llp = new LinearLayout.LayoutParams(showTextViewWidth, showTextViewHeight);
            }
            aShowTextView.setLayoutParams(llp);
        }

        ta.recycle();
        initViews(context);
    }
    private TextView tvDebugInputInfo;
    private void initViews(Context context) {
        innerEditText.addTextChangedListener(this);
//        innerEditText.setOnClickListener(this);

        LayoutParams childLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        addView(innerEditText,childLayoutParams);

        llShowTextViewsContainer = new LinearLayout(context);
        llShowTextViewsContainer.setOrientation(LinearLayout.HORIZONTAL);
        llShowTextViewsContainer.setGravity(Gravity.CENTER_VERTICAL);

        addView(llShowTextViewsContainer,childLayoutParams);
        setShowTextViews(showTextViews);
        showDebugTextView(isDebugShowInputText);
    }

    public void showDebugTextView(boolean isDebugShowInputText) {
        this.isDebugShowInputText = isDebugShowInputText;
        if (!isDebugShowInputText) {
            if (tvDebugInputInfo != null) {
                tvDebugInputInfo.setVisibility(GONE);
            }
        }
        else {//要显示
            if (tvDebugInputInfo == null) {
                tvDebugInputInfo = new TextView(getContext());
                tvDebugInputInfo.setTextColor(0xff00ff00);
                LayoutParams debugViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                debugViewParams.gravity = Gravity.CENTER_HORIZONTAL;
                addView(tvDebugInputInfo, debugViewParams);
            }
            else {
                tvDebugInputInfo.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        CommonLog.w(TAG, "-->onFinishInflate() isNeedAutoMeasureViewWH = " + isNeedAutoMeasureViewWH + " " + getMeasuredWidth());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        CommonLog.d(TAG, "-->onMeasure() isNeedAutoMeasureViewWH = " + isNeedAutoMeasureViewWH);
        if (isNeedAutoMeasureViewWH && llShowTextViewsContainer != null) {
            reMeasureShowTextViews();
        }
    }

    private void reMeasureShowTextViews() {
        int childViewCount = llShowTextViewsContainer.getChildCount();
        if (childViewCount == 0) {
            return;
        }
        int measureWidth = getMeasuredWidth();
        int measureHeight = getMeasuredHeight();
        if (measureWidth > 0) {
            int totalItemGapWidth = showTextViewGapWidth * (countOfShowTextViews - 1);
            if (totalItemGapWidth >= measureWidth) {
                return;
            }
            int showTextViewCanUseWidth = measureWidth - totalItemGapWidth;
            if (showTextViewCanUseWidth > 10 * countOfShowTextViews) {//这里是为了保证至少每个showTextView有个10px的宽度
                int perShowTextViewWidth = showTextViewCanUseWidth / countOfShowTextViews;
                CommonLog.e(TAG, "-->reMeasureShowTextViews() perShowTextViewWidth = " + perShowTextViewWidth);
                int perShowTextViewHeight = perShowTextViewWidth;//或者这里需要按 宽高比来计算？？？
                LinearLayout.LayoutParams childViewParams = new LinearLayout.LayoutParams(perShowTextViewWidth, perShowTextViewHeight);
                childViewParams.rightMargin = showTextViewGapWidth;
                boolean isJustOne = childViewCount == 1;
                if (isJustOne) {
                    llShowTextViewsContainer.setGravity(Gravity.CENTER);
                }
                for (int i = 0; i < childViewCount; i++) {
                    View childView = llShowTextViewsContainer.getChildAt(i);
                    if (i == childViewCount - 1) {
                        childViewParams = new LinearLayout.LayoutParams(perShowTextViewWidth, perShowTextViewHeight);
                    }
                    childView.setLayoutParams(childViewParams);
                }
                isNeedAutoMeasureViewWH = false;
            }
        }
    }

    public void setNeedAutoMeasureShowTextView(boolean needAutoMeasureShowTextView) {
        this.isNeedAutoMeasureViewWH = needAutoMeasureShowTextView;
        requestLayout();
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
//        CommonLog.d(TAG, "-->onTextChanged() s = " + s + " start = " + start + " before = " + before + " count = " + count);
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
        String inputtedText = s == null ? "" : s.toString();
        //只能在这里处理了
        showTextOnShowTextView(inputtedText);
        if (tvDebugInputInfo != null && isDebugShowInputText) {
            tvDebugInputInfo.setText(inputtedText);
        }
        onInput(inputtedText);
    }

    private void showTextOnShowTextView(CharSequence inputtedText) {
        int showTextViewCount = countOfShowTextViews;
        for (int i = 0; i < showTextViewCount; i++) {
            String textAtIndex = "";
            if (i < inputtedText.length()) {
                char charAtIndex = inputtedText.charAt(i);
                textAtIndex = String.valueOf(charAtIndex);
                if (mReplaceShowText != null) {
                    CharSequence willReplaceShowText = mReplaceShowText.onShowTextWillReplace(textAtIndex);
                    if (!TextUtils.isEmpty(willReplaceShowText)) {
                        textAtIndex = willReplaceShowText.toString();
                    }
                }
            }
            else {
                textAtIndex = "";
            }
            if (showTextViews != null) {
                showTextViews[i].setText(textAtIndex);
            }
            if (canShowTextViews != null) {
                canShowTextViews[i].showText(textAtIndex);
            }
        }
    }
    /**
     * 内部默认实现输入的过滤
     * 如果限制不能输入空格
     * @param source
     * @param start
     * @param end
     * @param dest
     * @param dstart
     * @param dend
     * @return
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String willInputText = source.toString();
        if (" ".equals(willInputText)) {
            return "";
        }
        return null;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        CommonLog.i(TAG, "-->onClick() v = " + v);
    }


    /**
     * 该接口是给外部如果有自定义的控件来承载显示文本的控件实现的
     */
    public interface ICanShowTextView {
        void showText(CharSequence textToShow);

        View theViewSelf();
    }

//    public interface IShowTextViewProvider {
//        /**
//         * 外部来提供要以焦点显示本控件输入的文本的 TextView的显示控件
//         *
//         * @return TextView[]
//         */
//        TextView[] provideShowTextViews();
//
//        /**
//         * 外部来提供能够显示本控件输入的文本的实现了ICanShowTextView接口的控件
//         * 因为外部并不想仅使用TextView来显示文本，可以还有更复杂的控件来显示文本
//         *
//         * @return ICanShowTextView[]
//         */
//        ICanShowTextView[] provideCanShowTextViews();
//    }

//    public void setIShowTextViewProvider(IShowTextViewProvider provider) {
//        this.showTextViewProvider = provider;
//        if (provider != null) {
//
//        }
//    }

    public void setShowTextViews(TextView... toFocusShowTextViews) {
        if (toFocusShowTextViews != null) {
            int len = toFocusShowTextViews.length;
            if (len > 0) {
                countOfShowTextViews = len;
                setEditTextInputFilter(this, new LengthFilter(len));
                this.showTextViews = toFocusShowTextViews;
                this.canShowTextViews = null;
                setUpShowTextViews();
            }
        }
    }

    private void setUpShowTextViews() {
        if (llShowTextViewsContainer != null) {
            if (llShowTextViewsContainer.getChildCount() > 0) {
                //已经有了，则先移除掉
                llShowTextViewsContainer.removeAllViews();
            }
            if (showTextViews != null) {
                for (TextView showTextView : this.showTextViews) {
                    llShowTextViewsContainer.addView(showTextView);
                }
            }
            if (canShowTextViews != null) {
                for (ICanShowTextView canShowTextView : canShowTextViews) {
                    llShowTextViewsContainer.addView(canShowTextView.theViewSelf());
                }
            }
        }
    }

    public void setCanShowTextViews(ICanShowTextView... canShowTextViews) {
        if (canShowTextViews != null) {
            int len = canShowTextViews.length;
            if (len > 0) {
                countOfShowTextViews = len;
                setEditTextInputFilter(this, new LengthFilter(len));
                this.canShowTextViews = canShowTextViews;
                this.showTextViews = null;
                setUpShowTextViews();
            }
        }
    }

    /**
     * 如果外部调用了，则以外部的InputFilter为准
     * @param inputFilters 输入的过滤器
     */
    public void setEditTextInputFilter(InputFilter... inputFilters) {
        if (inputFilters != null && innerEditText != null) {
            innerEditText.setFilters(inputFilters);
        }
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener l) {
        if (innerEditText != null) {
            innerEditText.setOnEditorActionListener(l);
        }
    }

    /**
     * 清除显示的文本
     */
    public void clearShowTexts() {
        if (innerEditText != null) {
            innerEditText.setText("");
        }
    }

    public EditText getInnerEditText() {
        return innerEditText;
    }
    private IInputEventListener inputEventListener;

    public void setInputEventListener(IInputEventListener l) {
        this.inputEventListener = l;
    }

    private void onInput(CharSequence inputtedText) {
        if (inputEventListener != null) {
            inputEventListener.onInputEvent(inputtedText, inputtedText.length() == countOfShowTextViews);
        }
    }
    public interface IInputEventListener{
        /**
         * 监听输入事件
         * @param inputtedText 当前输入的文本
         * @param isComplete 是否输入完成
         */
        void onInputEvent(CharSequence inputtedText, boolean isComplete);
    }

    private IWillReplaceShowText mReplaceShowText;

    public void setNeedReplaceShowText(IWillReplaceShowText needReplaceShowText) {
        this.mReplaceShowText = needReplaceShowText;
    }

    /**
     * 可用来替换当前所输入的文本,而显示所替换的文本
     * 如：作为密码输入时，用户实际上输入明文，实现此接口后，可以替换成“*”
     */
    public interface IWillReplaceShowText {

        /**
         * 给实现者一个机会把将要显示的文本给替换成自己想要怎么显示的文本
         * 如：作为密码显示的时候，用户实际上输入了明文，但需要被替换显示成"*"
         * @param theInputtedIndexText 当前index下所输入的原文本
         * @return 要替换显示成想要的文本
         */
        CharSequence onShowTextWillReplace(CharSequence theInputtedIndexText);
    }
}
