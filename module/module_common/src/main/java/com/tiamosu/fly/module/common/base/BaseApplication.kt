package com.tiamosu.fly.module.common.base

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.tiamosu.fly.base.BaseFlyApplication

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
open class BaseApplication : BaseFlyApplication(), ViewModelStoreOwner {

    //可借助 Application 来管理一个应用级 的 SharedViewModel，
    //实现全应用范围内的 生命周期安全 且 事件源可追溯的 视图控制器 事件通知。
    private val appViewModelStore by lazy { ViewModelStore() }
    private val factory by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
    }

    override fun getViewModelStore(): ViewModelStore {
        return appViewModelStore
    }

    fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(this, factory)
    }
}