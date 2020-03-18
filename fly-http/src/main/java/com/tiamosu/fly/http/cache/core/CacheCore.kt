package com.tiamosu.fly.http.cache.core

import com.tiamosu.fly.http.utils.dLog
import okio.ByteString

/**
 * 描述：缓存核心管理类
 * 1.采用LruDiskCache
 * 2.对Key进行MD5加密
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
class CacheCore(private val disk: LruDiskCache) {

    /**
     * 读取
     */
    @Synchronized
    fun <T> load(key: String?, time: Long): T? {
        val cacheKey = key?.let { ByteString.of(*it.toByteArray()).md5().hex() }
        dLog("loadCache  key=$cacheKey")
        return disk.load(cacheKey, time)
    }

    /**
     * 保存
     */
    @Synchronized
    fun <T> save(key: String?, value: T): Boolean {
        val cacheKey = key?.let { ByteString.of(*it.toByteArray()).md5().hex() }
        dLog("saveCache  key=$cacheKey")
        return disk.save(cacheKey, value)
    }

    /**
     * 是否包含
     */
    @Synchronized
    fun containsKey(key: String?): Boolean {
        val cacheKey = key?.let { ByteString.of(*it.toByteArray()).md5().hex() }
        dLog("containsCache  key=$cacheKey")
        return disk.containsKey(cacheKey)
    }

    /**
     * 删除缓存
     */
    @Synchronized
    fun remove(key: String?): Boolean {
        val cacheKey = key?.let { ByteString.of(*it.toByteArray()).md5().hex() }
        dLog("removeCache  key=$cacheKey")
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