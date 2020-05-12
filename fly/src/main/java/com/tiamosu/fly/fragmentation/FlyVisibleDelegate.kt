package com.tiamosu.fly.fragmentation

import android.util.Log
import androidx.fragment.app.Fragment

/**
 * @author tiamosu
 * @date 2020/4/14.
 */
class FlyVisibleDelegate(private val supportF: IFlySupportFragment) {
    private var currentVisibleState = false     //当前可见状态
    private var isFirstVisible = true           //是否第一次可见
    private var isChangedState = false
    private var fragment: Fragment

    init {
        if (supportF !is Fragment) {
            throw RuntimeException("${supportF.javaClass.simpleName} must extends Fragment")
        }
        fragment = supportF
    }

    fun onViewCreated() {
        if (FlySupportHelper.isFragmentVisible(fragment)) {
            dispatchUserVisibleHint(true)
        }
    }

    fun onResume() {
        if (FlySupportHelper.isFragmentVisible(fragment) && !currentVisibleState) {
            dispatchUserVisibleHint(true)
        }
    }

    fun onPause() {
        if (FlySupportHelper.isFragmentVisible(fragment) && currentVisibleState) {
            dispatchUserVisibleHint(false)
        }
    }

    fun onHiddenChanged(hidden: Boolean) {
        // 这里的可见返回为false
        if (hidden) {
            dispatchUserVisibleHint(false)
        } else {
            dispatchUserVisibleHint(true)
        }
    }

    fun isSupportVisible() = currentVisibleState

    /**
     * 统一处理用户可见事件分发
     */
    private fun dispatchUserVisibleHint(isVisible: Boolean) {
        // 为了代码严谨,如果当前状态与需要设置的状态本来就一致了,就不处理了
        if (currentVisibleState == isVisible) return
        currentVisibleState = isVisible
        if (isVisible) {
            if (isFirstVisible) {
                isFirstVisible = false
                supportF.onLazyInitView()
            }
            supportF.onSupportVisible()
            dispatchChildVisibleState(true)
        } else {
            supportF.onSupportInvisible()
            Log.e("xia", "Pause currentVisibleState:$currentVisibleState")
            dispatchChildVisibleState(false)
        }
    }

    /**
     * 分发事件给内嵌的 Fragment
     */
    private fun dispatchChildVisibleState(visible: Boolean) {
        val fragments = FlySupportHelper.getAddedFragments(fragment.childFragmentManager)
        Log.e("xia", "dispatchChildVisibleState:$visible" + "   size:" + fragments.size)

        for (fragment in fragments) {
            if (fragment !is FlySupportFragment) {
                continue
            }

            val delegate = fragment.getSupportDelegate().visibleDelegate
            if (visible) {
                if (delegate.isChangedState) {
                    delegate.isChangedState = false
                    delegate.dispatchUserVisibleHint(visible)
                }
            } else {
                if (delegate.currentVisibleState) {
                    delegate.isChangedState = true
                    delegate.dispatchUserVisibleHint(visible)
                }
            }
        }
    }
}