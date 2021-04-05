package common.base.mvx.vm

import android.app.Application
import common.base.mvx.m.IRepository

/**
 ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2021/4/4<br>
 * Time: 10:20<br>
 * <P>DESC:
 * 拥有和 Application 同生命周期的 ViewModel
 * </p>
 * ******************(^_^)***********************
 */

abstract class BaseViewModelWithAppContext<REPO:IRepository>(val application: Application) : BaseViewModelWithRepository<REPO>() {


    fun <App : Application> getApp(): App = application as App
}