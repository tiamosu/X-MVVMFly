@file:JvmName("AntiShakeUtils")

package com.tiamosu.fly.utils

/**
 * 描述：防止重复点击事件，默认0.2秒内不可重复点击
 *
 * @author tiamosu
 * @date 2020/3/18.
 */
private var lastClickTime = 0L

@JvmOverloads
fun isValid(duration: Long = 500): Boolean {
    val currentTime = System.currentTimeMillis()
    val isFastClick = currentTime - lastClickTime <= duration
    lastClickTime = currentTime
    return !isFastClick
}