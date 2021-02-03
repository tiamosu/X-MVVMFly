package com.tiamosu.fly.fragmentation

import androidx.fragment.app.Fragment

/**
 * @author tiamosu
 * @date 2020/5/12.
 */
class FlyVisibleDelegate(private val supportF: IFlySupportFragment) {
    // SupportVisible相关
    private var isSupportVisible = false
    private var isFirstVisible = true
    private var fragment: Fragment

    init {
        if (supportF !is Fragment) {
            throw RuntimeException("Must extends Fragment")
        }
        fragment = supportF
    }

    fun onResume() {
        if (isFirstVisible && !fragment.isHidden) {
            supportF.onLazyInitView()
            isFirstVisible = false
        }
        if (!fragment.isHidden) {
            isSupportVisible = true
            supportF.onSupportVisible()
        }
    }

    fun onPause() {
        isSupportVisible = false
        supportF.onSupportInvisible()
    }

    fun onDestroyView() {
        isFirstVisible = true
    }

    fun isSupportVisible() = isSupportVisible
}