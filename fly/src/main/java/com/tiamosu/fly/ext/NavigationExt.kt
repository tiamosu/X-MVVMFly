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
 * @author tiamosu
 * @date 2020/4/14.
 */
fun AppCompatActivity.navController(@IdRes viewId: Int): NavController {
    return Navigation.findNavController(this, viewId)
}

fun Fragment.navController(view: View?): NavController? {
    if (view != null) {
        return Navigation.findNavController(view)
    }
    return NavHostFragment.findNavController(this)
}

fun Fragment.navigateUp(
    view: View? = null,
    interval: Long = 500
): Boolean {
    return try {
        isAdded && isValid(interval) && navController(view)?.navigateUp() ?: false
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

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
            navController(view)?.navigate(resId, args, navOptions, navigatorExtras)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Fragment.navigate(
    deepLink: Uri,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    view: View? = null,
    interval: Long = 500
) {
    if (isValid(interval) && isAdded) {
        try {
            navController(view)?.navigate(deepLink, navOptions, navigatorExtras)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Fragment.navigate(
    directions: NavDirections,
    navOptions: NavOptions? = null,
    view: View? = null,
    interval: Long = 500
) {
    if (isValid(interval) && isAdded) {
        try {
            navController(view)?.navigate(directions, navOptions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Fragment.navigate(
    directions: NavDirections,
    navigatorExtras: Navigator.Extras,
    view: View? = null,
    interval: Long = 500
) {
    if (isValid(interval) && isAdded) {
        try {
            navController(view)?.navigate(directions, navigatorExtras)
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