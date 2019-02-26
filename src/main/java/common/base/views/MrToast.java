package common.base.views;

import android.content.Context;
import android.widget.Toast;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/2/26<br>
 * Time: 14:38<br>
 * <P>DESC:
 * 可全局使用的Toast
 * </p>
 * ******************(^_^)***********************
 */
public class MrToast {

    private MrToast() {

    }
    private static OkToast globalToast = null;
    public static void centerToast(CharSequence toastText,int duration) {
        if (globalToast != null) {
            globalToast.centerShow(toastText,duration);
        }
    }

    public static void centerToast(CharSequence toastText) {
        centerToast(toastText, 0);
    }

    public static void centerLongToast(CharSequence toastText) {
        centerToast(toastText,Toast.LENGTH_LONG);
    }

    public static void configGlobalOkToast(OkToast okToast) {
        globalToast = okToast;
    }

    public static void toast(CharSequence toastText, int duration, int showGravity, int xOffset, int yOffset) {
        if (globalToast != null) {
            globalToast.show(toastText, duration, showGravity, xOffset, yOffset);
        }
    }

    public static void bottomToast(CharSequence toastText) {
        if (globalToast != null) {
            globalToast.bottomShow(toastText);
        }
    }

    public static void topToast(CharSequence toastText, int duration) {
        if (globalToast != null) {
            globalToast.topShow(toastText,duration);
        }
    }

    public static void cancelToast(boolean cancelToast, boolean releastGlobalToast) {
        if (globalToast != null) {
            globalToast.cancelShow(cancelToast);
        }
        if (releastGlobalToast) {
            globalToast = null;
        }
    }

    public static OkToast initGlobalOkToast(Context context) {
        if (globalToast == null) {
            globalToast = OkToast.with(context);
        }
        return globalToast;
    }
}
