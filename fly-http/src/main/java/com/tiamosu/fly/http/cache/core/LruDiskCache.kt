package com.tiamosu.fly.http.cache.core

import com.blankj.utilcode.util.CloseUtils
import com.jakewharton.disklrucache.DiskLruCache
import com.tiamosu.fly.http.cache.converter.IDiskConverter
import java.io.File
import java.io.IOException

/**
 * 描述：磁盘缓存实现类
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
class LruDiskCache(
    private val diskConverter: IDiskConverter,
    diskDir: File,
    appVersion: Int,
    diskMaxSize: Long
) : BaseCache() {
    private val diskLruCache by lazy { DiskLruCache.open(diskDir, appVersion, 1, diskMaxSize) }

    override fun <T> doLoad(key: String?): T? {
        diskLruCache ?: return null
        try {
            val edit = diskLruCache.edit(key) ?: return null
            val source = edit.newInputStream(0)
            val value: T?
            if (source != null) {
                value = diskConverter.load(source)
                CloseUtils.closeIO(source)
                edit.commit()
                return value
            }
            edit.abort()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun <T> doSave(key: String?, value: T): Boolean {
        diskLruCache ?: return false
        try {
            val edit = diskLruCache.edit(key) ?: return false
            val sink = edit.newOutputStream(0)
            if (sink != null) {
                val result = diskConverter.writer(sink, value)
                CloseUtils.closeIO(sink)
                edit.commit()
                return result
            }
            edit.abort()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun doContainsKey(key: String?): Boolean {
        diskLruCache ?: return false
        try {
            return diskLruCache.get(key) != null
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun doRemove(key: String?): Boolean {
        diskLruCache ?: return false
        try {
            return diskLruCache.remove(key)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun doClear(): Boolean {
        var state = false
        try {
            diskLruCache?.delete()
            state = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return state
    }

    override fun isExpiry(key: String?, existTime: Long): Boolean {
        diskLruCache ?: return false
        if (existTime > -1) { //-1表示永久性存储 不用进行过期校验
            //为什么这么写，请了解DiskLruCache，看它的源码
            val file = File(diskLruCache.directory, "$key.0")
            //没有获取到缓存,或者缓存已经过期!
            return isCacheDataFailure(file, existTime)
        }
        return false
    }

    /**
     * 判断缓存是否已经失效
     */
    private fun isCacheDataFailure(dataFile: File, time: Long): Boolean {
        if (!dataFile.exists()) {
            return false
        }
        val existTime = System.currentTimeMillis() - dataFile.lastModified()
        return existTime > time * 1000
    }
}