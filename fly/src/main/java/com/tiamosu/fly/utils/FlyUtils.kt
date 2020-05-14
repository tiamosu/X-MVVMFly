@file:JvmName("FlyUtils")

package com.tiamosu.fly.utils

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.fragmentation.FlySupportFragment
import com.tiamosu.fly.base.IFlyApp
import com.tiamosu.fly.di.component.AppComponent
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

/**
 * @author tiamosu
 * @date 2020/2/28.
 */

/**
 * 用于判断懒加载对象是否已经初始化 例： this::okHttpBuilder.isInitialized()
 */
fun <T> KProperty0<T>.isInitialized(): Boolean {
    isAccessible = true
    return (getDelegate() as? Lazy<*>)?.isInitialized() ?: true
}

fun getAppComponent(): AppComponent {
    checkNotNull(Utils.getApp(), "%s cannot be null", Context::class.java.name)
    checkState(
        Utils.getApp() is IFlyApp,
        "%s must be implements %s",
        Utils.getApp().javaClass.name,
        IFlyApp::class.java.name
    )
    return (Utils.getApp() as IFlyApp).getAppComponent()
}

fun isPageVisible(owner: LifecycleOwner): Boolean {
    if (!AppUtils.isAppForeground()) return false
    return when (owner) {
        is FragmentActivity -> {
            ActivityUtils.getTopActivity() == owner
        }
        is FlySupportFragment -> {
            owner.isFlySupportVisible()
        }
        is Fragment -> {
            owner.isVisible
        }
        else -> false
    }
}