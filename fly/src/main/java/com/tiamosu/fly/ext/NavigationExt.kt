@file:Suppress("unused")

package com.tiamosu.fly.ext

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment

/**
 * 获取导航控制器[navController]
 *
 * @param viewId 视图控件id，必传
 */
fun AppCompatActivity.navController(@IdRes viewId: Int): NavController {
    return Navigation.findNavController(this, viewId)
}

/**
 * 尝试在导航层次结构中向上导航（页面返回到上一页）
 *
 * @param viewId 可通过viewId获取导航控制器[navController]
 */
fun AppCompatActivity.navigateUp(@IdRes viewId: Int): Boolean {
    return try {
        navController(viewId).navigateUp()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 尝试将控制器的后退栈弹出到特定的目的地（页面返回到给定的目的地）
 *
 * @param destinationId 所要达到的目的地id
 * @param inclusive 给定的目的地是否也弹出
 * @param viewId 可通过viewId获取导航控制器[navController]
 */
fun AppCompatActivity.popBackStack(
    @IdRes destinationId: Int? = null,
    inclusive: Boolean = false,
    @IdRes viewId: Int
): Boolean {
    return try {
        if (destinationId != null) {
            navController(viewId).popBackStack(destinationId, inclusive)
        } else {
            navController(viewId).popBackStack()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 从当前导航图导航到目标
 *
 * @param resId 操作ID或要导航到的目标ID
 * @param args 传递给目的地的参数
 * @param navOptions 此导航操作的特殊选项，可配置页面跳转动画等
 * @param navigatorExtras 额外内容传递给导航器，可配置页面共享元素等
 * @param viewId 可通过viewId获取导航控制器[navController]
 * @param interval 设置防抖间隔时间，单位毫秒
 */
fun AppCompatActivity.navigate(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    @IdRes viewId: Int,
    interval: Long = 500
) {
    if (isValid(interval)) {
        try {
            navController(viewId).navigate(resId, args, navOptions, navigatorExtras)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 通过给定的深层链接导航到目的地
 *
 * @param deepLink 从当前导航图[NavGraph]可到达目标的deepLink
 * @param navOptions 此导航操作的特殊选项，可配置页面跳转动画等
 * @param navigatorExtras 额外内容传递给导航器，可配置页面共享元素等
 * @param viewId 可通过viewId获取导航控制器[navController]
 * @param interval 设置防抖间隔时间，单位毫秒
 */
fun AppCompatActivity.navigate(
    deepLink: Uri,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    @IdRes viewId: Int,
    interval: Long = 500
) {
    if (isValid(interval)) {
        try {
            navController(viewId).navigate(deepLink, navOptions, navigatorExtras)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 通过给定的[NavDirections]导航到目的地
 *
 * @param directions 描述此导航操作的说明
 * @param navOptions 此导航操作的特殊选项，可配置页面跳转动画等
 * @param viewId 可通过viewId获取导航控制器[navController]
 * @param interval 设置防抖间隔时间，单位毫秒
 */
fun AppCompatActivity.navigate(
    directions: NavDirections,
    navOptions: NavOptions? = null,
    @IdRes viewId: Int,
    interval: Long = 500
) {
    if (isValid(interval)) {
        try {
            navController(viewId).navigate(directions, navOptions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 通过给定的[NavDirections]导航到目的地
 *
 * @param directions 描述此导航操作的说明
 * @param navigatorExtras 额外内容传递给导航器，可配置页面共享元素等
 * @param viewId 可通过viewId获取导航控制器[navController]
 * @param interval 设置防抖间隔时间，单位毫秒
 */
fun AppCompatActivity.navigate(
    directions: NavDirections,
    navigatorExtras: Navigator.Extras,
    @IdRes viewId: Int,
    interval: Long = 500
) {
    if (isValid(interval)) {
        try {
            navController(viewId).navigate(directions, navigatorExtras)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 通过给定的[NavDeepLinkRequest]导航到目的地
 *
 * @param request 从当前导航图[NavGraph]可到达目标的deepLinkRequest
 * @param navOptions 此导航操作的特殊选项，可配置页面跳转动画等
 * @param navigatorExtras 额外内容传递给导航器，可配置页面共享元素等
 * @param viewId 可通过viewId获取导航控制器[navController]
 * @param interval 设置防抖间隔时间，单位毫秒
 */
fun AppCompatActivity.navigate(
    request: NavDeepLinkRequest,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    @IdRes viewId: Int,
    interval: Long = 500
) {
    if (isValid(interval)) {
        try {
            navController(viewId).navigate(request, navOptions, navigatorExtras)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 获取导航控制器[Fragment.navController]
 *
 * @param view 视图View，可不传
 */
fun Fragment.navController(view: View? = null): NavController {
    if (view != null) {
        return Navigation.findNavController(view)
    }
    return NavHostFragment.findNavController(this)
}

/**
 * 尝试在导航层次结构中向上导航（页面返回到上一页）
 *
 * @param view 可通过view获取导航控制器[Fragment.navController]
 */
fun Fragment.navigateUp(view: View? = null): Boolean {
    if (!isAdded) {
        return false
    }
    return try {
        navController(view).navigateUp()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 尝试将控制器的后退栈弹出到特定的目的地（页面返回到给定的目的地）
 *
 * @param destinationId 所要达到的目的地id
 * @param inclusive 给定的目的地是否也弹出
 * @param view 可通过view获取导航控制器[Fragment.navController]
 */
fun Fragment.popBackStack(
    @IdRes destinationId: Int? = null,
    inclusive: Boolean = false,
    view: View? = null
): Boolean {
    if (!isAdded) {
        return false
    }
    return try {
        if (destinationId != null) {
            navController(view).popBackStack(destinationId, inclusive)
        } else {
            navController(view).popBackStack()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 从当前导航图导航到目标
 *
 * @param resId 操作ID或要导航到的目标ID
 * @param args 传递给目的地的参数
 * @param navOptions 此导航操作的特殊选项，可配置页面跳转动画等
 * @param navigatorExtras 额外内容传递给导航器，可配置页面共享元素等
 * @param view 可通过view获取导航控制器[Fragment.navController]
 * @param interval 设置防抖间隔时间，单位毫秒
 */
fun Fragment.navigate(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    view: View? = null,
    interval: Long = 500
) {
    if (isValid(interval) && isAdded) {
        try {
            navController(view).navigate(resId, args, navOptions, navigatorExtras)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 通过给定的深层链接导航到目的地
 *
 * @param deepLink 从当前导航图[NavGraph]可到达目标的deepLink
 * @param navOptions 此导航操作的特殊选项，可配置页面跳转动画等
 * @param navigatorExtras 额外内容传递给导航器，可配置页面共享元素等
 * @param view 可通过view获取导航控制器[Fragment.navController]
 * @param interval 设置防抖间隔时间，单位毫秒
 */
fun Fragment.navigate(
    deepLink: Uri,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    view: View? = null,
    interval: Long = 500
) {
    if (isValid(interval) && isAdded) {
        try {
            navController(view).navigate(deepLink, navOptions, navigatorExtras)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 通过给定的[NavDirections]导航到目的地
 *
 * @param directions 描述此导航操作的说明
 * @param navOptions 此导航操作的特殊选项，可配置页面跳转动画等
 * @param view 可通过view获取导航控制器[Fragment.navController]
 * @param interval 设置防抖间隔时间，单位毫秒
 */
fun Fragment.navigate(
    directions: NavDirections,
    navOptions: NavOptions? = null,
    view: View? = null,
    interval: Long = 500
) {
    if (isValid(interval) && isAdded) {
        try {
            navController(view).navigate(directions, navOptions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 通过给定的[NavDirections]导航到目的地
 *
 * @param directions 描述此导航操作的说明
 * @param navigatorExtras 额外内容传递给导航器，可配置页面共享元素等
 * @param view 可通过view获取导航控制器[Fragment.navController]
 * @param interval 设置防抖间隔时间，单位毫秒
 */
fun Fragment.navigate(
    directions: NavDirections,
    navigatorExtras: Navigator.Extras,
    view: View? = null,
    interval: Long = 500
) {
    if (isValid(interval) && isAdded) {
        try {
            navController(view).navigate(directions, navigatorExtras)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 通过给定的[NavDeepLinkRequest]导航到目的地
 *
 * @param request 从当前导航图[NavGraph]可到达目标的deepLinkRequest
 * @param navOptions 此导航操作的特殊选项，可配置页面跳转动画等
 * @param navigatorExtras 额外内容传递给导航器，可配置页面共享元素等
 * @param view 可通过view获取导航控制器[Fragment.navController]
 * @param interval 设置防抖间隔时间，单位毫秒
 */
fun Fragment.navigate(
    request: NavDeepLinkRequest,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    view: View? = null,
    interval: Long = 500
) {
    if (isValid(interval) && isAdded) {
        try {
            navController(view).navigate(request, navOptions, navigatorExtras)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private var lastNavTime = 0L

private fun isValid(interval: Long): Boolean {
    val currentTime = System.currentTimeMillis()
    if (currentTime >= lastNavTime + interval) {
        lastNavTime = currentTime
        return true
    }
    return false
}