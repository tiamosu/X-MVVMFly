package com.tiamosu.fly.base.delegate

import android.app.Application
import android.content.Context
import android.content.res.Configuration

/**
 * 用于代理 [Application] 的生命周期
 *
 * @author tiamosu
 * @date 2018/9/14.
 */
interface IFlyAppLifecycles {

    fun attachBaseContext(context: Context)

    fun onCreate(application: Application)

    fun onTerminate(application: Application)

    fun onConfigurationChanged(configuration: Configuration)

    fun onLowMemory()

    fun onTrimMemory(level: Int)
}
