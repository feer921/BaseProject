package common.base.glide.glideImageView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import androidx.annotation.IntDef;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import common.base.R;
import common.base.utils.Util;


public class ShapeImageView extends ImageView {

    // 定义Bitmap的默认配置
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLOR_DRAWABLE_DIMENSION = 1;

    // 图片的宽高
    private int width;
    private int height;

    private int borderColor = 0x1A000000; // 边框颜色
    private int borderWidth = 0; // 边框宽度
    private int radius = 0; // 圆角弧度
    private int shapeType = ShapeType.NONE; // 图片类型（圆形, 矩形）

    private Paint pressedPaint; // 按下的画笔
    private float pressedAlpha = 0.1f; // 按下的透明度
    private int pressedColor = 0x1A000000; // 按下的颜色

    @IntDef({ShapeType.NONE, ShapeType.RECTANGLE, ShapeType.CIRCLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShapeType {
        int NONE = 0;
        int RECTANGLE = 1;
        int CIRCLE = 2;
    }

    public ShapeImageView(Context context) {
        this(context, null);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageViewStyle);
            borderWidth = array.getDimensionPixelOffset(R.styleable.ShapeImageViewStyle_siv_border_width, borderWidth);
            borderColor = array.getColor(R.styleable.ShapeImageViewStyle_siv_border_color, borderColor);
            radius = array.getDimensionPixelOffset(R.styleable.ShapeImageViewStyle_siv_radius, radius);
            pressedAlpha = array.getFloat(R.styleable.ShapeImageViewStyle_siv_pressed_alpha, pressedAlpha);
            if (pressedAlpha > 1) pressedAlpha = 1;
            pressedColor = array.getColor(R.styleable.ShapeImageViewStyle_siv_pressed_color, pressedColor);
            shapeType = array.getInteger(R.styleable.ShapeImageViewStyle_siv_shape_type, shapeType);
            array.recycle();
        }

        setClickable(shapeType != ShapeType.NONE);
        initPressedPaint();
        setDrawingCacheEnabled(true);
        setWillNotDraw(false);
    }

    // 初始化按下的画笔
    private void initPressedPaint() {
        pressedPaint = new Paint();
        pressedPaint.setAntiAlias(true);
        pressedPaint.setStyle(Paint.Style.FILL);
        pressedPaint.setColor(pressedColor);
        pressedPaint.setAlpha(0);
        pressedPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (shapeType == ShapeType.NONE) {
            super.onDraw(canvas);
            return;
        }

        if (getDrawable() == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        drawDrawable(canvas, getBitmapFromDrawable(getDrawable()));
        drawBorder(canvas);
        drawPressed(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    // 绘制圆角
    private void drawDrawable(Canvas canvas, Bitmap bitmap) {
        Paint paint = new Paint();
        paint.setColor(0xffffffff);
        paint.setAntiAlias(true);

        int saveFlags = Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG
                | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG;

        canvas.saveLayer(0, 0, width, height, null, saveFlags);

        if (shapeType == ShapeType.RECTANGLE) {
            RectF rectf = new RectF(borderWidth / 2, borderWidth / 2, getWidth() - borderWidth / 2, getHeight() - borderWidth / 2);
            canvas.drawRoundRect(rectf, radius, radius, paint);
        } else {
            canvas.drawCircle(width / 2, height / 2, width / 2 - borderWidth, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)); // SRC_IN 只显示两层图像交集部分的上层图像

        //Bitmap缩放
        float scaleWidth = ((float) getWidth()) / bitmap.getWidth();
        float scaleHeight = ((float) getHeight()) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.restore();
    }

    // 绘制边框
    private void drawBorder(Canvas canvas) {
        if (borderWidth > 0) {
            Paint paint = new Paint();
            paint.setStrokeWidth(borderWidth);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(borderColor);
            paint.setAntiAlias(true);
            if (shapeType == ShapeType.RECTANGLE) {
                RectF rectf = new RectF(borderWidth / 2, borderWidth / 2, getWidth() - borderWidth / 2, getHeight() - borderWidth / 2);
                canvas.drawRoundRect(rectf, radius, radius, paint);
            } else {
                canvas.drawCircle(width / 2, height / 2, (width - borderWidth) / 2, paint);
            }
        }
    }

    // 绘制按下效果
    private void drawPressed(Canvas canvas) {
        if (shapeType == ShapeType.RECTANGLE) {
            RectF rectf = new RectF(1, 1, width - 1, height - 1);
            canvas.drawRoundRect(rectf, radius, radius, pressedPaint);
        } else {
            canvas.drawCircle(width / 2, height / 2, width / 2, pressedPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressedPaint.setAlpha((int) (pressedAlpha * 255));
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                pressedPaint.setAlpha(0);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                pressedPaint.setAlpha(0);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    // 获取Bitmap内容
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        try {
            Bitmap bitmap;
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    // 设置边框颜色
    public void setBorderColor(@ColorRes int id) {
        this.borderColor = getResources().getColor(id);
        invalidate();
    }

    // 设置边框宽度
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = Util.dip2px(getContext(), borderWidth);
        invalidate();
    }

    // 设置图片按下颜色透明度
    public void setPressedAlpha(float pressAlpha) {
        this.pressedAlpha = pressAlpha;
    }

    // 设置图片按下的颜色
    public void setPressedColor(@ColorRes int id) {
        this.pressedColor = getResources().getColor(id);
        pressedPaint.setColor(pressedColor);
        pressedPaint.setAlpha(0);
        invalidate();
    }

    // 设置圆角半径
    public void setRadius(int radius) {
        this.radius = Util.dip2px(getContext(), radius);
        invalidate();
    }

    // 设置形状类型
    public void setShapeType(@ShapeType int shapeType) {
        this.shapeType = shapeType;
        setClickable(shapeType != ShapeType.NONE);
        invalidate();
    }
}
