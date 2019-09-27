package common.base.utils;

import android.view.View;
import android.widget.TextView;

import com.android.xselector.XSelector;
import com.android.xselector.selector.ColorSelector;
import com.android.xselector.selector.ShapeSelector;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/9/19<br>
 * Time: 17:28<br>
 * <P>DESC:
 * 给View视图控件配置各种状态下的背景等工具类
 * 这样的话，就不用在/drawable/  资源文件夹下写shape xml文件
 * </p>
 * ******************(^_^)***********************
 */
public class SelectorUtil {

    /**
     * 给一个View设置各种状态下的背景颜色
     * 替代drawable下的selector.xml文件
     * 颜色值的格式为 "#AARRGGBB"的样式
     * @param viewToConfig 要配置的View
     * @param radiusPx 圆角半径像素值 =0为矩形；注意是像素值
     * @param defBgColor 默认背景颜色
     * @param disableBgColor  不能操作时的背景颜色
     * @param pressedBgColor 点击时的背景颜色
     * @param focusBgColor 获得焦点时的背景颜色，一般不需要
     */
    public static void shapeSelector(View viewToConfig, int radiusPx, String defBgColor,
                                     String disableBgColor, String pressedBgColor,
                                     String focusBgColor){

        shapeSelector(radiusPx,defBgColor,disableBgColor,pressedBgColor,focusBgColor)
                .into(viewToConfig);
    }

    public static ShapeSelector shapeSelector(int radiusPx, String defBgColor,
                                String disableBgColor, String pressedBgColor,
                                String focusBgColor){
        ShapeSelector shapeSelector = XSelector.shapeSelector();
        if (radiusPx > 0) {
            shapeSelector.radius(radiusPx);
        }
        if (!CheckUtil.isEmpty(defBgColor)) {
            shapeSelector.defaultBgColor(defBgColor);
        }
        if (!CheckUtil.isEmpty(disableBgColor)) {
            shapeSelector.disabledBgColor(disableBgColor);
        }
        if (!CheckUtil.isEmpty(pressedBgColor)) {
            shapeSelector.pressedBgColor(pressedBgColor);
        }
        if (!CheckUtil.isEmpty(focusBgColor)) {
            shapeSelector.focusedBgColor(focusBgColor);
        }
        return shapeSelector;
    }

    /**
     * 给一个TextView设置各种状态下的字体颜色
     * 替代drawable下的selector.xml文件
     * 颜色值的格式为 "#AARRGGBB"的样式
     * @param viewToConfig 要配置的View
     * @param defColor 默认字体颜色
     * @param disableColor  不能操作时的字体颜色
     * @param pressedColor 点击时的字体颜色
     * @param focusColor 获得焦点时的字体颜色，一般不需要
     */
    public static void colorSelector(TextView viewToConfig, String defColor,
                                     String disableColor, String pressedColor,
                                     String focusColor){
        ColorSelector colorSelector = colorSelector(defColor, disableColor, pressedColor, focusColor);
        colorSelector.into(viewToConfig);
    }

    public static ColorSelector colorSelector(String defColor,
                                String disableColor, String pressedColor,
                                String focusColor) {

        ColorSelector colorSelector = XSelector.colorSelector();
        if (!CheckUtil.isEmpty(defColor)) {
            colorSelector.defaultColor(defColor);
        }
        if (!CheckUtil.isEmpty(disableColor)) {
            colorSelector.disabledColor(disableColor);
        }
        if (!CheckUtil.isEmpty(pressedColor)) {
            colorSelector.pressedColor(pressedColor);
        }
        if (!CheckUtil.isEmpty(focusColor)) {
            colorSelector.focusedColor(focusColor);
        }
        return colorSelector;
    }
}
