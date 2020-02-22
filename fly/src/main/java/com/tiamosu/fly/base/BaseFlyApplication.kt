package com.tiamosu.fly.base

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.tiamosu.fly.base.delegate.FlyAppDelegate
import com.tiamosu.fly.base.delegate.IFlyAppLifecycles
import com.tiamosu.fly.di.component.AppComponent
import com.tiamosu.fly.utils.Preconditions

/**
 * @author tiamosu
 * @date 2018/7/2.
 */
open class BaseFlyApplication : Application(), IFlyApp {
    private var mAppDelegate: IFlyAppLifecycles? = null

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        mAppDelegate = mAppDelegate ?: FlyAppDelegate(base)
        mAppDelegate?.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        mAppDelegate?.onCreate(this)
    }

    /**
     * 在模拟环境中程序终止时会被调用
     */
    override fun onTerminate() {
        super.onTerminate()
        mAppDelegate?.onTerminate(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mAppDelegate?.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mAppDelegate?.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        mAppDelegate?.onTrimMemory(level)
    }

    /**
     * 将 [AppComponent] 返回出去, 供其它地方使用,
     * [AppComponent] 接口中声明的方法所返回的实例, 在 [getAppComponent] 拿到对象后都可以直接使用
     *
     * @return [AppComponent]
     * @see [com.tiamosu.fly.utils.FlyUtils.getAppComponent]
     */
    override fun getAppComponent(): AppComponent {
        Preconditions.checkNotNull<Any>(
            mAppDelegate,
            "%s cannot be null",
            IFlyAppLifecycles::class.java.name
        )
        Preconditions.checkState(
            mAppDelegate is IFlyApp,
            "%s must be implements %s",
            IFlyAppLifecycles::class.java.name,
            IFlyApp::class.java.name
        )
        return (mAppDelegate as IFlyApp).getAppComponent()
    }
}
