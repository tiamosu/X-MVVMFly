package com.tiamosu.fly.integration.ext

import android.net.Uri
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment

/**
 * @author tiamosu
 * @date 2020/4/14.
 */
fun Fragment.nav(): NavController {
    return NavHostFragment.findNavController(this)
}

fun Fragment.navigateUp(): Boolean {
    return nav().navigateUp()
}

fun Fragment.navigate(@IdRes resId: Int) {
    navigate(resId, null)
}

fun Fragment.navigate(@IdRes resId: Int, args: Bundle?) {
    navigate(resId, args, null)
}

fun Fragment.navigate(@IdRes resId: Int, args: Bundle?, navOptions: NavOptions?) {
    navigate(resId, args, navOptions, null)
}

fun Fragment.navigate(
    @IdRes resId: Int,
    args: Bundle?,
    navOptions: NavOptions?,
    navigatorExtras: Navigator.Extras?
) {
    nav().navigate(resId, args, navOptions, navigatorExtras)
}

fun Fragment.navigate(deepLink: Uri) {
    navigate(deepLink, null)
}

fun Fragment.navigate(deepLink: Uri, navOptions: NavOptions?) {
    navigate(deepLink, navOptions, null)
}

fun Fragment.navigate(deepLink: Uri, navOptions: NavOptions?, navigatorExtras: Navigator.Extras?) {
    nav().navigate(deepLink, navOptions, navigatorExtras)
}

fun Fragment.navigate(directions: NavDirections) {
    nav().navigate(directions)
}

fun Fragment.navigate(directions: NavDirections, navOptions: NavOptions?) {
    nav().navigate(directions, navOptions)
}

fun Fragment.navigate(directions: NavDirections, navigatorExtras: Navigator.Extras) {
    nav().navigate(directions, navigatorExtras)
}