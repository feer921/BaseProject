package common.base.animators;

import android.support.annotation.Keep;
import android.view.View;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/7/22<br>
 * Time: 20:25<br>
 * <P>DESC:
 * 可用于对 View的宽、高、中心点X，中心点Y进行属性动画的包装
 * 如果还要支持其他的属性动画，可扩展
 * </p>
 * ******************(^_^)***********************
 */
public class ViewWrapper {

    private View delegateView;

    public ViewWrapper(View theRealView) {
        this.delegateView = theRealView;
    }

    @Keep
    public int getWidth() {
        return delegateView.getLayoutParams().width;
    }

    @Keep
    public void setWidth(int width) {
        delegateView.getLayoutParams().width = width;
        delegateView.requestLayout();
    }

    @Keep
    public int getHeight() {
        return delegateView.getLayoutParams().height;
    }

    @Keep
    public void setHeight(int height) {
        delegateView.getLayoutParams().height = height;
        delegateView.requestLayout();
    }

    @Keep
    public void setCoordX(float x) {
        delegateView.setX(x);
    }

    @Keep
    public float getScaleX() {
        return delegateView.getScaleX();
    }

    @Keep
    public void setScaleX(float scaleX) {
        delegateView.setScaleX(scaleX);
    }

    @Keep
    public float getScaleY() {
        return delegateView.getScaleY();
    }

    @Keep
    public void setScaleY(float scaleY) {
        delegateView.setScaleY(scaleY);
    }

    @Keep
    public float getCoordX() {
        return delegateView.getX();
    }

    @Keep
    public void setCoordY(float y) {
        delegateView.setY(y);
    }

    @Keep
    public float getCoordY() {
        return delegateView.getY();
    }

    @Keep
    public void setTranslationY(float translationY) {
        delegateView.setTranslationY(translationY);
    }

    @Keep
    public float getTranslationY() {
        return delegateView.getTranslationY();
    }

    // 是因为，ObjectAnimator 使用反射来获取，所以可能在release版本时 优化打包流程时认为没有地方使用可能会把方法给remove掉
    @Keep
    public float getTranslationX() {
        return delegateView.getTranslationX();
    }

    @Keep
    public void setTranslationX(float translationX) {
        delegateView.setTranslationX(translationX);
    }

    @Keep
    public float getAlpha() {
        return delegateView.getAlpha();
    }

    @Keep
    public void setAlpha(float alpha) {
        delegateView.setAlpha(alpha);
    }

    @Keep
    public float getCenterX() {
        return delegateView.getPivotX();
    }

    @Keep
    public void setCenterX(float pivotX) {
        delegateView.setPivotX(pivotX);
    }

    @Keep
    public float getCenterY() {
        return delegateView.getPivotY();
    }

    @Keep
    public void setCenterY(float pivotY) {
        delegateView.setPivotY(pivotY);
    }

    @Keep
    public void resetXY() {
        delegateView.setTranslationX(0.0f);
        delegateView.setTranslationY(0.0f);
    }

}
