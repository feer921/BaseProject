package common.base.utils;

import android.content.Context;
import android.support.annotation.IdRes;
import android.text.TextUtils;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/6/7<br>
 * Time: 11:22<br>
 * <P>DESC:
 *  根据资源的名称获取Android资源id的工具类
 *  注：一般情况下，用不到，直接在代码里引用对应的资源id
 * </p>
 * ******************(^_^)***********************
 */
public class ResIdPeekUtil {

    private static final String RES_TYPE_ID = "id";
    private static final String RES_TYPE_STRING = "string";
    private static final String RES_TYPE_DRAWABLE = "drawable";
    private static final String RES_TYPE_LAYOUT = "layout";
    private static final String RES_TYPE_STYLE = "style";
    private static final String RES_TYPE_COLOR = "color";
    private static final String RES_TYPE_DIMEN = "dimen";
    private static final String RES_TYPE_ANIM = "anim";
    private static final String RES_TYPE_MENU = "menu";


    public static @IdRes int getId(Context context, String idResName) {
        return getResId(context, idResName, RES_TYPE_ID);
    }

    public static @IdRes int getStringResId(Context context, String stringResName) {
        return getResId(context, stringResName, RES_TYPE_STRING);
    }

    public static @IdRes int getDrawableResId(Context context, String drawableResName) {
        return getResId(context, drawableResName, RES_TYPE_DRAWABLE);
    }

    public static @IdRes int getLayoutResId(Context context, String layoutResName) {
        return getResId(context, layoutResName, RES_TYPE_LAYOUT);
    }

    public static @IdRes int getStyleResId(Context context, String typeResName) {
        return getResId(context, typeResName, RES_TYPE_STYLE);
    }

    public static @IdRes int getColorResId(Context context, String colorResName) {
        return getResId(context, colorResName, RES_TYPE_COLOR);
    }

    public static @IdRes int getDimenResId(Context context, String dimenResName) {
        return getResId(context, dimenResName, RES_TYPE_DIMEN);
    }

    public static @IdRes int getAnimResId(Context context, String animResName) {
        return getResId(context, animResName, RES_TYPE_ANIM);
    }

    public static @IdRes int getMenuResId(Context context, String menuResName) {
        return getResId(context, menuResName, RES_TYPE_MENU);
    }
    public static @IdRes int getResId(Context context,String resName,String resDefType){
        return getResId(context, resName, resDefType, context.getPackageName());
    }
    /**
     * 根据定义的资源的名称，来查找对应的资源的id(系统分配)
     * @param context context
     * @param resName 资源名称. eg.: "logo"
     * @param resDefType 资源类型. eg.: "drawable"
     * @param packageName 资源所在的app包名内,言外之意，理论上是可以拿到其他app的资源id的(如果知道对应的资源名称的话)
     * @return 资源名称所对应的资源Id
     */
    public static @IdRes int getResId(Context context, String resName, String resDefType, String packageName) {
        if (!TextUtils.isEmpty(resName)) {
           return context.getResources().getIdentifier(resName, resDefType, packageName);
        }
        return 0;
    }
}
