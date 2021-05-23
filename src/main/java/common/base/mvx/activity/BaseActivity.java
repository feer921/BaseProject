package common.base.mvx.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.DimenRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import common.base.R;
import common.base.utils.CommonLog;

/**
 * Author: fee
 * Date: 2016-05-16
 * Time: 15:21
 * DESC: 通用Activity基类
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected final String TAG = getClass().getSimpleName();
    protected boolean LIFE_CIRCLE_DEBUG = false;
    protected boolean PRINT_TASK_ID = false;

    protected Context appContext;
    protected Context mContext;

    /**
     * 会自动调用：
     * initViews();-->initData();
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onCreate() " + (PRINT_TASK_ID ? " taskId: " + getTaskId() : ""));
        }
        appContext = getApplicationContext();
        mContext = this;
        boolean needInitAuto = false;
        int subActivityContentViewResID = getProvideContentViewResID();
        if (subActivityContentViewResID > 0) {//子类有提供当前Activity的内容视图，则父类来调用初始化方法
            if (isNeedSetContentView()) {
                setContentView(subActivityContentViewResID);
            }
            needInitAuto = true;
        } else {
            View providedContentView = providedContentView(null,savedInstanceState);
            if (providedContentView != null) {
                if (isNeedSetContentView()) {
                    setContentView(providedContentView);// 注：这种填充布局的方式，不会把xml布局中根View的布局参数给保留(如：设置的左右marging属性)
                }
                needInitAuto = true;
            }
        }
        if (needInitAuto) {
            initViews();
            initData();
        }
    }

    /**
     * 提供的内容视图
     *
     * @return
     */
    protected View providedContentView(ViewGroup container,Bundle savedInstanceState) {
        return null;
    }

    /**
     * 获取当前Activity需要填充、展示的内容视图，如果各子类提供，则由基类来填充，如果不提供，各子类也可自行处理
     *
     * @return 当前Activity需要展示的内容视图资源ID
     */
    protected abstract int getProvideContentViewResID();

    /**
     * 是否需要 本基类 Activity，直接调用填充 当前的布局
     * 注：在使用[DataBinding] 的场景下，并不需要本基类 直接把 布局填充好
     * def: true
     * @return true:本基类 Activity会填充好视图布局；false:不调用Activity的 setContentView
     */
    protected boolean isNeedSetContentView(){
        return true;
    }
    /**
     * 如果子类在getProvideContentViewResID()方法提供了视图资源，那么子类的初始化视图可在此方法中完成
     */
    protected abstract void initViews();

    /**
     * 初始化数据
     */
    protected void initData() {

    }


    /**
     * 获取项目内Application级别的上下文
     *
     * @param <APP> 各项目自己的继承自Application的实例
     * @return 各项目自己的继承自Application的实例
     */
    protected <APP extends Application> APP getAppInstance() {
        return (APP) getApplication();
    }


    /**
     * Activity之间的切换转场动画，本基类使用普通动画，各子类可自行实现
     *
     * @param finishSelf
     */
    protected void switchActivity(boolean finishSelf) {
        if (finishSelf) {//关闭自身
            //被关闭的本Activity 往右方向整个渐出,返回到前台的Activity 从左方向短距离(部分的)的进来
            //                       //后台的Activity进场动画        //自己出场的动画(右出)
            overridePendingTransition(R.anim.common_whole_left_in, R.anim.common_whole_right_out);
        } else {
            //要启动的Activity完整的从右侧进来，而运行至后台的本Activity 部分地、短距离的从右侧类似的出
            //                        //要启动的Activity的进场动画     //自身的左出动画效果？？
            overridePendingTransition(R.anim.common_whole_right_in, R.anim.common_whole_left_out);
        }
    }

    /**
     * 在一个容器视图中依据View ID查找子视图
     *
     * @param containerView 容器View
     * @param childViewId   子View ID
     * @param <V>
     * @return
     */
    protected <V extends View> V findAviewInContainer(View containerView, int childViewId) {
        if (containerView == null || childViewId == 0) {
            return null;
        }
        return containerView.findViewById(childViewId);
    }

    protected void jumpToActivity(Intent startIntent, int requestCode, boolean needReturnResult) {
        if (startIntent != null) {
            if (!needReturnResult) {
                startActivity(startIntent);
            } else {
                startActivityForResult(startIntent, requestCode);
            }
        }
    }

    protected void jumpToActivity(Class<?> targetActivityClass) {
        Intent startIntent = new Intent(mContext, targetActivityClass);
        jumpToActivity(startIntent, 0, false);
    }

    protected void jumpToActivity(Class<?> targetActiviyClass, int requestCode, boolean needReturnResult) {
        Intent startIntent = new Intent(mContext, targetActiviyClass);
        jumpToActivity(startIntent, requestCode, needReturnResult);
    }

    protected void jumpToActivity(Class<?> targetActiviyClass, int requestCode, boolean needReturnResult, String[] intentKeys, String... keysValues) {
        Intent startIntent = new Intent(mContext, targetActiviyClass);
        if (intentKeys != null && keysValues != null) {
            int keysLen = intentKeys.length;
            if (keysLen > 0 && keysValues.length >= keysLen) {
                for (int i = 0; i < keysLen; i++) {
                    startIntent.putExtra(intentKeys[i], keysValues[i]);
                }
            }
        }
        jumpToActivity(startIntent, requestCode, needReturnResult);
    }

    //------------------------- 生命周期方法----------(我是不漂亮的分隔线)------------------
    @Override
    protected void onRestart() {
        super.onRestart();
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onRestart()");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onStart()");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onResume()");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "--> onNewIntent() intent = " + intent);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onRestoreInstanceState() savedInstanceState = " + savedInstanceState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onPause()");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onStop()");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onSaveInstanceState()");
        }
    }

    /**
     * 结束/finish()自已
     *
     * @param needTransitionAnim 是否需要过场动画
     */
    protected void finishSelf(boolean needTransitionAnim) {
        finish();
        if (needTransitionAnim) {
            switchActivity(true);
        }
    }

    /**
     * Activity生命周期是否经历了finish()
     */
    protected boolean isResumeFinish;

    /**
     * 注：该方法为主动调用方法，不在Activity的生命周期流程中
     * 则需要注意：如果Activity是自动结束(如，屏幕旋转等)的，因不会走finish()而导致在此方法内作的释放不执行
     */
    @CallSuper
    @Override
    public void finish() {
        isResumeFinish = true;
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> finish()");
        }
        //changed by fee 2018-10-18:这里屏蔽来自onDestroy()方法内的调用finish()时,不再调用super.finish()
        if (!isResumeDestroy) {
            super.finish();
        }
    }

    /**
     * Activity生命周期是否经历了onDestroy()
     */
    private boolean isResumeDestroy;

    @Override
    protected void onDestroy() {
        //added by fee 2018-10-18:解决Activity在自动销毁过程中,不走finish()而导致一些子Activity在finish()作释放功能没有执行的问题，或者不在本框架内处理
        isResumeDestroy = true;
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onDestroy()");
        }
        if (!isResumeFinish) {
            finish();
        }
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onActivityResult() requestCode = " + requestCode + " resultCode = " + resultCode + " data = " + data);
        }
    }

    @Override
    public void onBackPressed() {
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onBackPressed()");
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (LIFE_CIRCLE_DEBUG) {
            CommonLog.i(TAG, "---> onConfigurationChanged() newConfig = " + newConfig);
        }
    }

    protected boolean EXTRA_BORING_LOG_DEBUG = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (LIFE_CIRCLE_DEBUG && EXTRA_BORING_LOG_DEBUG) {
            CommonLog.i(TAG, "---> onWindowFocusChanged() hasFocus = " + hasFocus);
        }
    }
    //---------------------up up up 生命周期方法 up up up ----------(我是不漂亮的分隔线)---------------

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }


    /**
     * 基类提供普通Log输出之error级信息输出
     * 注意一点：因为第二个参数是可变参数，该方法允许只传一个参数eg.: e("")
     *
     * @param logTag  log的TAG，如果为null,会使用{@link #TAG}
     * @param logBody
     */
    protected void e(String logTag, Object... logBody) {
        CommonLog.e(null == logTag ? TAG : logTag, logBody);
    }

    /**
     * 基类提供普通Log输出之info级信息输出
     * 注意一点：因为第二个参数是可变参数，该方法允许只传一个参数eg.: i("")
     *
     * @param logTag  log的TAG，如果为null,会使用{@link #TAG}
     * @param logBody Log内的具体要打印的信息
     */
    protected void i(String logTag, Object... logBody) {
        CommonLog.i(null == logTag ? TAG : logTag, logBody);
    }

    protected void w(String logTag, Object... logBody) {
        CommonLog.w(null == logTag ? TAG : logTag, logBody);
    }

    public boolean onFragmentOptReq(Fragment curFragment, String reqOpt, Object reqData) {
        //here do nothing...
        return false;
    }

    /**
     * 将dimen资源id,转换为系统中的px值
     *
     * @param dimenResId 定义的dimen 资源 ID
     * @return px像素值
     */
    protected int dimenResPxValue(@DimenRes int dimenResId) {
        return getResources().getDimensionPixelSize(dimenResId);
    }
}
