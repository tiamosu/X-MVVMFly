package com.tiamosu.fly.fragmentation

import androidx.fragment.app.Fragment

/**
 * @author tiamosu
 * @date 2020/5/12.
 */
class FlyVisibleDelegate(private val supportF: IFlySupportFragment) {
    private var isFirstVisible = true
    private var isSupportVisible = false
    private var fragment: Fragment

    init {
        if (supportF !is Fragment) {
            throw RuntimeException("Must extends Fragment")
        }
        fragment = supportF
    }

    fun onResume() {
        if (!fragment.isHidden && !isSupportVisible) {
            if (isFirstVisible) {
                isFirstVisible = false
                supportF.onLazyInitView()
            }
            isSupportVisible = true
            supportF.onSupportVisible()
        }
    }

    fun onPause() {
        if (isSupportVisible) {
            isSupportVisible = false
            supportF.onSupportInvisible()
        }
    }

    fun onDestroyView() {
        isFirstVisible = true
        isSupportVisible = false
    }

    fun isSupportVisible() = isSupportVisible
}