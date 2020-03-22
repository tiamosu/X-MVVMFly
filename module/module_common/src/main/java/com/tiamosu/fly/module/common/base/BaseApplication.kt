package com.tiamosu.fly.module.common.base

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.tiamosu.fly.base.BaseFlyApplication
import com.tiamosu.fly.utils.checkState

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
open class BaseApplication : BaseFlyApplication(), ViewModelStoreOwner {

    //可借助 Application 来管理一个应用级 的 SharedViewModel，
    //实现全应用范围内的 生命周期安全 且 事件源可追溯的 视图控制器 事件通知。
    private lateinit var appViewModelStore: ViewModelStore
    private var factory: ViewModelProvider.Factory? = null

    override fun onCreate() {
        super.onCreate()
        appViewModelStore = ViewModelStore()
    }

    override fun getViewModelStore(): ViewModelStore {
        return appViewModelStore
    }

    fun getAppViewModelProvider(activity: Activity): ViewModelProvider {
        val app = activity.applicationContext as BaseApplication
        return ViewModelProvider(app, app.getAppFactory(activity))
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        val application = checkApplication(activity)
        if (factory == null) {
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
        return factory!!
    }

    private fun checkApplication(activity: Activity): Application {
        checkState(
            activity.application != null,
            "Your activity/fragment is not yet attached to Application. " +
                    "You can't request ViewModel before onCreate call."
        )
        return activity.application
    }
}