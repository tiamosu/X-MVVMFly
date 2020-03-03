package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import io.reactivex.Observable
import java.lang.reflect.Type

/**
 * 描述：先请求网络，网络请求失败，再加载缓存
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
@Suppress("unused")
class FirstRemoteStrategy : BaseStrategy() {

    override fun <T> execute(
        rxCache: RxCache,
        cacheKey: String,
        cacheTime: Long,
        source: Observable<T>,
        type: Type?
    ): Observable<CacheResult<T>> {
        val cache: Observable<CacheResult<T>> = loadCache(rxCache, type, cacheKey, cacheTime, true)
        val remote: Observable<CacheResult<T>> = loadRemote(rxCache, cacheKey, source, false)
        return Observable
            .concatDelayError<CacheResult<T>>(listOf(remote, cache))
            .take(1)
    }
}