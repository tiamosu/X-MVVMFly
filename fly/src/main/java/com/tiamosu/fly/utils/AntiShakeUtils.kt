@file:JvmName("AntiShakeUtils")

package com.tiamosu.fly.utils

import android.view.View

/**
 * 描述：防止重复点击事件，默认0.2秒内不可重复点击
 *
 * @author tiamosu
 * @date 2020/3/18.
 */

private const val DEFAULT_DURATION = 200L
private const val TAG_KEY = 0x7EFFFFFF

@JvmOverloads
fun isValid(view: View, duration: Long = DEFAULT_DURATION): Boolean {
    val curTime = System.currentTimeMillis()
    val tag = view.getTag(TAG_KEY)
    if (tag !is Long) {
        view.setTag(TAG_KEY, curTime)
        return true
    }
    if (curTime - tag <= duration) return false
    view.setTag(TAG_KEY, curTime)
    return true
}