package com.tiamosu.fly.base.action

import android.os.Handler
import android.os.Looper
import android.os.SystemClock

/**
 * @author tiamosu
 * @date 2021/2/24.
 */
interface HandlerAction {

    /**
     * 获取 Handler
     */
    val handler get() = HANDLER

    /**
     * 延迟执行
     */
    fun post(r: Runnable): Boolean {
        return postDelayed(r, 0)
    }

    /**
     * 延迟一段时间执行
     */
    fun postDelayed(r: Runnable, delayMillis: Long): Boolean {
        var uptimeMillis = delayMillis
        if (uptimeMillis < 0) {
            uptimeMillis = 0
        }
        return postAtTime(r, SystemClock.uptimeMillis() + uptimeMillis)
    }

    /**
     * 在指定的时间执行
     */
    fun postAtTime(r: Runnable, uptimeMillis: Long): Boolean {
        // 发送和当前对象相关的消息回调
        return HANDLER.postAtTime(r, this, uptimeMillis)
    }

    /**
     * 移除单个消息回调
     */
    fun removeCallbacks(r: Runnable) {
        HANDLER.removeCallbacks(r)
    }

    /**
     * 移除全部消息回调
     */
    fun removeCallbacks() {
        // 移除和当前对象相关的消息回调
        HANDLER.removeCallbacksAndMessages(this)
    }

    companion object {
        val HANDLER = Handler(Looper.getMainLooper())
    }
}