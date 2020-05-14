package com.tiamosu.fly.base

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.tiamosu.fly.base.delegate.FlyAppDelegate
import com.tiamosu.fly.base.delegate.IFlyAppLifecycles
import com.tiamosu.fly.di.component.AppComponent
import com.tiamosu.fly.utils.checkNotNull
import com.tiamosu.fly.utils.checkState

/**
 * @author tiamosu
 * @date 2018/7/2.
 */
open class BaseFlyApplication : Application(), IFlyApp, ViewModelStoreOwner {
    private var appDelegate: IFlyAppLifecycles? = null

    //可借助 Application 来管理一个应用级的 ViewModel，
    //实现全应用范围内的 生命周期安全 且 事件源可追溯的 视图控制器 事件通知。
    private val appViewModelStore by lazy { ViewModelStore() }
    private val factory by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        appDelegate = appDelegate ?: FlyAppDelegate(base)
        appDelegate?.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        appDelegate?.onCreate(this)
    }

    /**
     * 在模拟环境中程序终止时会被调用
     */
    override fun onTerminate() {
        super.onTerminate()
        appDelegate?.onTerminate(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        appDelegate?.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        appDelegate?.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        appDelegate?.onTrimMemory(level)
    }

    override fun getViewModelStore(): ViewModelStore {
        return appViewModelStore
    }

    fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(this, factory)
    }

    /**
     * 将 [AppComponent] 返回出去, 供其它地方使用,
     * [AppComponent] 接口中声明的方法所返回的实例, 在 [getAppComponent] 拿到对象后都可以直接使用
     *
     * @return [AppComponent]
     * @see [com.tiamosu.fly.utils.getAppComponent]
     */
    override fun getAppComponent(): AppComponent {
        checkNotNull(appDelegate, "%s cannot be null", IFlyAppLifecycles::class.java.name)
        checkState(
            appDelegate is IFlyApp,
            "%s must be implements %s",
            IFlyAppLifecycles::class.java.name,
            IFlyApp::class.java.name
        )
        return (appDelegate as IFlyApp).getAppComponent()
    }
}
