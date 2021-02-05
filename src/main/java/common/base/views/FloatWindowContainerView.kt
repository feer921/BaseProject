package common.base.views

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import common.base.utils.CommonLog
import kotlin.math.abs

/**
 * @author fee
 * <P> DESC:
 * 通用可悬浮显示的 容器View，
 * 显示的具体内容由外部添加
 * </P>
 */
open class FloatWindowContainerView(context: Context) : FrameLayout(context), ValueAnimator.AnimatorUpdateListener {
    private var mContext = context
    private val TAG = "FloatWindowContainerView"

    /**
     * 当前屏幕的宽
     * 随着屏幕旋转会与之前的高对调
     */
    private var curScreenWidth: Int = 100

    /**
     * 当前屏幕的高
     * 随着屏幕旋转会与之前的宽对调
     */
    private var curScreenHeight: Int = 100

    protected var mWindowManager: WindowManager? = null

    /**
     * 悬浮布局的 Layout 参数
     * 该参数来决定悬浮的功能
     */
    private var mFloatLayoutParams: WindowManager.LayoutParams? = null

    /**
     * 当前是否已经在悬浮显示了
     * def = false
     */
    protected var isFloating = false

    init {
        mContext = context
    }

    /**
     * @step 1 初始化调用的显示 可以再自定义悬浮布局参数
     */
    fun floatShow(layoutParamConfig: WindowManager.LayoutParams.() -> Unit) {
        if (isFloating) {
            return
        }
        if (mFloatLayoutParams == null) {
            mFloatLayoutParams = WindowManager.LayoutParams()
            //默认的配置:
            mFloatLayoutParams?.run{
                type = if (Build.VERSION.SDK_INT >= 26) {//
        //                    Requires {@link android.Manifest.permission#SYSTEM_ALERT_WINDOW} permission.
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY // 如何申请权限？？？
//                    WindowManager.LayoutParams.LAST_APPLICATION_WINDOW
                } else{
                    WindowManager.LayoutParams.TYPE_PHONE //@deprecated for non-system apps. Use {@link #TYPE_APPLICATION_OVERLAY} instead.
                }
                format = PixelFormat.RGBA_8888
                flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                gravity = Gravity.START or  Gravity.TOP
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                x = 0
                y = 0
            }
        }
        mFloatLayoutParams?.layoutParamConfig()//调用处还可以再配置一遍，如果有自定义配置参数
        show()
    }

    fun show() {
        if (isFloating) {
            return
        }
        if (mFloatLayoutParams != null) {
            if (mWindowManager == null) {
                mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
                mWindowManager!!.defaultDisplay?.let {
                    val xyPoint = Point()
                    it.getRealSize(xyPoint)
//                    it.getSize(xyPoint)
                    curScreenWidth = xyPoint.x
                    curScreenHeight = xyPoint.y
                    CommonLog.e(TAG, "--> show() curScreenWidth = $curScreenWidth, curScreenHeight = $curScreenHeight")
                }
            }
            if (parent == null) {
                mWindowManager?.addView(this, mFloatLayoutParams)
                isFloating = true
            }
        }
    }

    fun dismiss() {
        if (isFloating && parent!= null) {
            mWindowManager?.removeViewImmediate(this)
            isFloating = false
        }
    }

    /**
     * 点击/触摸时，当前的屏幕上的X坐标
     * 用来计算手指滑动时 X轴方向的移动距离
     */
    private var theLastTouchX = 0f

    /**
     * 点击/触摸时，当前的屏幕上的 Y坐标
     * 用来计算手指滑动时 Y 轴方向的移动距离
     */
    private var theLastTouchY = 0f

    /**
     * 点击/触摸时，本View当前在屏幕上的 X位置
     * 用来更新 手指滑动时 本View移动到的目标 X位置
     */
    private var curLayoutX = 0

    /**
     * 点击/触摸时，本View当前在屏幕上的 Y轴位置
     * 用来更新 手指滑动时 本View移动到的目标 Y位置
     */
    private var curLayoutY = 0

    /**
     * 认为为 有效的滑动，则移动视图
     */
    private val theSnapDistance = 20

    /**
     * 是否需要 开启本视图可 触摸移动
     * def = false
     */
    private var isEnabledTouchMove = false

    /**
     * 当松开手时，是否需要自动根据当前所处的位置偏向吸附到屏幕边缘(左、右)
     * def = false
     */
    private var isEnabledAutoAdsorbEdge = false

    /**
     * 自动吸附到屏幕边缘的动画
     */
    private var anim4AutoAdsorbEdge: ValueAnimator? = null

    private val animDuration = 300L

    /**
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabledTouchMove || anim4AutoAdsorbEdge != null) {
            return super.onTouchEvent(event)
        }
        var isConsumeEvent = false
        if (event != null) {
            val touchX = event.x
            val touchRawX = event.rawX
            val touchY = event.y
            val touchRawY = event.rawY
            CommonLog.i(TAG, "--> onTouchEvent() action = ${MotionEvent.actionToString(event.action)} touchX = $touchX, touchRawX = $touchRawX, touchY = $touchY,touchRawY = $touchRawY,")
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {//按下
                    theLastTouchX = touchRawX
                    theLastTouchY = touchRawY
                    mFloatLayoutParams?.let {
                        curLayoutX = it.x
                        curLayoutY = it.y
                    }
                }
                MotionEvent.ACTION_MOVE -> {//移动
                    val moveXDistance = touchRawX - theLastTouchX
                    val moveYDistance = touchRawY - theLastTouchY
                    CommonLog.i(TAG, "--> ACTION_MOVE, moveXDistance = $moveXDistance, moveYDistance = $moveYDistance")
                    if (abs(moveXDistance) >= theSnapDistance || abs(moveYDistance) >= theSnapDistance) {
                        isConsumeEvent = true
                        mFloatLayoutParams?.let {
                            val theCanMoveToMaxX = curScreenWidth - measuredWidth//因为本View的宽可能会随着子VIew的改变而改变，所以变成动画计算了
                            val theCanMoveToMaxY = curScreenHeight - measuredHeight

                            var moveToTargetX = curLayoutX + moveXDistance
                            var moveToTargetY = curLayoutY + moveYDistance
                            CommonLog.e(TAG, "--> onTouchEvent() moveToTargetX = $moveToTargetX,moveToTargetY = $moveToTargetY, curLayoutX = $curLayoutX, curLayoutY = $curLayoutY"
                                    + " theCanMoveToMaxX = $theCanMoveToMaxX, theCanMoveToMaxY = $theCanMoveToMaxY")

                            if (moveToTargetX < 0) {
                                moveToTargetX = 0f
                            } else if (moveToTargetX > theCanMoveToMaxX) {
                                moveToTargetX = theCanMoveToMaxX.toFloat()
                            }

                            if (moveToTargetY < 0) {
                                moveToTargetY = 0f
                            } else if (moveToTargetY > theCanMoveToMaxY) {
                                moveToTargetY = theCanMoveToMaxY.toFloat()
                            }
                            it.x = moveToTargetX.toInt()
                            it.y = moveToTargetY.toInt()
//                            CommonLog.e(TAG, "--> onTouchEvent() moveToTargetX = $moveToTargetX,moveToTargetY = $moveToTargetY, layoutX = $layoutX, layoutY = $layoutY")
                            mWindowManager?.updateViewLayout(this, it)
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {//抬起
                    if (isEnabledAutoAdsorbEdge) {//开启了自动 吸附屏幕边缘的功能
                        mFloatLayoutParams?.let { lp ->
                            val curLayoutX = lp.x
                            CommonLog.i(TAG, "--> ACTION_UP curLayoutX = $curLayoutX, xOfView = $x")
                            anim4AutoAdsorbEdge = if (curLayoutX <= (curScreenWidth / 2)) {//本View 的顶点未超过屏幕宽的一半
                                if (curLayoutX != 0) {
                                    ValueAnimator.ofInt(curLayoutX, 0)
                                } else {//如果等于，则没必要动画移动吸附了
                                    null
                                }
                            } else {
                                val myMaxCanReachX = curScreenWidth - measuredWidth
                                if (curLayoutX != myMaxCanReachX) {
                                    ValueAnimator.ofInt(curLayoutX, myMaxCanReachX)
                                } else {//如果等于，则没必要动画移动吸附了
                                    null
                                }
                            }
                            anim4AutoAdsorbEdge?.duration = animDuration
                            anim4AutoAdsorbEdge?.addUpdateListener(this)
                            anim4AutoAdsorbEdge?.start()
                        }
                    }
                }
                else -> {
                }
            }
        }
        return isConsumeEvent || super.onTouchEvent(event)
    }

    companion object ConditionCheck{
        /**
         * 判断当前APP是否有可显示 悬浮窗的权限
         */
        fun isHasShowFloatWindowPermission(activity: Activity): Boolean {
            //As of API
            //     * level 23, an app cannot draw on top of other apps unless it declares the
            //     * {@link android.Manifest.permission#SYSTEM_ALERT_WINDOW} permission in its
            //     * manifest, <em>and</em> the user specifically grants the app this
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Android 6.0
                if (Settings.canDrawOverlays(activity)) {
                    return true
                }
            }
            return false
        }

        /**
         * 需要APP的AndroidManifest.xml中 声明 了权限:
         * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
         * 然后发起权限请求弹框让用户授予是否允许本APP的 悬浮窗的权限
         */
        fun tryToReqFloatWindowPermission(activity: Activity, reqCode: Int) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(activity)) {
                    activity.startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${activity.packageName}")), reqCode)
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        // TODO:  处理屏幕旋转
        CommonLog.e(TAG, "--> onConfigurationChanged() newConfig = $newConfig")
    }

    /**
     * 使能本视图是否开启 可滑动移动
     * @step 1
     */
    fun enableTouchMove(enable: Boolean) {
        this.isEnabledTouchMove = enable
    }

    /**
     * 使能当可滑动时，如果松开手指的情况下 是否需要自动动画的方式吸附到屏幕的两边缘
     * @step 2
     */
    fun enableAutoAdsorbEdge(enable: Boolean) {
        this.isEnabledAutoAdsorbEdge = enable
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        animation?.let {va->
            mFloatLayoutParams?.let {
                lp->
                lp.x = va.animatedValue as Int
                mWindowManager?.updateViewLayout(this, lp)
            }
            if (va.animatedFraction == 1F) {
                va.removeUpdateListener(this)
                anim4AutoAdsorbEdge = null
            }
        }
    }
}