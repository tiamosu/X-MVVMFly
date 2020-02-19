package com.tiamosu.fly.utils

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.base.App
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
        return (Utils.getApp() as App).getAppComponent()
    }

    @JvmStatic
    fun getContext(lifecycleOwner: LifecycleOwner): Context? {
        return when (lifecycleOwner) {
            is SupportFragment -> {
                lifecycleOwner.context
            }
            is Fragment -> {
                lifecycleOwner.context
            }
            is SupportActivity -> {
                lifecycleOwner.getContext()
            }
            is Activity -> {
                lifecycleOwner
            }
            else -> null
        }
    }

//    @JvmStatic
//    fun isCurrentVisible(lifecycleOwner: LifecycleOwner): Boolean {
//        when (lifecycleOwner) {
//            is Activity -> {
//                val activity = lifecycleOwner as Activity?
//                return AppUtils.isAppForeground()
//                        && ActivityUtils.getTopActivity() == activity!!
//            }
//            is FlySupportFragment<*> -> {
//                val fragment = lifecycleOwner as FlySupportFragment<*>?
//                return fragment!!.isSupportVisible()
//            }
//            is Fragment -> {
//                val fragment = lifecycleOwner as Fragment?
//                return fragment!!.isVisible
//            }
//            else -> return false
//        }
//    }
}
