package common.base.mvx.v

import android.os.SystemClock
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import common.base.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 ******************(^_^)***********************<br>
 * Author: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2023/7/2<br>
 * Time: 19:55<br>
 * <P>DESC:
 * 对　UI 的一些通用扩展
 * </p>
 * ******************(^_^)***********************
 */

var theViewClickGapTs = 1500L

/**
 * 扩展 [View]的快速点击场景下的 判断是否为有效的点击　以防重复点击
 */
fun View?.isValidClick(theGapTs: Long = theViewClickGapTs): Boolean {
    if (null == this){
        return false
    }
    val tagId = R.id.view_double_click_tag_id
    val lastClickTime = getTag(tagId)
    val now = SystemClock.elapsedRealtime()
    if (lastClickTime == null){
        setTag(tagId,now)
    } else {
        lastClickTime as Long
        //两次点击的时间间隔小于限制的间隔时间，则认为最后一次点击为无效点击
        if (now - lastClickTime < theGapTs){
            return false
        }
        setTag(tagId,now)
    }
    return true
}

/**
 * 延迟初始化之：默认不进行　sync　的初始化
 */
fun <T> defNonSyncLazy(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE, initializer: () -> T) =
    lazy(mode = mode,initializer)


fun ViewModel.launch(block: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch { block() }
}