package com.tiamosu.fly.fragmentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment

/**
 * @author tiamosu
 * @date 2020/4/15.
 */
object FlySupportHelper {

    fun getAddedFragment(fragmentManager: FragmentManager): IFlySupportFragment? {
        return getAddedFragment(fragmentManager, null)
    }

    private fun getAddedFragment(
        fragmentManager: FragmentManager?,
        parentFragment: IFlySupportFragment?
    ): IFlySupportFragment? {
        val fragmentList = getAddedFragments(fragmentManager)
        if (fragmentList.isEmpty()) {
            return parentFragment
        }
        for (i in fragmentList.indices.reversed()) {
            val fragment = fragmentList[i]
            val isBool = ((fragment is IFlySupportFragment && fragment.isFlySupportVisible())
                    || isNavHostFragment(fragment)) && fragment.isResumed

            if (isBool) {
                return getAddedFragment(
                    getChildFragmentManager(fragment), fragment as? IFlySupportFragment
                )
            }
        }
        return parentFragment
    }

    fun isNavHostFragment(fragment: Fragment?): Boolean {
        return fragment is NavHostFragment
    }

    fun getAddedFragments(fragmentManager: FragmentManager?): List<Fragment> {
        return fragmentManager?.fragments ?: emptyList()
    }

    fun getChildFragmentManager(fragment: Fragment?): FragmentManager? {
        return try {
            fragment?.childFragmentManager
        } catch (ignore: Exception) {
            null
        }
    }
}