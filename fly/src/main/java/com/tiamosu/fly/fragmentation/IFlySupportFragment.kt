package com.tiamosu.fly.fragmentation

/**
 * @author tiamosu
 * @date 2020/4/15.
 */
interface IFlySupportFragment {

    fun getSupportDelegate(): FlySupportFragmentDelegate

    fun onFlyLazyInitView()

    fun onFlySupportVisible()

    fun onFlySupportInvisible()

    fun isFlySupportVisible(): Boolean

    fun onBackPressedSupport(): Boolean
}