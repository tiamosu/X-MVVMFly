package com.tiamosu.fly.base.delegate

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.tiamosu.fly.base.FlySupportFragment

/**
 * @author tiamosu
 * @date 2020/4/13.
 */
class FlyVisibleDelegate(private val fragment: FlySupportFragment) {
    private var isSupportVisible = false
    private var needDispatch = true
    private var invisibleWhenLeave = false
    private var isFirstVisible = true
    private var compatReplace = true
    private var abortInitVisible = false
    private var savedInstanceState: Bundle? = null
    private var taskDispatchSupportVisible: Runnable? = null
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            this.savedInstanceState = savedInstanceState
            // setUserVisibleHint() may be called before onCreate()
            invisibleWhenLeave = savedInstanceState.getBoolean(FRAGMENT_STATE_INVISIBLE_WHEN_LEAVE)
            compatReplace = savedInstanceState.getBoolean(FRAGMENT_STATE_COMPAT_REPLACE)
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(FRAGMENT_STATE_INVISIBLE_WHEN_LEAVE, invisibleWhenLeave)
        outState.putBoolean(FRAGMENT_STATE_COMPAT_REPLACE, compatReplace)
    }

    fun onActivityCreated() {
        if (!compatReplace
            && fragment.tag?.startsWith("android:switcher:") == true
        ) {
            return
        }
        if (compatReplace) {
            compatReplace = false
        }
        initVisible()
    }

    fun onResume() {
        if (!isFirstVisible) {
            if (!isSupportVisible && !invisibleWhenLeave && isFragmentVisible(fragment)) {
                needDispatch = false
                dispatchSupportVisible(true)
            }
        } else {
            if (abortInitVisible) {
                abortInitVisible = false
                initVisible()
            }
        }
    }

    fun onPause() {
        if (taskDispatchSupportVisible != null) {
            handler.removeCallbacks(taskDispatchSupportVisible!!)
            abortInitVisible = true
            return
        }

        if (isSupportVisible && isFragmentVisible(fragment)) {
            needDispatch = false
            invisibleWhenLeave = false
            dispatchSupportVisible(false)
        } else {
            invisibleWhenLeave = true
        }
    }

    fun onHiddenChanged(hidden: Boolean) {
        if (!hidden && !fragment.isResumed) {
            //if fragment is shown but not resumed, ignore...
            onFragmentShownWhenNotResumed()
            return
        }
        if (hidden) {
            safeDispatchUserVisibleHint(false)
        } else {
            enqueueDispatchVisible()
        }
    }

    fun onDestroyView() {
        isFirstVisible = true
    }

    fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (fragment.isResumed || (!fragment.isAdded && isVisibleToUser)) {
            if (!isSupportVisible && isVisibleToUser) {
                safeDispatchUserVisibleHint(true)
            } else if (isSupportVisible && !isVisibleToUser) {
                dispatchSupportVisible(false)
            }
        }
    }

    fun isSupportVisible(): Boolean {
        return isSupportVisible
    }

    private fun initVisible() {
        if (!invisibleWhenLeave && isFragmentVisible(fragment)) {
            val parentFragment = fragment.parentFragment
            if (parentFragment == null || isFragmentVisible(parentFragment)) {
                needDispatch = false
                safeDispatchUserVisibleHint(true)
            }
        }
    }

    private fun onFragmentShownWhenNotResumed() {
        invisibleWhenLeave = false
        dispatchChildOnFragmentShownWhenNotResumed()
    }

    private fun dispatchChildOnFragmentShownWhenNotResumed() {
        val fragmentManager = fragment.childFragmentManager
        val childFragments = fragmentManager.fragments
        for (child in childFragments) {
            if (child is FlySupportFragment && isFragmentVisible(child)) {
                child.visibleDelegate.onFragmentShownWhenNotResumed()
            }
        }
    }

    private fun safeDispatchUserVisibleHint(visible: Boolean) {
        if (isFirstVisible) {
            if (!visible) return
            enqueueDispatchVisible()
        } else {
            dispatchSupportVisible(visible)
        }
    }

    private fun enqueueDispatchVisible() {
        taskDispatchSupportVisible = Runnable {
            taskDispatchSupportVisible = null
            dispatchSupportVisible(true)
        }
        taskDispatchSupportVisible?.let(handler::post)
    }

    private fun dispatchSupportVisible(visible: Boolean) {
        if (visible && isParentInvisible()) return

        if (isSupportVisible == visible) {
            needDispatch = true
            return
        }
        isSupportVisible = visible

        if (visible) {
            if (checkAddState()) return
            fragment.onFlySupportVisible()

            if (isFirstVisible) {
                isFirstVisible = false
                fragment.onFlyLazyInitView(savedInstanceState)
            }
            dispatchChild(true)
        } else {
            dispatchChild(false)
            fragment.onFlySupportInvisible()
        }
    }

    private fun dispatchChild(visible: Boolean) {
        if (!needDispatch) {
            needDispatch = true
        } else {
            if (checkAddState()) return

            val fragmentManager = fragment.childFragmentManager
            val childFragments = fragmentManager.fragments
            for (child in childFragments) {
                if (child is FlySupportFragment && isFragmentVisible(child)) {
                    child.visibleDelegate.dispatchSupportVisible(visible)
                }
            }
        }
    }

    private fun isParentInvisible(): Boolean {
        val parentFragment = fragment.parentFragment
        return if (parentFragment is FlySupportFragment) {
            !parentFragment.isFlySupportVisible()
        } else parentFragment?.isVisible == false
    }

    private fun checkAddState(): Boolean {
        if (!fragment.isAdded) {
            isSupportVisible = !isSupportVisible
            return true
        }
        return false
    }

    @Suppress("DEPRECATION")
    private fun isFragmentVisible(fragment: Fragment): Boolean {
        return !fragment.isHidden && fragment.userVisibleHint
    }

    companion object {
        private const val FRAGMENT_STATE_INVISIBLE_WHEN_LEAVE = "fragmentation_invisible_when_leave"
        private const val FRAGMENT_STATE_COMPAT_REPLACE = "fragmentation_compat_replace"
    }
}