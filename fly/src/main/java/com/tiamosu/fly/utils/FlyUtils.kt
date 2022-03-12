@file:JvmName("FlyUtils")

package com.tiamosu.fly.utils

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.base.IFlyApp
import com.tiamosu.fly.di.component.AppComponent
import com.tiamosu.fly.integration.gson.GsonFactory
import com.tiamosu.navigation.page.FlySupportActivity
import com.tiamosu.navigation.page.FlySupportFragment

/**
 * @author tiamosu
 * @date 2020/2/28.
 */

fun getAppComponent(): AppComponent {
    return checkNotNull((Utils.getApp() as? IFlyApp)?.getAppComponent()) {
        "${Utils.getApp().javaClass.name} must be implements ${IFlyApp::class.java.name}"
    }
}

/**
 * 页面是否真实可见
 */
val LifecycleOwner.isPageVisible: Boolean
    get() {
        return when (this) {
            is FlySupportFragment -> this.isFlySupportVisible()
            is FlySupportActivity -> this.isFlySupportVisible()
            is Activity -> AppUtils.isAppForeground() && ActivityUtils.getTopActivity() == this
            is Fragment -> AppUtils.isAppForeground() && this.isVisible
            else -> AppUtils.isAppForeground()
        }
    }

/**
 * 深度克隆
 */
inline fun <reified T> T.deepClone(): T? {
    val str = GsonFactory.toJson(this)
    return GsonFactory.fromJson(str)
}