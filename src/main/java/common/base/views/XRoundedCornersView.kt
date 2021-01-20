package common.base.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout
import common.base.R

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2020/10/20<br>
 * Time: 15:56<br>
 * <P>DESC:
 * 可以指定 左上，左下、右上、右下为圆角的自定义容器View
 * </p>
 * ******************(^_^)***********************
 */
open class XRoundedCornersView : FrameLayout {

    /**
     * 左上 圆角半径值：
     * 单位：PX
     */
    private var leftTopCornerRadius: Float = 0f

    /**
     * 左下 圆角半径值：
     */
    private var leftBottomCornerRadius: Float = 0f

    /**
     * 右上圆角半径值：
     */
    private var rightTopCornerRadius: Float = 0f

    /**
     * 右下圆角半径值：
     */
    private var rightBottomCornerRadius: Float = 0f

    /**
     * 每个 角的 圆角半径，如果设置了，则每个角的半径统一一致
     */
    private var perCornerRadius: Float = 0f

    private val mPath = Path()


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

    constructor(context: Context, attributes: AttributeSet?, defStyle: Int) : super(context, attributes, defStyle) {
        val obtainStyledAttributes = context.obtainStyledAttributes(attributes, R.styleable.XRoundedCornersView)
        perCornerRadius = obtainStyledAttributes.getDimension(R.styleable.XRoundedCornersView_perCornerRadius, 0f)
        if (perCornerRadius > 0) {
            leftTopCornerRadius = perCornerRadius
            leftBottomCornerRadius = perCornerRadius
            rightTopCornerRadius = perCornerRadius
            rightBottomCornerRadius = perCornerRadius
        } else {
            leftTopCornerRadius = obtainStyledAttributes.getDimension(R.styleable.XRoundedCornersView_leftTopCornerRadius, 0f)
            leftBottomCornerRadius = obtainStyledAttributes.getDimension(R.styleable.XRoundedCornersView_leftBottomCornerRadius, 0f)
            rightTopCornerRadius = obtainStyledAttributes.getDimension(R.styleable.XRoundedCornersView_rightTopCornerRadius, 0f)
            rightBottomCornerRadius = obtainStyledAttributes.getDimension(R.styleable.XRoundedCornersView_rightBottomCornerRadius, 0f)
        }
        obtainStyledAttributes.recycle()
    }


    override fun dispatchDraw(canvas: Canvas?) {
        val array = floatArrayOf(leftTopCornerRadius,
                leftTopCornerRadius,
                rightTopCornerRadius,
                rightTopCornerRadius,
                rightBottomCornerRadius,
                rightBottomCornerRadius,
                leftBottomCornerRadius,
                leftBottomCornerRadius);
        mPath.addRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), array, Path.Direction.CW)
//        canvas?.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas?.clipPath(mPath)
        super.dispatchDraw(canvas)
    }

    /**
     * 优化子控件点击体验
     */
    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

}