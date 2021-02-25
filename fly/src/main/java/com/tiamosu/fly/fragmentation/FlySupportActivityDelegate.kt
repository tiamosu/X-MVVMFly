package com.tiamosu.fly.fragmentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * @author tiamosu
 * @date 2020/4/15.
 */
class FlySupportActivityDelegate(private val supportA: IFlySupportActivity) {
    private val activity: FragmentActivity

    init {
        if (supportA !is FragmentActivity) {
            throw IllegalStateException("${supportA.javaClass.simpleName} must extends FragmentActivity")
        }
        this.activity = supportA
    }

    fun onBackPressed() {
        // 获取 activeFragment 即从栈顶开始 状态为 show 的那个 Fragment
        val activeFragment = FlySupportHelper.getAddedFragment(getSupportFragmentManager())
        if (!dispatchBackPressedEvent(activeFragment)) {
            supportA.onBackPressedSupport()
        }
    }

    fun onBackPressedSupport() {
        activity.onBackPressedDispatcher.onBackPressed()
    }

    private fun dispatchBackPressedEvent(activeFragment: IFlySupportFragment?): Boolean {
        if (activeFragment != null) {
            if (activeFragment.onBackPressedSupport()) return true

            val parentFragment = (activeFragment as? Fragment)?.parentFragment
            return dispatchBackPressedEvent(parentFragment as? IFlySupportFragment)
        }
        return false
    }

    private fun getSupportFragmentManager(): FragmentManager {
        return activity.supportFragmentManager
    }
}