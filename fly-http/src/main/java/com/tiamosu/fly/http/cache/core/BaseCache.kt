package com.tiamosu.fly.http.cache.core

import java.lang.reflect.Type
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * 描述：缓存的基类
 * 1.所有缓存处理都继承该基类
 * 2.增加了锁机制，防止频繁读取缓存造成的异常
 * 3.子类直接考虑具体的实现细节就可以了
 *
 * @author tiamosu
 * @date 2020/2/29.
 */
abstract class BaseCache {

    private val readWriteLock: ReadWriteLock by lazy { ReentrantReadWriteLock() }

    /**
     * 读取缓存
     *
     * @param key       缓存key
     * @param existTime 缓存时间
     */
    fun <T> load(type: Type?, key: String, existTime: Long): T? {
        //判断key是否存在,key不存在去读缓存没意义
        if (!containsKey(key)) {
            return null
        }
        //判断是否过期，过期自动清理
        if (isExpiry(key, existTime)) {
            remove(key)
            return null
        }
        //开始真正的读取缓存
        readWriteLock.readLock().lock()
        return try { // 读取缓存
            doLoad(type, key)
        } finally {
            readWriteLock.readLock().unlock()
        }
    }

    /**
     * 保存缓存
     *
     * @param key   缓存key
     * @param value 缓存内容
     */
    fun <T> save(key: String, value: T?): Boolean {
        //如果要保存的值为空,则删除
        value ?: return remove(key)
        //写入缓存
        readWriteLock.writeLock().lock()
        return try {
            doSave(key, value)
        } finally {
            readWriteLock.writeLock().unlock()
        }
    }

    /**
     * 删除缓存
     */
    fun remove(key: String): Boolean {
        readWriteLock.writeLock().lock()
        return try {
            doRemove(key)
        } finally {
            readWriteLock.writeLock().unlock()
        }
    }

    /**
     * 清空缓存
     */
    fun clear(): Boolean {
        readWriteLock.writeLock().lock()
        return try {
            doClear()
        } finally {
            readWriteLock.writeLock().unlock()
        }
    }

    /**
     * 是否包含 加final 是让子类不能被重写，只能使用doContainsKey
     * 这里加了锁处理，操作安全
     *
     * @param key 缓存key
     * @return 是否有缓存
     */
    fun containsKey(key: String): Boolean {
        readWriteLock.readLock().lock()
        return try {
            doContainsKey(key)
        } finally {
            readWriteLock.readLock().unlock()
        }
    }

    /**
     * 是否包含  采用protected修饰符  被子类修改
     */
    protected abstract fun doContainsKey(key: String): Boolean

    /**
     * 是否过期
     */
    protected abstract fun isExpiry(key: String, existTime: Long): Boolean

    /**
     * 读取缓存
     */
    protected abstract fun <T> doLoad(type: Type?, key: String): T?

    /**
     * 保存
     */
    protected abstract fun <T> doSave(key: String, value: T): Boolean

    /**
     * 删除缓存
     */
    protected abstract fun doRemove(key: String): Boolean

    /**
     * 清空缓存
     */
    protected abstract fun doClear(): Boolean
}