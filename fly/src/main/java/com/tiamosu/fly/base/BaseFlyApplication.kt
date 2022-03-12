package com.tiamosu.fly.base

import android.content.Context
import android.content.res.Configuration
import com.tiamosu.fly.base.delegate.FlyAppDelegate
import com.tiamosu.fly.base.delegate.IFlyAppLifecycles
import com.tiamosu.fly.di.component.AppComponent
import com.tiamosu.navigation.FlyApplication

/**
 * @author tiamosu
 * @date 2018/7/2.
 */
open class BaseFlyApplication : FlyApplication(), IFlyApp {
    private var appDelegate: IFlyAppLifecycles? = null

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

    /**
     * 将 [AppComponent] 返回出去, 供其它地方使用,
     * [AppComponent] 接口中声明的方法所返回的实例, 在 [getAppComponent] 拿到对象后都可以直接使用
     *
     * @return [AppComponent]
     * @see [com.tiamosu.fly.utils.getAppComponent]
     */
    override fun getAppComponent(): AppComponent {
        return checkNotNull((appDelegate as? IFlyApp)?.getAppComponent()) {
            "${IFlyAppLifecycles::class.java.name} must be implements ${IFlyApp::class.java.name}"
        }
    }
}
