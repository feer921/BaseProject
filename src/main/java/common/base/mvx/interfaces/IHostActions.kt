package common.base.mvx.interfaces

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner

/**
 * @author fee
 * <P> DESC:
 * 宿主相关的 动作
 * </P>
 */
interface IHostActions {

    /**
     * 当系统的相关配置 eg.: 字体大小变更、方向变更、等，有变更时的回调
     */
    fun onConfigurationChanged(newConfig: Configuration?){

    }

    /**
     * 当宿主 接收到用户按下 [返回键]时，视图层是否需要消费
     * def: false 不消费，直接让宿主消费
     * @return true: 视图层需要消费掉 [返回键]事件，则宿主不消费; false:本视图层不消费 [返回键]事件
     */
    fun onConsumeBackPressed(): Boolean = false

    /**
     * 当宿主 有数据从其他界面 响应返回时的回调
     *
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    /**
     * 当宿主 为当前/恢复可见/前台 状态时
     */
    fun onResume() {

    }

    /**
     * 宿主的 生命周期 [onPause] 回调
     */
    fun onPause(){

    }
    /**
     * 当宿主 将处于后台状态时
     */
    fun onStop() {

    }

    /**
     * 当宿主 将要处于后台时，需要临时存储相关状态、数据的回调
     */
    fun onSaveInstanceState(outState: Bundle?) {

    }

    /**
     * 当宿主将要销毁时回调
     * 注：需要 视图层作释放相关逻辑
     */
    fun onHostFinish() {

    }

    /**
     * 当宿主处于销毁流程阶段时回调
     */
    fun onDetach() {

    }

    /**
     * 关联 宿主的 ViewModelStoreOwner
     */
    fun attachViewModelStoreOwner(theViewModelStoreOwner: ViewModelStoreOwner?)

    /**
     * 关联 宿主的 LifecycleOwner
     */
    fun attachLifecycleOwner(theLifecycleOwner: LifecycleOwner?)
}