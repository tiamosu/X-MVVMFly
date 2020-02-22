package com.tiamosu.fly.utils

import android.view.View

import androidx.annotation.IntRange

/**
 * @author tiamosu
 * @date 2019/6/20.
 */
object AntiShakeUtils {
    private const val DEFAULT_DURATION: Long = 200
    private const val TAG_KEY = 0x7EFFFFFF

    @JvmStatic
    @JvmOverloads
    fun isValid(view: View, @IntRange(from = 0) duration: Long = DEFAULT_DURATION): Boolean {
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
}
