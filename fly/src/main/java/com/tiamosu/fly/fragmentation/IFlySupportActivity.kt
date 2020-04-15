package com.tiamosu.fly.fragmentation

/**
 * @author tiamosu
 * @date 2020/4/15.
 */
interface IFlySupportActivity {

    fun getSupportDelegate(): FlySupportActivityDelegate

    fun onBackPressed()

    fun onBackPressedSupport()
}