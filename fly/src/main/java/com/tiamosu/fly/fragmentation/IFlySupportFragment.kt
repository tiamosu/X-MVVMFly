package com.tiamosu.fly.fragmentation

/**
 * @author tiamosu
 * @date 2020/4/15.
 */
interface IFlySupportFragment {

    fun getSupportDelegate(): FlySupportFragmentDelegate

    fun onLazyInitView()

    fun onSupportVisible()

    fun onSupportInvisible()

    fun isSupportVisible(): Boolean

    fun onBackPressedSupport(): Boolean
}