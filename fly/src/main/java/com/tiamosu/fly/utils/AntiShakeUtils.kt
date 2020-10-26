@file:JvmName("AntiShakeUtils")

package com.tiamosu.fly.utils

import android.os.SystemClock
import android.view.View
import java.util.concurrent.ConcurrentHashMap

/**
 * 描述：防止重复点击事件，默认0.2秒内不可重复点击
 *
 * @author tiamosu
 * @date 2020/3/18.
 */
private const val CACHE_SIZE = 64
private val KEY_MILLIS_MAP: MutableMap<String, Long> = ConcurrentHashMap(CACHE_SIZE)

fun View.isValid(duration: Long = 500): Boolean {
    val curTime = SystemClock.elapsedRealtime()
    clearIfNecessary(curTime)
    val key = this.hashCode().toString()
    val validTime = KEY_MILLIS_MAP[key]
    val isFastClick = validTime != null && curTime - validTime <= duration
    KEY_MILLIS_MAP[key] = curTime
    return !isFastClick
}

private fun clearIfNecessary(curTime: Long) {
    if (KEY_MILLIS_MAP.size < CACHE_SIZE) return
    val it: MutableIterator<Map.Entry<String, Long>> = KEY_MILLIS_MAP.entries.iterator()
    while (it.hasNext()) {
        val entry = it.next()
        val validTime = entry.value
        if (curTime >= validTime) {
            it.remove()
        }
    }
}