package com.tiamosu.fly.http.utils

/**
 * @author tiamosu
 * @date 2018/10/8.
 *
 *
 * 用于处理空参数
 */
object ParamsUtils {

    @JvmStatic
    fun <V> escapeParams(map: Map<String?, V?>?): Map<String, V> {
        if (map == null || map.isEmpty()) {
            return mapOf()
        }
        val hashMap: LinkedHashMap<String, V> = linkedMapOf()
        for ((key, value) in map) {
            if (key != null && value != null) {
                hashMap[key] = value
            }
        }
        return hashMap
    }
}
