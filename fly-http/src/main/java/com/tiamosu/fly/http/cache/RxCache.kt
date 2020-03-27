package com.tiamosu.fly.http.cache

import android.os.StatFs
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.cache.converter.IDiskConverter
import com.tiamosu.fly.http.cache.converter.SerializableDiskConverter
import com.tiamosu.fly.http.cache.core.CacheCore
import com.tiamosu.fly.http.cache.core.LruDiskCache
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.cache.model.CacheResult
import com.tiamosu.fly.http.cache.stategy.IStrategy
import com.tiamosu.fly.http.utils.eLog
import com.tiamosu.fly.http.utils.iLog
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableTransformer
import io.reactivex.exceptions.Exceptions
import java.io.File

/**
 * 描述：缓存统一入口类
 * 主要实现技术：Rxjava+DiskLruCache(jakewharton大神开源的LRU库)
 *
 * 主要功能：
 * 1.可以独立使用，单独用RxCache来存储数据
 * 2.采用transformer与网络请求结合，可以实现网络缓存功能,本地硬缓存
 * 3.可以保存缓存（异步）
 * 4.可以读取缓存（异步）
 * 5.可以判断缓存是否存在
 * 6.根据key删除缓存
 * 7.清空缓存（异步）
 * 8.缓存Key会自动进行MD5加密
 * 9.其它参数设置：缓存磁盘大小、缓存key、缓存时间、缓存存储的转换器、缓存目录、缓存Version
 *
 * 使用说明：
 * RxCache rxCache = new RxCache.Builder(this)
 *  .appVersion(1)//不设置，默认为1
 *  .diskDir(new File(getCacheDir().getPath() + File.separator + "data-cache"))//不设置，默认使用缓存路径
 *  .diskConverter(new SerializableDiskConverter())//目前只支持Serializable缓存
 *  .diskMax(20*1024*1024)//不设置， 默为认50MB
 *  .build();
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
class RxCache {
    var cacheCore: CacheCore? = null    //缓存的核心管理类
    val cacheKey: String?               //缓存的key
    val cacheTime: Long                 //缓存的时间 单位:秒
    val diskConverter: IDiskConverter?  //缓存的转换器
    val diskDir: File?                  //缓存的磁盘目录，默认是缓存目录
    val appVersion: Int                 //缓存的版本
    val diskMaxSize: Long               //缓存的磁盘大小

    constructor() : this(Builder())

    constructor(builder: Builder) {
        cacheKey = builder.cachekey
        cacheTime = builder.cacheTime
        diskDir = builder.diskDir
        appVersion = builder.appVersion
        diskMaxSize = builder.diskMaxSize
        diskConverter = builder.diskConverter

        if (diskConverter != null && diskDir != null) {
            cacheCore = CacheCore(LruDiskCache(diskConverter, diskDir, appVersion, diskMaxSize))
        }
    }

    fun newBuilder(): Builder {
        return Builder(this)
    }

    /**
     * 缓存transformer
     *
     * @param cacheMode 缓存类型
     */
    fun <T> transformer(cacheMode: CacheMode): ObservableTransformer<T, CacheResult<T>> {
        val strategy = loadStrategy(cacheMode) //获取缓存策略
        return ObservableTransformer { upstream ->
            iLog("cacheKey=$cacheKey")
            strategy.execute(
                this@RxCache,
                cacheKey,
                cacheTime,
                upstream
            )
        }
    }

    private abstract class SimpleSubscribe<T> : ObservableOnSubscribe<T> {
        @Throws(Exception::class)
        override fun subscribe(subscriber: ObservableEmitter<T>) {
            try {
                val data = execute()
                if (!subscriber.isDisposed) {
                    if (data == null) {
                        subscriber.onError(NullPointerException())
                        return
                    }
                    subscriber.onNext(data)
                }
            } catch (e: Throwable) {
                eLog(e.message)
                if (!subscriber.isDisposed) {
                    subscriber.onError(e)
                }
                Exceptions.throwIfFatal(e)
                return
            }
            if (!subscriber.isDisposed) {
                subscriber.onComplete()
            }
        }

        @Throws(Throwable::class)
        abstract fun execute(): T?
    }

    /**
     * 获取缓存
     *
     * @param key 缓存key
     */
    fun <T> load(key: String?): Observable<T> {
        return load(key, -1)
    }

    /**
     * 根据时间读取缓存
     *
     * @param key  缓存key
     * @param time 保存时间
     */
    fun <T> load(key: String?, time: Long): Observable<T> {
        return Observable.create(object : SimpleSubscribe<T>() {
            override fun execute(): T? {
                return cacheCore?.load(key, time)
            }
        })
    }

    /**
     * 保存
     *
     * @param key   缓存key
     * @param value 缓存Value
     */
    fun <T> save(key: String?, value: T): Observable<Boolean> {
        return Observable.create(object : SimpleSubscribe<Boolean>() {
            @Throws(Throwable::class)
            override fun execute(): Boolean {
                cacheCore?.save(key, value)
                return true
            }
        })
    }

    /**
     * 是否包含
     */
    fun containsKey(key: String?): Observable<Boolean> {
        return Observable.create(object : SimpleSubscribe<Boolean>() {
            @Throws(Throwable::class)
            override fun execute(): Boolean {
                return cacheCore?.containsKey(key) ?: return false
            }
        })
    }

    /**
     * 删除缓存
     */
    fun remove(key: String?): Observable<Boolean> {
        return Observable.create(object : SimpleSubscribe<Boolean>() {
            @Throws(Throwable::class)
            override fun execute(): Boolean {
                return cacheCore?.remove(key) ?: return false
            }
        })
    }

    /**
     * 清空缓存
     */
    fun clear(): Observable<Boolean> {
        return Observable.create(object : SimpleSubscribe<Boolean>() {
            @Throws(Throwable::class)
            override fun execute(): Boolean {
                return cacheCore?.clear() ?: return false
            }
        })
    }

    /**
     * 利用反射，加载缓存策略模型
     */
    private fun loadStrategy(cacheMode: CacheMode): IStrategy {
        return try {
            val pkName = IStrategy::class.java.getPackage()?.name
            Class.forName(pkName + "." + cacheMode.className).newInstance() as IStrategy
        } catch (e: Exception) {
            throw RuntimeException("loadStrategy(" + cacheMode + ") err!!" + e.message)
        }
    }

    class Builder {
        var appVersion: Int
        var diskMaxSize = 0L
        var diskDir: File? = null
        var diskConverter: IDiskConverter?
        var cachekey: String? = null
        var cacheTime: Long

        constructor() {
            diskConverter = SerializableDiskConverter()
            cacheTime = CACHE_NEVER_EXPIRE
            appVersion = 1
        }

        constructor(rxCache: RxCache) {
            appVersion = rxCache.appVersion
            diskMaxSize = rxCache.diskMaxSize
            diskDir = rxCache.diskDir
            diskConverter = rxCache.diskConverter
            cachekey = rxCache.cacheKey
            cacheTime = rxCache.cacheTime
        }

        fun init(): Builder {
            return this
        }

        /**
         * 不设置，默认为1
         */
        fun appVersion(appVersion: Int): Builder {
            this.appVersion = appVersion
            return this
        }

        /**
         * 默认为缓存路径
         *
         * @param directory
         * @return
         */
        fun diskDir(directory: File?): Builder {
            diskDir = directory
            return this
        }

        fun diskConverter(converter: IDiskConverter?): Builder {
            diskConverter = converter
            return this
        }

        /**
         * 不设置， 默为认50MB
         */
        fun diskMax(maxSize: Long): Builder {
            diskMaxSize = maxSize
            return this
        }

        fun cacheKey(cacheKey: String?): Builder {
            this.cachekey = cacheKey
            return this
        }

        fun cacheTime(cacheTime: Long): Builder {
            this.cacheTime = cacheTime
            return this
        }

        fun build(): RxCache {
            diskDir = diskDir ?: FlyHttp.getCacheDirectory()
            diskConverter = diskConverter ?: SerializableDiskConverter()
            if (diskMaxSize <= 0 && diskDir != null) {
                diskMaxSize = calculateDiskCacheSize(diskDir!!)
            }
            cacheTime = CACHE_NEVER_EXPIRE.coerceAtLeast(cacheTime)
            appVersion = 1.coerceAtLeast(appVersion)
            return RxCache(this)
        }

        companion object {
            private const val MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024   // 5MB
            private const val MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024  // 50MB
            private const val CACHE_NEVER_EXPIRE = -1L                //永久不过期

            @Suppress("DEPRECATION")
            private fun calculateDiskCacheSize(dir: File): Long {
                var size: Long = 0
                try {
                    val statFs = StatFs(dir.absolutePath)
                    val available = statFs.blockCount.toLong() * statFs.blockSize
                    size = available / 50
                } catch (ignored: IllegalArgumentException) {
                }
                return size.coerceAtMost(MAX_DISK_CACHE_SIZE.toLong())
                    .coerceAtLeast(MIN_DISK_CACHE_SIZE.toLong())
            }
        }
    }
}