package com.tiamosu.fly.fragmentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle

/**
 * @author tiamosu
 * @date 2020/4/15.
 */
object FlySupportHelper {

    fun getAddedFragment(fragmentManager: FragmentManager): IFlySupportFragment? {
        return getAddedFragment(fragmentManager, null)
    }

    private fun getAddedFragment(
        fragmentManager: FragmentManager,
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
                    fragment.childFragmentManager, fragment as? IFlySupportFragment
                )
            }
        }
        return parentFragment
    }

    fun isFragmentVisible(fragment: Fragment): Boolean {
        return fragment.lifecycle.currentState == Lifecycle.State.STARTED
    }

    fun isNavHostFragment(fragment: Fragment?): Boolean {
        fragment ?: return false
        return fragment.javaClass.simpleName.contains("NavHostFragment")
    }

    fun getAddedFragments(fragmentManager: FragmentManager): List<Fragment> {
        return fragmentManager.fragments
    }
}