@file:JvmName("AntiShakeUtils")

package com.tiamosu.fly.utils

import android.view.View

/**
 * 描述：防止重复点击事件，默认0.2秒内不可重复点击
 *
 * @author tiamosu
 * @date 2020/3/18.
 */

private const val TAG_KEY = 0x7EFFFFFF

@JvmOverloads
fun isValid(view: View, duration: Long = 200L): Boolean {
    val curTime = System.currentTimeMillis()
    val preTime = view.getTag(TAG_KEY)
    if (preTime !is Long) {
        view.setTag(TAG_KEY, curTime)
        return true
    }
    if (curTime - preTime < 0) {
        view.setTag(TAG_KEY, curTime)
        return false
    } else if (curTime - preTime <= duration) {
        return false
    }
    view.setTag(TAG_KEY, curTime)
    return true
}