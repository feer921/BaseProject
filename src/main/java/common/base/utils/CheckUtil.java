package common.base.utils;

import android.os.Build;

import java.util.Collection;
import java.util.Map;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/1/19<br>
 * Time: 17:44<br>
 * <P>DESC:
 * 简单的只作检查的工具类
 * </p>
 * ******************(^_^)***********************
 */
public class CheckUtil {
    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        return isEmpty(str, false);
    }

    public static boolean isEmpty(CharSequence str, boolean careNullText) {
        if (
                str == null
                        ||
                        str.length() == 0
                        ||
                        str.toString().trim().length() == 0
                        ||
                        (careNullText && "null".equalsIgnoreCase(str.toString()))
        )
        {
            return true;
        }
        return false;
    }
    /**
     * 是否向下兼容某个系统版本
     * @param apiLevle 要兼容的某个版本
     * @return true:兼容，即当前系统版本大于或者等于目标要兼容的版本；false：不兼容，即当前系统版本小于目标版本
     */
    public static boolean isCompateApi(int apiLevle){
        if(apiLevle <= 0) return  false;
        if(apiLevle <= Build.VERSION.SDK_INT){
            return true;
        }
        return false;
    }

    public static boolean isNullObj(Object theObj) {
        return theObj == null;
    }

    public static boolean isEmpty(Collection collection) {
        return isNullObj(collection) || collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return isNullObj(map) || map.isEmpty();
    }

    public static boolean isEmpty(Object[] objArray) {
        return isNullObj(objArray) || objArray.length == 0;
    }
}
