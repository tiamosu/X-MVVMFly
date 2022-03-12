package com.tiamosu.fly.http.utils

import com.blankj.utilcode.util.SPUtils

/**
 * @author tiamosu
 * @date 2020/11/24.
 */
internal object FlyCacheUtils {
    private val SP_UTILS = SPUtils.getInstance("FlyHttp")

    private fun putInt(key: String, value: Int) {
        SP_UTILS.put(key, value)
    }

    private fun getInt(key: String, defaultValue: Int = 0): Int {
        return SP_UTILS.getInt(key, defaultValue)
    }

    fun setDownloadStatus(key: String, downloadStatus: Int) {
        putInt(key, downloadStatus)
    }

    fun getDownloadStatus(key: String): Int {
        return getInt(key)
    }
}