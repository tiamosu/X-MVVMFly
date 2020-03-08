package com.tiamosu.fly.http.utils

import android.util.Log

/**
 * @author tiamosu
 * @date 2020/3/1.
 */
object FlyHttpLog {
    private var isLogEnable = true
    private var tag = "FlyHttp"

    @JvmStatic
    fun debug(isEnable: Boolean) {
        debug(tag, isEnable)
    }

    @JvmStatic
    fun debug(logTag: String, isEnable: Boolean) {
        this.tag = logTag
        this.isLogEnable = isEnable
    }

    @JvmStatic
    fun v(msg: String?) {
        v(tag, msg)
    }

    @JvmStatic
    fun v(tag: String?, msg: String?) {
        if (isLogEnable) Log.v(tag, msg ?: "")
    }

    @JvmStatic
    fun d(msg: String?) {
        d(tag, msg)
    }

    @JvmStatic
    fun d(tag: String?, msg: String?) {
        if (isLogEnable) Log.d(tag, msg ?: "")
    }

    @JvmStatic
    fun i(msg: String?) {
        i(tag, msg)
    }

    @JvmStatic
    fun i(tag: String?, msg: String?) {
        if (isLogEnable) Log.i(tag, msg ?: "")
    }

    @JvmStatic
    fun w(msg: String?) {
        w(tag, msg)
    }

    @JvmStatic
    fun w(tag: String?, msg: String?) {
        if (isLogEnable) Log.w(tag, msg ?: "")
    }

    @JvmStatic
    fun e(msg: String?) {
        e(tag, msg)
    }

    @JvmStatic
    fun e(tag: String?, msg: String?) {
        if (isLogEnable) Log.e(tag, msg ?: "")
    }

    @JvmStatic
    fun printStackTrace(t: Throwable?) {
        if (isLogEnable && t != null) t.printStackTrace()
    }
}