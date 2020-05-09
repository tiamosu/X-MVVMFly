package com.tiamosu.fly.integration.cache

import android.content.Context
import com.tiamosu.fly.utils.activityManager

/**
 * 构建 [Cache] 时,使用 [CacheType] 中声明的类型,来区分不同的模块
 * 从而为不同的模块构建不同的缓存策略
 *
 * @author tiamosu
 * @date 2018/9/14.
 * @see Cache.Factory.build
 */
interface CacheType {

    /**
     * 返回框架内需要缓存的模块对应的 `id`
     */
    fun getCacheTypeId(): Int

    /**
     * 计算对应模块需要的缓存大小
     */
    fun calculateCacheSize(context: Context): Int

    @Suppress("unused")
    companion object {
        const val RETROFIT_SERVICE_CACHE_TYPE_ID = 0
        const val EXTRAS_TYPE_ID = 1

        val RETROFIT_SERVICE_CACHE: CacheType = object : CacheType {
            private val MAX_SIZE = 150
            private val MAX_SIZE_MULTIPLIER = 0.002f

            override fun getCacheTypeId(): Int {
                return RETROFIT_SERVICE_CACHE_TYPE_ID
            }

            override fun calculateCacheSize(context: Context): Int {
                val targetMemoryCacheSize =
                    (activityManager?.memoryClass?.toFloat()
                        ?: 0F * MAX_SIZE_MULTIPLIER * 1024f).toInt()
                return if (targetMemoryCacheSize >= MAX_SIZE) {
                    MAX_SIZE
                } else targetMemoryCacheSize
            }
        }

        val EXTRAS: CacheType = object : CacheType {
            private val MAX_SIZE = 500
            private val MAX_SIZE_MULTIPLIER = 0.005f

            override fun getCacheTypeId(): Int {
                return EXTRAS_TYPE_ID
            }

            override fun calculateCacheSize(context: Context): Int {
                val targetMemoryCacheSize =
                    (activityManager?.memoryClass?.toFloat()
                        ?: 0F * MAX_SIZE_MULTIPLIER * 1024f).toInt()
                return if (targetMemoryCacheSize >= MAX_SIZE) {
                    MAX_SIZE
                } else targetMemoryCacheSize
            }
        }
    }
}
