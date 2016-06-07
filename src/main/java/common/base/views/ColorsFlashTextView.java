package common.base.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-06
 * Time: 15:00
 * DESC: 有颜色闪动的TextView
 */
public class ColorsFlashTextView extends TextView {
    LinearGradient gradient;
    Matrix matrix;
    private int mTranslate;

    public ColorsFlashTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorsFlashTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (matrix != null) {
            mTranslate += getMeasuredWidth() / 10;
            if (mTranslate > getMeasuredWidth() * 2) {
                mTranslate = -getMeasuredWidth();
            } //当偏移超过两倍宽度是移到到最前
            matrix.setTranslate(mTranslate, 0); //设置偏移
            gradient.setLocalMatrix(matrix); //渐变开始偏移
            postInvalidateDelayed(100); //刷新间隔
        }
    }
    private void init() {
        Paint mPaint = getPaint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setTextSize(40);
        /**
         *Shader.TileMode.CLAMP   重复最后一个颜色至最后
         Shader.TileMode.MIRROR  重复着色的图像水平或垂直方向已镜像方式填充会有翻转效果
         Shader.TileMode.REPEAT  重复着色的图像水平或垂直方向
         */
        gradient = new LinearGradient(0, 0, getMeasuredWidth(), 0, Color.RED, Color.BLUE, Shader.TileMode.CLAMP);
        mPaint.setShader(gradient);
        matrix = new Matrix();
    }
}
