package common.base.animators;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * USER: fee(1176610771@qq.com)
 * DATE: 2017/3/9
 * TIME: 17:01
 * DESC: 贝塞尔曲线 上轨迹 的估值点
 */

public class BezierPointValuer implements TypeEvaluator<PointF> {
    //贝塞尔曲线上的主要四个点中的中间的确定曲线的两个点
    private PointF point1;
    private PointF point2;

    public BezierPointValuer(PointF point1, PointF point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    /**
     * This function returns the result of linearly interpolating the start and end values, with
     * <code>fraction</code> representing the proportion between the start and end values. The
     * calculation is a simple parametric calculation: <code>result = x0 + t * (x1 - x0)</code>,
     * where <code>x0</code> is <code>startValue</code>, <code>x1</code> is <code>endValue</code>,
     * and <code>t</code> is <code>fraction</code>.
     *
     * @param t  The fraction from the starting to the ending values
     * @param p0 The start value. 贝塞尔曲线的开始点
     * @param p3 The end value. 贝塞尔曲线的结束点这里为第三个点
     * @return A linear interpolation between the start and end values, given the
     * <code>fraction</code> parameter.
     * 贝塞尔曲线 公式：B(t) = P0*(1-t)^3 + 3*p1*t*(1-t)^2 + 3*p2*t^2*(1-t) + p3*t^3, t{0~1}
     */
    @Override
    public PointF evaluate(float t, PointF p0, PointF p3) {
        PointF transitionPoint = new PointF();
        transitionPoint.x = p0.x * (1 - t) * (1 - t) * (1 - t)
                + 3 * point1.x * t * (1 - t) * (1 - t) + 3 * point2.x * t * t * (1 - t)
                + p3.x * t * t * t;
        transitionPoint.y = p0.y * (1 - t) * (1 - t) * (1 - t)
                + 3 * point1.y * t * (1 - t) * (1 - t) + 3 * point2.y * t * t * (1 - t)
                + p3.y * t * t * t;

        return transitionPoint;
    }
}
