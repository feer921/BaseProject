package common.base.mvx.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

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
    fun <VM : ViewModel> getViewModel(viewModelStoreOwner: ViewModelStoreOwner, viewModelClass: Class<VM>, viewModelFactory: ViewModelProvider.Factory? = null, viewModelKey: String? = null): VM {
        val theViewModelProvider = ViewModelProvider(viewModelStoreOwner, viewModelFactory
                ?: instanceViewModelFactory)
        return if (viewModelKey != null) {
            theViewModelProvider.get(viewModelKey, viewModelClass)
        } else {
            theViewModelProvider.get(viewModelClass)
        }
    }

}