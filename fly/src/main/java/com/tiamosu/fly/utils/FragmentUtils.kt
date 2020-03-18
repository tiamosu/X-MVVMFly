@file:JvmName("FragmentUtils")

package com.tiamosu.fly.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import me.yokeyword.fragmentation.ISupportFragment

/**
 * @author tiamosu
 * @date 2020/3/18.
 */

fun <T : ISupportFragment> newInstance(cls: Class<out ISupportFragment>?): T? {
    return newInstance(cls, null)
}

@Suppress("UNCHECKED_CAST")
fun <T : ISupportFragment> newInstance(cls: Class<out ISupportFragment>?, bundle: Bundle?): T? {
    try {
        val t = cls?.newInstance() as? T
        return newInstance(t, bundle)
    } catch (ignored: IllegalAccessException) {
    } catch (ignored: InstantiationException) {
    }
    return null
}

fun <T : ISupportFragment> newInstance(fragment: ISupportFragment?): T? {
    return newInstance(fragment, null)
}

@Suppress("UNCHECKED_CAST")
fun <T : ISupportFragment> newInstance(fragment: ISupportFragment?, bundle: Bundle?): T? {
    fragment?.apply {
        if (bundle != null && !bundle.isEmpty) {
            (this as Fragment).arguments = bundle
            putNewBundle(bundle)
        }
    }
    return fragment as? T
}