package com.tiamosu.fly.fragmentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment

/**
 * @author tiamosu
 * @date 2020/5/12.
 */
class FlyVisibleDelegate(private val supportF: IFlySupportFragment) {
    // SupportVisible相关
    private var isSupportVisible = false
    private var needDispatch = true
    private var invisibleWhenLeave = false
    private var isFirstVisible = true
    private var firstCreateViewCompatReplace = true
    private var abortInitVisible = false
    private var isNeedDispatchRecord = false
    private var taskDispatchSupportVisible: Runnable? = null

    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private var saveInstanceState: Bundle? = null
    private var fragment: Fragment

    init {
        if (supportF !is Fragment) {
            throw RuntimeException("Must extends Fragment")
        }
        fragment = supportF
    }

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            saveInstanceState = savedInstanceState
            // setUserVisibleHint() may be called before onCreate()
            invisibleWhenLeave =
                savedInstanceState.getBoolean(FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE)
            firstCreateViewCompatReplace =
                savedInstanceState.getBoolean(FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE)
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE, invisibleWhenLeave)
        outState.putBoolean(FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE, firstCreateViewCompatReplace)
    }

    fun onActivityCreated() {
        if (!firstCreateViewCompatReplace
            && fragment.tag?.startsWith("android:switcher:") == true
        ) {
            return
        }
        if (firstCreateViewCompatReplace) {
            firstCreateViewCompatReplace = false
        }
        initVisible()
    }

    private fun initVisible() {
        if (!invisibleWhenLeave && FlySupportHelper.isFragmentVisible(fragment)) {
            if (fragment.parentFragment == null
                || FlySupportHelper.isFragmentVisible(fragment.requireParentFragment())
            ) {
                needDispatch = false
                safeDispatchUserVisibleHint(true)
            }
        }
    }

    fun onResume() {
        if (!isFirstVisible) {
            if (!isSupportVisible && !invisibleWhenLeave
                && FlySupportHelper.isFragmentVisible(fragment)
            ) {
                needDispatch = false
                dispatchSupportVisible(true)
            }
        } else {
            if (abortInitVisible) {
                abortInitVisible = false
                initVisible()
            } else {
                needDispatch = false
                dispatchSupportVisible(true)
            }
        }
    }

    fun onPause() {
        if (taskDispatchSupportVisible != null) {
            handler.removeCallbacks(taskDispatchSupportVisible!!)
            abortInitVisible = true
            return
        }

        if (isSupportVisible && FlySupportHelper.isFragmentVisible(fragment)) {
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

    private fun onFragmentShownWhenNotResumed() {
        invisibleWhenLeave = false
        dispatchChildOnFragmentShownWhenNotResumed()
    }

    private fun dispatchChildOnFragmentShownWhenNotResumed() {
        val fragmentManager = fragment.childFragmentManager
        val childFragments = FlySupportHelper.getAddedFragments(fragmentManager)
        for (child in childFragments) {
            if (child is IFlySupportFragment && FlySupportHelper.isFragmentVisible(child)) {
                child.getSupportDelegate().visibleDelegate.onFragmentShownWhenNotResumed()
            }
        }
    }

    fun onDestroyView() {
        isFirstVisible = true
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

            if (isFirstVisible) {
                isFirstVisible = false
                supportF.onLazyInitView()
            }
            supportF.onSupportVisible()
            dispatchChild(true)
        } else {
            dispatchChild(false)
            supportF.onSupportInvisible()
        }
    }

    private fun dispatchChild(visible: Boolean) {
        if (!needDispatch) {
            needDispatch = true
        } else {
            if (checkAddState()) return

            val fragmentManager = fragment.childFragmentManager
            val childFragments = FlySupportHelper.getAddedFragments(fragmentManager)
            for (child in childFragments) {
                if (child !is IFlySupportFragment) {
                    continue
                }
                val childVisibleDelegate = child.getSupportDelegate().visibleDelegate
                if (visible) {
                    if (childVisibleDelegate.isNeedDispatchRecord) {
                        childVisibleDelegate.isNeedDispatchRecord = false
                        childVisibleDelegate.dispatchSupportVisible(true)
                    }
                } else {
                    if (childVisibleDelegate.isSupportVisible) {
                        childVisibleDelegate.isNeedDispatchRecord = true
                        childVisibleDelegate.dispatchSupportVisible(false)
                    }
                }
            }
        }
    }

    private fun isParentInvisible(): Boolean {
        val parentFragment = fragment.parentFragment
        return when {
            parentFragment is IFlySupportFragment -> {
                !parentFragment.isFlySupportVisible()
            }
            FlySupportHelper.isNavHostFragment(parentFragment) -> {
                false
            }
            else -> {
                parentFragment?.isVisible == false
            }
        }
    }

    private fun checkAddState(): Boolean {
        if (!fragment.isAdded) {
            isSupportVisible = !isSupportVisible
            return true
        }
        return false
    }

    fun isSupportVisible() = isSupportVisible

    companion object {
        private const val FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE =
            "fragmentation_invisible_when_leave"
        private const val FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE = "fragmentation_compat_replace"
    }
}