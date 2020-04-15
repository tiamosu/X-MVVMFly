package com.tiamosu.fly.integration.ext

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment

/**
 * @author tiamosu
 * @date 2020/4/14.
 */

fun Activity.navController(@IdRes viewId: Int): NavController {
    return Navigation.findNavController(this, viewId)
}

fun Activity.navController(view: View): NavController {
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

fun Fragment.navigate(@IdRes resId: Int, view: View? = null) {
    navigate(resId, null, view)
}

fun Fragment.navigate(@IdRes resId: Int, args: Bundle?, view: View? = null) {
    navigate(resId, args, null, view)
}

fun Fragment.navigate(
    @IdRes resId: Int,
    args: Bundle?,
    navOptions: NavOptions?,
    view: View? = null
) {
    navigate(resId, args, navOptions, null, view)
}

fun Fragment.navigate(
    @IdRes resId: Int,
    args: Bundle?,
    navOptions: NavOptions?,
    navigatorExtras: Navigator.Extras?,
    view: View? = null
) {
    navController(view).navigate(resId, args, navOptions, navigatorExtras)
}

fun Fragment.navigate(deepLink: Uri, view: View? = null) {
    navigate(deepLink, null, view)
}

fun Fragment.navigate(deepLink: Uri, navOptions: NavOptions?, view: View? = null) {
    navigate(deepLink, navOptions, null, view)
}

fun Fragment.navigate(
    deepLink: Uri,
    navOptions: NavOptions?,
    navigatorExtras: Navigator.Extras?,
    view: View? = null
) {
    navController(view).navigate(deepLink, navOptions, navigatorExtras)
}

fun Fragment.navigate(directions: NavDirections, view: View? = null) {
    navigate(directions, null, view)
}

fun Fragment.navigate(directions: NavDirections, navOptions: NavOptions?, view: View? = null) {
    navController(view).navigate(directions, navOptions)
}

fun Fragment.navigate(
    directions: NavDirections,
    navigatorExtras: Navigator.Extras,
    view: View? = null
) {
    navController(view).navigate(directions, navigatorExtras)
}