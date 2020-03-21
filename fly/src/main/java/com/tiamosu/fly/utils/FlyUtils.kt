@file:JvmName("FlyUtils")

package com.tiamosu.fly.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.base.IFlyApp
import com.tiamosu.fly.di.component.AppComponent
import me.yokeyword.fragmentation.SupportFragment
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

/**
 * @author tiamosu
 * @date 2020/2/28.
 */
fun <T> KProperty0<T>.isInitialized(): Boolean {
    isAccessible = true
    return (getDelegate() as? Lazy<*>)?.isInitialized() ?: true
}

fun getAppComponent(): AppComponent {
    return (Utils.getApp() as IFlyApp).getAppComponent()
}

fun isPageVisible(owner: LifecycleOwner): Boolean {
    if (!AppUtils.isAppForeground()) return false
    return when (owner) {
        is FragmentActivity -> {
            ActivityUtils.getTopActivity() == owner
        }
        is SupportFragment -> {
            owner.isSupportVisible()
        }
        is Fragment -> {
            owner.isVisible
        }
        else -> false
    }
}