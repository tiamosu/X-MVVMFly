package com.tiamosu.fly.fragmentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import com.tiamosu.fly.navigation.NavHostFragment

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
        Log.e(
            "tiamosu", "onActivityCreated:$firstCreateViewCompatReplace"
                    + "   startWithAndroidSwitcher:" + (fragment.tag?.startsWith("android:switcher:") == true)
        )

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
        Log.e("tiamosu", "initVisible")

        Log.e(
            "tiamosu", "invisibleWhenLeave:" + invisibleWhenLeave
                    + "   isFragmentVisible:" + FlySupportHelper.isFragmentVisible(fragment)
        )

        if (!invisibleWhenLeave && FlySupportHelper.isFragmentVisible(fragment)) {
            Log.e("tiamosu", "initVisible1")

            if (fragment.parentFragment == null
                || FlySupportHelper.isFragmentVisible(fragment.requireParentFragment())
            ) {
                needDispatch = false
                safeDispatchUserVisibleHint(true)
            }
        }
    }

    fun onResume() {
        Log.e("tiamosu", "onResume")

        if (!isFirstVisible) {
            if (!isSupportVisible && !invisibleWhenLeave && FlySupportHelper.isFragmentVisible(
                    fragment
                )
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
        Log.e("tiamosu", "onPause")

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
        Log.e("tiamosu", "onHiddenChanged:$hidden")

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
        Log.e("tiamosu", "onFragmentShownWhenNotResumed")

        invisibleWhenLeave = false
        dispatchChildOnFragmentShownWhenNotResumed()
    }

    private fun dispatchChildOnFragmentShownWhenNotResumed() {
        Log.e("tiamosu", "dispatchChildOnFragmentShownWhenNotResumed")

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

    fun setUserVisibleHint(isVisibleToUser: Boolean) {
        Log.e("tiamosu", "setUserVisibleHint:$isVisibleToUser")

        if (fragment.isResumed || (!fragment.isAdded && isVisibleToUser)) {
            if (!isSupportVisible && isVisibleToUser) {
                safeDispatchUserVisibleHint(true)
            } else if (isSupportVisible && !isVisibleToUser) {
                dispatchSupportVisible(false)
            }
        }
    }

    private fun safeDispatchUserVisibleHint(visible: Boolean) {
        Log.e("tiamosu", "safeDispatchUserVisibleHint:$visible")

        if (isFirstVisible) {
            if (!visible) return
            enqueueDispatchVisible()
        } else {
            dispatchSupportVisible(visible)
        }
    }

    private fun enqueueDispatchVisible() {
        Log.e("tiamosu", "enqueueDispatchVisible")

        taskDispatchSupportVisible = Runnable {
            taskDispatchSupportVisible = null
            dispatchSupportVisible(true)
        }
        taskDispatchSupportVisible?.let(handler::post)
    }

    private fun dispatchSupportVisible(visible: Boolean) {
        Log.e("tiamosu", "dispatchSupportVisible:$visible")

        if (visible && isParentInvisible()) return
        Log.e("tiamosu", "dispatchSupportVisible111")

        if (isSupportVisible == visible) {
            needDispatch = true
            return
        }
        isSupportVisible = visible
        Log.e("tiamosu", "dispatchSupportVisible222")

        if (visible) {
            Log.e("tiamosu", "addState:" + checkAddState())
            if (checkAddState()) return

            if (isFirstVisible) {
                isFirstVisible = false
                supportF.onFlyLazyInitView()
            }
            supportF.onFlySupportVisible()
            dispatchChild(true)
        } else {
            dispatchChild(false)
            supportF.onFlySupportInvisible()
        }
    }

    private fun dispatchChild(visible: Boolean) {
        Log.e("tiamosu", "dispatchChild:$visible   needDispatch:$needDispatch")

        if (!needDispatch) {
            needDispatch = true
        } else {
            if (checkAddState()) return

            val fragmentManager = fragment.childFragmentManager
            val childFragments = FlySupportHelper.getAddedFragments(fragmentManager)
            for (child in childFragments) {
                if (child is IFlySupportFragment) {
                    val childVisibleDelegate = child.getSupportDelegate().visibleDelegate
                    Log.e(
                        "tiamosu",
                        "simpleName:" + child.javaClass.simpleName
                                + "   isSupportVisible:" + childVisibleDelegate.isSupportVisible
                    )

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
    }

    private fun isParentInvisible(): Boolean {
        return when (val parentFragment = fragment.parentFragment) {
            is IFlySupportFragment -> {
                !parentFragment.isFlySupportVisible()
            }
            is NavHostFragment -> {
                false
            }
            else -> parentFragment?.isVisible == false
        }
    }

    private fun checkAddState(): Boolean {
        if (!fragment.isAdded) {
            isSupportVisible = !isSupportVisible
            Log.e("tiamosu", "checkAddState: true")
            return true
        }
        Log.e("tiamosu", "checkAddState: false")
        return false
    }

    fun isSupportVisible() = isSupportVisible

    companion object {
        private const val FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE =
            "fragmentation_invisible_when_leave"
        private const val FRAGMENTATION_STATE_SAVE_COMPAT_REPLACE = "fragmentation_compat_replace"
    }
}