@file:Suppress("unused")

package com.tiamosu.fly.ext

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.*
import com.tiamosu.fly.navigation.NavHostFragment

/**
 * @author tiamosu
 * @date 2020/4/14.
 */
fun navController(view: View): NavController {
    return Navigation.findNavController(view)
}

fun AppCompatActivity.navController(@IdRes viewId: Int): NavController {
    return Navigation.findNavController(this, viewId)
}

fun AppCompatActivity.navController(view: View): NavController {
    return Navigation.findNavController(view)
}

fun Fragment.navController(view: View? = null): NavController {
    if (view != null) {
        return Navigation.findNavController(view)
    }
    return NavHostFragment.findNavController(this)
}

fun Fragment.navigateUp(view: View? = null): Boolean {
    return navController(view).navigateUp()
}

fun Fragment.navigate(
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    view: View? = null
) {
    navController(view).navigate(resId, args, navOptions, navigatorExtras)
}

fun Fragment.navigate(
    deepLink: Uri,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    view: View? = null
) {
    navController(view).navigate(deepLink, navOptions, navigatorExtras)
}

fun Fragment.navigate(
    directions: NavDirections,
    navOptions: NavOptions? = null,
    view: View? = null
) {
    navController(view).navigate(directions, navOptions)
}

fun Fragment.navigate(
    directions: NavDirections,
    navigatorExtras: Navigator.Extras,
    view: View? = null
) {
    navController(view).navigate(directions, navigatorExtras)
}