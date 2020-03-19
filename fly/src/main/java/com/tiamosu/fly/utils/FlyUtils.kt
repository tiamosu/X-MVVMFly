@file:JvmName("FlyUtils")

package com.tiamosu.fly.utils

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.base.IFlyApp
import com.tiamosu.fly.di.component.AppComponent
import me.yokeyword.fragmentation.SupportActivity
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

fun getContext(owner: LifecycleOwner): Context? {
    return when (owner) {
        is SupportFragment -> {
            owner.context
        }
        is Fragment -> {
            owner.context
        }
        is SupportActivity -> {
            owner.getContext()
        }
        is Activity -> {
            owner
        }
        else -> null
    }
}

fun isPageVisible(owner: LifecycleOwner): Boolean {
    if (!AppUtils.isAppForeground()) return false
    return when (owner) {
        is Activity -> {
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