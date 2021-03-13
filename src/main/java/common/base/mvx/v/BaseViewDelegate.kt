package common.base.mvx.v

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import common.base.R
import common.base.utils.CheckUtil
import common.base.utils.CommonLog
import common.base.utils.ImageUtil
import common.base.views.OkToast


/**
 * @author fee
 * <P> DESC:
 * 通用的基础 的[V]视图层
 * </P>
 */
abstract class BaseViewDelegate(protected val mContext: Context) : IView, View.OnClickListener {

    protected val TAG = javaClass.simpleName

    protected var IS_LOG_DEBUG = false

    protected var rootView: View? = null

    /**
     * 通用默认实现： Toast
     * 注：该 Toast 兼容了当应用的通知权限被禁止的场景下也能显示出 Toast(内部Dialog实现)
     */
    protected var okToast: OkToast? = null

    /**
     * 当前View层可能需要查找出的各视图控件
     * 1、之所以使用 稀释数组缓存起来，是为了减少在调用[findViewById]方法获取View时减少遍历View树
     * 2、方便子类可以不声明各控件变量情况下，直接可多次使用[findView]来拿到想要的控件
     */
    protected var viewsCache: SparseArray<View>? = null

//    protected val appContext: Context = mContext.applicationContext

    protected var mViewModelStoreOwner: ViewModelStoreOwner? = null

    protected var mLifecycleOwner: LifecycleOwner? = null

    var isNeedCancelToastAtFinish = false

    /**
     * added by fee 2021-03-13: 由于本视图层是使用 [LayoutInflater] [inflate]出界面布局的方式，
     * 而如果 [container]外部没有指定容器View的Case 下，XML中根 View 所写的 布局参数并不会被保留
     * 造成开发者在 XML 中写的 边距等属性无效，所以这里统一处理，并可以让子类 自主配置
     * def = false 因为大多数情况下，我们在 XML中的根 View不会写 边距等属性
     */
    protected var isNeedKeepXmlRootViewParams = false
    /**
     * @param container 将要装载 本 视图层的容器 View; eg.: 当本视图层是提供给 [Fragment]时
     * @param savedInstanceState [Activity] 重绘时临时存储了状态数据的 对象
     * @return 本视图层最终创建的 视图 View
     */
    override fun onCreateView(container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val provideVLayoutRes = provideVLayoutRes()
        if (provideVLayoutRes != 0) {
            var theTempContainerView = container
            if (theTempContainerView == null && isNeedKeepXmlRootViewParams) {
                theTempContainerView = FrameLayout(mContext)
            }
            rootView = LayoutInflater.from(mContext).inflate(provideVLayoutRes, theTempContainerView, false)
        }
        return peekRootView()
    }

    /**
     * 本 接口View 的根View，可以提供给 [Activity] 或者 [Fragment]
     */
    override fun peekRootView(): View? = rootView

    var isEnableCachViews = true

    /**
     * 根据 指定的 View的 ID来查找对应的View
     * @param targetViewId View所声明的ID
     * @return null or View
     */
    override fun <V : View> findView(@IdRes targetViewId: Int): V {
        var findedView = viewsCache?.get(targetViewId)//首先在 缓存中找,目的是 减少 View 树的遍历
        if (findedView == null) {
            findedView = super.findView(targetViewId)
            if (isEnableCachViews && findedView != null) {
                if (viewsCache == null) {
                    viewsCache = SparseArray()
                }
                viewsCache?.put(targetViewId, findedView)
            }
        }
        return findedView as V//如果 findedView == null 会抛出异常
    }

    protected fun <V : View> view(@IdRes targetViewId: Int): V = findView(targetViewId)

    /**
     * 将dimen资源id,转换为系统中的px值
     * @param dimenResId 定义的dimen 资源 ID
     * @return px像素值
     */
    fun dimenResPxValue(@DimenRes dimenResId: Int): Int {
        return mContext.resources.getDimensionPixelSize(dimenResId)
    }

    fun getString(@StringRes strResId: Int): String {
        return mContext.getString(strResId)
    }

    //------ 关于 通用 Toast @start ---------
    protected fun needOkToast(initBlock: (OkToast) -> Unit) {
        if (okToast == null) {
            val toastTextPadding = dimenResPxValue(R.dimen.dp_10)
            okToast = OkToast.with(mContext)
                .withTextSizePixelValue(dimenResPxValue(R.dimen.sp_14).toFloat())
                .withTextPadding(
                    toastTextPadding,
                    toastTextPadding,
                    toastTextPadding,
                    toastTextPadding
                )
//                .withBackground()
            initBlock(okToast!!)
            extraInitOkToast()
        }
    }

    protected open fun extraInitOkToast() {
        //here do nothing
    }

    /**
     * 显示一个 居中 Toast，
     * 方法名以 toast前缀 便捷限定即为 Toast功能
     * 注：如果子类有不同的 Toast方案，重写之
     */
    open fun toastCenter(toastMsg: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
        if (!CheckUtil.isEmpty(toastMsg)) {
            needOkToast {
//                it.withBackground()
            }
            okToast?.withXYOffset(0, 0)
                ?.centerShow(toastMsg, duration)
        }
    }

    fun toastCenter(@StringRes toastMsgResId: Int) {
        toastCenter(getString(toastMsgResId))
    }

    open fun toastTop(toastMsg: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
        if (!CheckUtil.isEmpty(toastMsg)) {
            needOkToast { }
            okToast?.withXYOffset(0, 100)
                ?.topShow(toastMsg, duration)
        }
    }

    fun toastTop(@StringRes toastMsgResId: Int) {
        toastTop(getString(toastMsgResId))
    }

    open fun toastBottom(toastMsg: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
        if (!toastMsg.isNullOrBlank()) {
            needOkToast { }
            okToast?.withXYOffset(0, 0)
                ?.bottomShow(toastMsg, duration)
        }
    }

    fun toastBottom(@StringRes toastMsgResId: Int) {
        toastBottom(getString(toastMsgResId))
    }
    //------ 关于 通用 Toast @end -----------


    //------ 关于 通用 路由跳转 @start ---------

    open fun jumpToActivity(
        startIntent: Intent,
        startReqCode: Int,
        needFeedbackResult: Boolean = false
    ) {
        if (!needFeedbackResult) {
            mContext.startActivity(startIntent)
        } else {
            if (mContext is Activity) {
                mContext.startActivityForResult(startIntent, startReqCode)
            }
        }
    }

    fun jumpToActivity(
        targetActivityClass: Class<*>,
        reqCode: Int = 0,
        needFeedbackResult: Boolean = false
    ) {
        jumpToActivity(Intent(mContext, targetActivityClass), reqCode, needFeedbackResult)
    }

    fun jumpToActivity(
        targetActivityClass: Class<*>,
        reqCode: Int = 0,
        needFeedbackResult: Boolean = false,
        extraData: Intent.() -> Unit
    ) {
        val startIntent = Intent(mContext, targetActivityClass)
        startIntent.extraData()
        jumpToActivity(startIntent, reqCode, needFeedbackResult)
    }
    //------ 关于 通用 路由跳转 @end -----------


    //------- 关于 宿主生命周期 @start ----------
    @CallSuper
    override fun finish() {
        okToast?.cancelShow(isNeedCancelToastAtFinish)
        super.finish()
    }
    //------- 关于 宿主生命周期 @end   ----------

    //------- 关于 调试日志输出 @start ----------
    open fun i(logTag: String? = TAG, logMsg: String) {
        CommonLog.i(logTag ?: TAG, logMsg)
    }

    open fun e(logTag: String? = TAG, logMsg: String) {
        CommonLog.e(logTag ?: TAG, logMsg)
    }

    //------- 关于 调试日志输出 @end   ----------


    //------- 关于 通用提示性 Dialog @start ----------

    //------- 关于 通用提示性 Dialog @end   ----------


    //------- 关于 通用loading @start ----------

    //------- 关于 通用loading @end   ----------


    /**
     * 关联 宿主的 ViewModelStoreOwner
     */
    override fun attachViewModelStoreOwner(theViewModelStoreOwner: ViewModelStoreOwner?) {
        mViewModelStoreOwner = theViewModelStoreOwner
    }

    /**
     * 关联 宿主的 LifecycleOwner
     */
    override fun attachLifecycleOwner(theLifecycleOwner: LifecycleOwner?) {
        mLifecycleOwner = theLifecycleOwner
    }

    protected open fun <App : Application> peekAppInstance(): App {
        return mContext.applicationContext as App
    }

    fun <V : View> bindViewOnClickListener(
        @IdRes theViewID: Int,
        onClickListener: View.OnClickListener = this
    ): V {
        val theV: V = findView(theViewID)
        if (theV != null) {
            bindViewOnClickListener(theV,onClickListener)
        }
        return theV
    }

    fun bindViewOnClickListener(theView: View?, onClickListener: View.OnClickListener = this) {
        theView?.setOnClickListener(onClickListener)
    }

    override fun onClick(v: View?) {

    }

    open fun commonLoadImgData(
        @IdRes imgViewId: Int,
        imgUrl: String?,
        @DrawableRes defHoldImgRes: Int,
        useAppContext: Boolean
    ) {
        val ivImg: ImageView = findView(imgViewId)
        var useContext: Context? = null
        if (!useAppContext) {
            if (ivImg != null) {
                useContext = ivImg.context
            }
        }
        loadImgDataWithContext(useContext, ivImg, imgUrl, defHoldImgRes)
    }

    //------------ 通用加载图片 方法 @Start
    /**
     * 使用 Application 级的Context 加载图片
     * @param imgViewId
     * @param imgUrl
     * @param defHoldImgRes
     */
    open fun commonLoadImgData(
        @IdRes imgViewId: Int,
        imgUrl: String?,
        @DrawableRes defHoldImgRes: Int
    ) {
        val ivImg: ImageView = findView(imgViewId)
        commonLoadImgData(ivImg, imgUrl, defHoldImgRes)
    }

    open fun commonLoadImgData(
        imgView: ImageView?,
        imgUrl: String?,
        @DrawableRes defHoldImgRes: Int
    ) {
        loadImgDataWithContext(null, imgView, imgUrl, defHoldImgRes)
    }

    open fun loadImgDataWithContext(
        assignContext: Context?,
        imgView: ImageView?,
        imgUrl: String?,
        @DrawableRes defHoldImgRes: Int
    ) {
        if (imgView != null) {
            var willUseContext: Context? = assignContext
            if (willUseContext == null) {
                willUseContext = mContext.applicationContext
            }
            if (isEmpty(imgUrl)) {
                imgView.setImageResource(defHoldImgRes)
            } else {
                if (CheckUtil.isGifImage(imgUrl)) {
                    ImageUtil.loadGifModel(willUseContext, imgUrl, defHoldImgRes, imgView, 0)
                } else {
                    ImageUtil.loadImage(
                        willUseContext,
                        imgUrl,
                        defHoldImgRes,
                        defHoldImgRes,
                        imgView
                    )
                }
            }
        }
    }

    //------------ 通用加载图片 方法 @end
    open fun isEmpty(charSequence: CharSequence?): Boolean {
        return charSequence.isNullOrBlank()
    }

    fun peekContextAsActivity(): Activity? {
        return if (mContext is Activity) {
            mContext
        } else {
            null
        }
    }
}