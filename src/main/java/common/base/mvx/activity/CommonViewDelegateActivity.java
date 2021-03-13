package common.base.mvx.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;

import common.base.StarterIntent;
import common.base.mvx.v.BaseViewDelegate;
import common.base.mvx.v.IView;

/**
 * *****************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2021/3/6<br>
 * Time: 14:05<br>
 * <P>DESC:
 * 通用的 视图代理 Activity
 * 外部只需要在[Intent]中传入对应的[IView]对象即可
 * 有了该通用Activity，项目中可以只写 [ViewDelegate],就共用本Activity
 * </p>
 * ******************(^_^)***********************
 */
public class CommonViewDelegateActivity extends BaseViewDelegateActivity2<IView> {

    protected static final String INTENT_KEY_REQ_SCREEN_ORIENTATION = "intent.key.screen_orientation";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent startIntent = getIntent();
        if (startIntent != null) {
            if (startIntent.hasExtra(INTENT_KEY_REQ_SCREEN_ORIENTATION)) {
                int reqRientation = startIntent.getIntExtra(INTENT_KEY_REQ_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setRequestedOrientation(reqRientation);
            }
        }
        super.onCreate(savedInstanceState);
    }

    public static <V extends BaseViewDelegate> void load(Context context, Class<V> viewDelegateClass/*, Bundle bundleOptions*/) {
        if (viewDelegateClass != null) {
            Intent startIntent = buildStartIntent(context, viewDelegateClass);
            boolean isActivityContext = context instanceof Activity;
            if (!isActivityContext /*&& context instanceof Application*/) {
                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(startIntent);
        }
    }

    public static <V extends BaseViewDelegate> void load(Context context, Class<V> viewDelegateClass,/*, Bundle bundleOptions*/boolean isReqScreenPortraitOrLandscape) {
        if (viewDelegateClass != null) {
            Intent startIntent = buildStartIntent(context, viewDelegateClass);
            startIntent.putExtra(INTENT_KEY_REQ_SCREEN_ORIENTATION, isReqScreenPortraitOrLandscape ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            boolean isActivityContext = context instanceof Activity;
            if (!isActivityContext /*&& context instanceof Application*/) {
                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(startIntent);
        }
    }

    public static <V extends BaseViewDelegate> StarterIntent buildStartIntent(Context context, Class<V> viewDelegateClass) {
       return new StarterIntent(context, CommonViewDelegateActivity.class).putExtra("viewDelegateClass", viewDelegateClass);
    }

    @Nullable
    @Override
    protected IView provideVModule() {
        Intent startIntent = getIntent();
        if (startIntent != null) {
            try {
                Class<IView> viewDelegateClass = (Class<IView>) startIntent.getSerializableExtra("viewDelegateClass");
                Constructor<IView> declaredConstructor = viewDelegateClass.getDeclaredConstructor(Context.class);
                return declaredConstructor.newInstance(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
