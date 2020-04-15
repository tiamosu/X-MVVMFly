package com.tiamosu.fly.fragmentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * @author tiamosu
 * @date 2020/4/15.
 */
class FlySupportFragmentDelegate(private val supportF: IFlySupportFragment) {
    internal val visibleDelegate by lazy { FlyVisibleDelegate(supportF) }
    private val fragment: Fragment
    private lateinit var activity: FragmentActivity

    init {
        if (supportF !is Fragment) {
            throw RuntimeException("${supportF.javaClass.simpleName} must extends Fragment")
        }
        fragment = supportF
    }

    fun onAttach() {
        val activity = fragment.activity
        if (activity !is IFlySupportActivity) {
            throw RuntimeException(
                activity?.javaClass?.simpleName
                    ?: "activity" + " must impl IFlySupportActivity!"
            )
        }
        this.activity = activity
    }

    fun onCreateView() {
        visibleDelegate.onCreateView()
    }

    fun onViewCreated() {
        visibleDelegate.onViewCreated()
    }

    fun onResume() {
        visibleDelegate.onResume()
    }

    fun onPause() {
        visibleDelegate.onPause()
    }

    fun onDestroyView() {
        visibleDelegate.onDestroyView()
    }

    fun setUserVisibleHint(isVisibleToUser: Boolean) {
        visibleDelegate.setUserVisibleHint(isVisibleToUser)
    }

    fun onHiddenChanged(hidden: Boolean) {
        visibleDelegate.onHiddenChanged(hidden)
    }

    fun isSupportVisible() = visibleDelegate.isSupportVisible()

    fun onBackPressedSupport() = false
}