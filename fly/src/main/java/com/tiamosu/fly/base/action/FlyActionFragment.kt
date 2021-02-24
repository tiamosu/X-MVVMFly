package com.tiamosu.fly.base.action

import android.view.KeyEvent
import androidx.lifecycle.Lifecycle
import com.tiamosu.fly.fragmentation.FlySupportFragment

/**
 * @author tiamosu
 * @date 2021/2/24.
 */
open class FlyActionFragment : FlySupportFragment(),
    FragmentAction, BundleAction, HandlerAction, KeyboardAction {

    final override val bundle
        get() = arguments

    final override fun getContext() = activity

    override fun onDestroy() {
        super.onDestroy()
        removeCallbacks()
    }

    /**
     * Fragment 按键事件派发
     */
    internal fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            //这个子 Fragment 必须是 FlyActionFragment 的子类，并且处于可见状态
            if (fragment !is FlyActionFragment ||
                fragment.lifecycle.currentState != Lifecycle.State.RESUMED
            ) {
                continue
            }
            //将按键事件派发给子 Fragment 进行处理
            if (fragment.dispatchKeyEvent(event)) {
                //如果子 Fragment 拦截了这个事件，那么就不交给父 Fragment 处理
                return true
            }
        }
        return when (event?.action) {
            KeyEvent.ACTION_DOWN -> onKeyDown(event.keyCode, event)
            KeyEvent.ACTION_UP -> onKeyUp(event.keyCode, event)
            else -> false
        }
    }

    /**
     * 按键按下事件回调，默认不拦截按键事件
     */
    open fun onKeyDown(keyCode: Int, event: KeyEvent) = false

    /**
     * 按键抬起事件回调，默认不拦截按键事件
     */
    open fun onKeyUp(keyCode: Int, event: KeyEvent) = false
}