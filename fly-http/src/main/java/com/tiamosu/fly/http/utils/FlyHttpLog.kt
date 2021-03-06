@file:JvmName("FlyHttpLog")

package com.tiamosu.fly.http.utils

import android.util.Log

/**
 * @author tiamosu
 * @date 2020/3/18.
 */
object FlyHttpLog {
    private var isLogEnable = false
    private var tag = "FlyHttp"

    fun debugLog(isEnable: Boolean) {
        debugLog(tag, isEnable)
    }

    fun debugLog(logTag: String, isEnable: Boolean) {
        tag = logTag
        isLogEnable = isEnable
    }

    fun vLog(msg: String?) {
        vLog(tag, msg)
    }

    fun vLog(tag: String?, msg: String?) {
        if (isLogEnable) Log.v(tag, msg ?: "")
    }

    fun dLog(msg: String?) {
        dLog(tag, msg)
    }

    fun dLog(tag: String?, msg: String?) {
        if (isLogEnable) Log.d(tag, msg ?: "")
    }

    fun iLog(msg: String?) {
        iLog(tag, msg)
    }

    fun iLog(tag: String?, msg: String?) {
        if (isLogEnable) Log.i(tag, msg ?: "")
    }

    fun wLog(msg: String?) {
        wLog(tag, msg)
    }

    fun wLog(tag: String?, msg: String?) {
        if (isLogEnable) Log.w(tag, msg ?: "")
    }

    fun eLog(msg: String?) {
        eLog(tag, msg)
    }

    fun eLog(tag: String?, msg: String?) {
        if (isLogEnable) Log.e(tag, msg ?: "")
    }

    fun printStackTraceLog(t: Throwable?) {
        if (isLogEnable && t != null) t.printStackTrace()
    }
}