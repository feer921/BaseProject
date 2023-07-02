package common.base.mvx.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import java.lang.Exception

/**
 * ******************(^_^)***********************<br>
 * @author fee
 * <P>DESC:
 * [ViewModel]提供者的工厂
 * </p>
 * ******************(^_^)***********************
 */
object ViewModelsProviderFactory {
//    /**
//     * 因为本类为单例，所以这个如果缓存起来的话，应该是可以全局(当前Activity、Fragment)复用的
//     * 但也由于本类为单例，却不适合 缓存短生命周期的该ViewModelProvider
//     */
//    private var cachedViewModelProvider: ViewModelProvider? = null
    private val instanceViewModelFactory = ViewModelProvider.NewInstanceFactory()


    private var instanceAndroidViewModelFactory: ViewModelProvider.AndroidViewModelFactory? = null


    fun getCustomAndroidViewModelFactory(application: Application):ViewModelProvider.AndroidViewModelFactory?{
        if (instanceAndroidViewModelFactory == null) {
            instanceAndroidViewModelFactory =
                object : ViewModelProvider.AndroidViewModelFactory(application) {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        try {
                           return modelClass.getConstructor(Application::class.java)
                                .newInstance(application)
                        }catch (ignore: Exception){

                        }
                        return super.create(modelClass)
                    }
//                    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                        try {
//                            return modelClass.getConstructor(Application::class.java)
//                                .newInstance(application)
//                        } catch (ex: Exception) {
//                        }
//                        return super.create(modelClass)
//                    }
                }
        }
        return instanceAndroidViewModelFactory
    }

//    fun <VM : ViewModel> getViewModel(viewModelStoreOwner: ViewModelStoreOwner, viewModelFactory: ViewModelProvider.Factory? = null, viewModelKey: String?, viewModelClass: Class<VM>, gainFromCache: Boolean = false): VM {
//        val theViewModelProvider = if(gainFromCache){
//            if (cachedViewModelProvider == null) {
//                cachedViewModelProvider = ViewModelProvider(viewModelStoreOwner, viewModelFactory
//                        ?: instanceViewModelFactory)
//            }
//            cachedViewModelProvider!!
//        }else{
//            ViewModelProvider(viewModelStoreOwner, viewModelFactory ?: instanceViewModelFactory)
//        }
//        return if(viewModelKey!= null){
//            theViewModelProvider.get(viewModelKey, viewModelClass)
//        }
//        else{
//            theViewModelProvider.get(viewModelClass)
//        }
//    }

    /**
     * @param viewModelStoreOwner [ViewModelStoreOwner]会在 Activity或者Fragment 销毁时，清除掉 map中缓存的 ViewModels
     * @param viewModelClass 要获取出的 [ViewModel] 的Class
     */
    fun <VM : ViewModel> getViewModel(
        viewModelStoreOwner: ViewModelStoreOwner,
        viewModelClass: Class<VM>,
        viewModelFactory: ViewModelProvider.Factory? = null,
        viewModelKey: String? = null
    ): VM {
        val theViewModelProvider = ViewModelProvider(
            viewModelStoreOwner, viewModelFactory
                ?: instanceViewModelFactory
        )
        return if (viewModelKey != null) {
            theViewModelProvider.get(viewModelKey, viewModelClass)
        } else {
            theViewModelProvider.get(viewModelClass)
        }
    }

    /**
     * 获取 具有 和 [Application]同生命周期级别的 ViewModel？？
     *
     */
    fun <VM : ViewModel> getViewModelOfApplicationLevel(
        viewModelStoreOwner: ViewModelStoreOwner,
        viewModelClass: Class<VM>,
        application: Application,
        viewModelKey: String? = null
    ): VM {
        return getViewModel(
            viewModelStoreOwner,
            viewModelClass,
            getCustomAndroidViewModelFactory(application),
            viewModelKey
        )
    }
}