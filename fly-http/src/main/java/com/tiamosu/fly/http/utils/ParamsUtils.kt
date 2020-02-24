package com.tiamosu.fly.http.utils

import java.io.File

/**
 * @author tiamosu
 * @date 2018/10/8.
 *
 *
 * 用于处理空参数
 */
object ParamsUtils {

    @JvmStatic
    fun escapeParams(map: MutableMap<String, String?>?): Map<String, String> {
        if (map?.isNotEmpty() == true) {
            val mapTemp = hashMapOf<String, String>()
            for ((key, value) in map) {
                mapTemp[key] = value ?: ""
            }
            return mapTemp
        }
        return mapOf()
    }

    @JvmStatic
    fun escapeFileParams(map: MutableMap<String, File?>?): Map<String, File> {
        if (map?.isNotEmpty() == true) {
            val mapTemp = hashMapOf<String, File>()
            for ((key, value) in map) {
                value ?: continue
                mapTemp[key] = value
            }
            return mapTemp
        }
        return mapOf()
    }
}
