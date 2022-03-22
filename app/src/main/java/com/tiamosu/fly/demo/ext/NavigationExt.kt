package com.tiamosu.fly.demo.ext

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseActivity
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.navigation.ext.navController
import com.tiamosu.navigation.ext.navigate
import com.tiamosu.navigation.ext.popBackStack

/**
 * @author tiamosu
 * @date 2022/3/22
 */

/**
 * Fragment页面跳转
 * @param resId 操作ID或要导航到的目标ID
 * @param popUpToId 退出栈到指定目标ID
 * @param popUpToInclusive 给定的目的地是否也弹出
 * @param singleTop 是否进行栈顶复用
 * @param navigatorExtras 额外内容传递给导航器，可配置页面共享元素等
 * @param interval 设置防抖间隔时间，单位毫秒
 * @param args 传递给目的地的参数
 */
fun BaseFragment.jumpFragment(
    @IdRes resId: Int = -1,
    @IdRes popUpToId: Int = -1,
    popUpToInclusive: Boolean = false,
    singleTop: Boolean = false,
    navigatorExtras: Navigator.Extras? = null,
    interval: Long = 500,
    args: Bundle? = null,
    block: NavOptions.Builder.() -> Unit = {},
) {

    fun start() {
        if (resId == -1) {
            if (popUpToId != -1) {
                popBackStack(popUpToId, popUpToInclusive)
            }
            return
        }
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fly_slide_enter)
            .setExitAnim(R.anim.fly_slide_exit)
            .setPopEnterAnim(R.anim.fly_slide_pop_enter)
            .setPopExitAnim(R.anim.fly_slide_pop_exit)
            .apply {
                if (popUpToId != -1) {
                    setPopUpTo(popUpToId, popUpToInclusive)
                }
            }
            .setLaunchSingleTop(singleTop)
            .apply(block)
            .build()

        navigate(
            resId = resId,
            args = args,
            navOptions = navOptions,
            navigatorExtras = navigatorExtras,
            interval = interval
        )
    }

    if (!isStateSaved) {
        start()
    } else {
        context.lifecycleScope.launchWhenResumed { start() }
    }
}

/**
 * Fragment页面跳转
 *
 * @param viewId 可通过viewId获取导航控制器[navController]
 * @param resId 操作ID或要导航到的目标ID
 * @param popUpToId 退出栈到指定目标ID
 * @param popUpToInclusive 给定的目的地是否也弹出
 * @param singleTop 是否进行栈顶复用
 * @param navigatorExtras 额外内容传递给导航器，可配置页面共享元素等
 * @param interval 设置防抖间隔时间，单位毫秒
 * @param args 传递给目的地的参数
 */
fun BaseActivity.jumpFragment(
    @IdRes viewId: Int,
    @IdRes resId: Int = -1,
    @IdRes popUpToId: Int = -1,
    popUpToInclusive: Boolean = false,
    singleTop: Boolean = false,
    navigatorExtras: Navigator.Extras? = null,
    interval: Long = 500,
    args: Bundle? = null,
    block: NavOptions.Builder.() -> Unit = {},
) {

    fun start() {
        if (resId == -1) {
            if (popUpToId != -1) {
                popBackStack(viewId, popUpToId, popUpToInclusive)
            }
            return
        }
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fly_slide_enter)
            .setExitAnim(R.anim.fly_slide_exit)
            .setPopEnterAnim(R.anim.fly_slide_pop_enter)
            .setPopExitAnim(R.anim.fly_slide_pop_exit)
            .apply {
                if (popUpToId != -1) {
                    setPopUpTo(popUpToId, popUpToInclusive)
                }
            }
            .setLaunchSingleTop(singleTop)
            .apply(block)
            .build()

        navigate(
            viewId = viewId,
            resId = resId,
            args = args,
            navOptions = navOptions,
            navigatorExtras = navigatorExtras,
            interval = interval
        )
    }

    lifecycleScope.launchWhenResumed { start() }
}