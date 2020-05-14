package com.tiamosu.fly.core.ext

import android.view.View
import com.tiamosu.fly.utils.isValid

/**
 * 防止重复点击事件，默认0.5秒内不可重复点击
 */
fun View.clickNoRepeat(
    interval: Long = 500,
    block: (view: View) -> Unit
) {
    setOnClickListener {
        if (isValid(it, interval)) {
            block.invoke(it)
        }
    }
}

/**
 * 防止重复点击事件，默认0.5秒内不可重复点击
 */
fun clickNoRepeat(
    interval: Long = 500,
    vararg views: View,
    block: (view: View) -> Unit
) {
    views.forEach {
        it.clickNoRepeat(interval, block)
    }
}