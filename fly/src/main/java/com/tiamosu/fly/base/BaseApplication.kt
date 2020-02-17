package com.tiamosu.fly.base

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.tiamosu.fly.base.delegate.AppDelegate
import com.tiamosu.fly.base.delegate.AppLifecycles
import com.tiamosu.fly.di.component.AppComponent
import com.tiamosu.fly.utils.Preconditions

/**
 * @author xia
 * @date 2018/7/2.
 */
open class BaseApplication : Application(), App {
    private var mAppDelegate: AppLifecycles? = null

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        mAppDelegate = mAppDelegate ?: AppDelegate(base)
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
            AppLifecycles::class.java.name
        )
        Preconditions.checkState(
            mAppDelegate is App,
            "%s must be implements %s",
            AppLifecycles::class.java.name,
            App::class.java.name
        )
        return (mAppDelegate as App).getAppComponent()
    }
}
