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

/**
 * @author tiamosu
 * @date 2018/9/14.
 */
object FlyUtils {

    @JvmStatic
    fun getAppComponent(): AppComponent {
        return (Utils.getApp() as IFlyApp).getAppComponent()
    }

    @JvmStatic
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

    @JvmStatic
    fun isPageVisible(owner: LifecycleOwner): Boolean {
        return when (owner) {
            is Activity -> {
                val activity = owner as Activity
                (AppUtils.isAppForeground() && ActivityUtils.getTopActivity() == activity)
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
}
