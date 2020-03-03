package com.tiamosu.fly.http.cache.core

import com.tiamosu.fly.http.utils.FlyHttpLog
import okio.ByteString
import java.lang.reflect.Type

/**
 * 描述：缓存核心管理类
 * 1.采用LruDiskCache
 * 2.对Key进行MD5加密
 *
 * @author tiamosu
 * @date 2020/2/29.
 */
class CacheCore(private val disk: LruDiskCache) {

    /**
     * 读取
     */
    @Synchronized
    fun <T> load(type: Type?, key: String, time: Long): T? {
        val cacheKey = ByteString.of(*key.toByteArray()).md5().hex()
        FlyHttpLog.d("loadCache  key=$cacheKey")
        return disk.load(type, cacheKey, time)
    }

    /**
     * 保存
     */
    @Synchronized
    fun <T> save(key: String, value: T): Boolean {
        val cacheKey = ByteString.of(*key.toByteArray()).md5().hex()
        FlyHttpLog.d("saveCache  key=$cacheKey")
        return disk.save(cacheKey, value)
    }

    /**
     * 是否包含
     *
     * @param key
     * @return
     */
    @Synchronized
    fun containsKey(key: String): Boolean {
        val cacheKey = ByteString.of(*key.toByteArray()).md5().hex()
        FlyHttpLog.d("containsCache  key=$cacheKey")
        return disk.containsKey(cacheKey)
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    @Synchronized
    fun remove(key: String): Boolean {
        val cacheKey = ByteString.of(*key.toByteArray()).md5().hex()
        FlyHttpLog.d("removeCache  key=$cacheKey")
        return disk.remove(cacheKey)
    }

    /**
     * 清空缓存
     */
    @Synchronized
    fun clear(): Boolean {
        return disk.clear()
    }
}