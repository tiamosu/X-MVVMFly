package com.tiamosu.fly.http.utils

import com.blankj.utilcode.util.SPUtils

/**
 * @author tiamosu
 * @date 2020/11/24.
 */
object CacheUtils {
    private val SP_UTILS = SPUtils.getInstance("FlyHttp")

    private fun putBoolean(key: String, value: Boolean) {
        SP_UTILS.put(key, value)
    }

    private fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return SP_UTILS.getBoolean(key, defaultValue)
    }

    fun setDownloadComplete(url: String) {
        putBoolean(url, true)
    }

    fun isDownloadComplete(url: String) {
        getBoolean(url)
    }
}