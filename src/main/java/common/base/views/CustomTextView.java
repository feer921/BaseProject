package common.base.views;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2020/1/10<br>
 * Time: 11:35<br>
 * <P>DESC:
 * </p>
 * ******************(^_^)***********************
 */
public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTextSizeWithPxValue(@Px int textSizePxValue) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePxValue);
    }
}
