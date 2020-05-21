package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import io.reactivex.rxjava3.core.Observable

/**
 * 描述：先请求网络，请求网络失败后再加载缓存
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
@Suppress("unused")
class FirstRemoteStrategy : BaseStrategy() {

    override fun <T> execute(
        rxCache: RxCache,
        cacheKey: String?,
        cacheTime: Long,
        source: Observable<T>
    ): Observable<CacheResult<T>> {
        val cache: Observable<CacheResult<T>> = loadCache(rxCache, cacheKey, cacheTime, true)
        val remote: Observable<CacheResult<T>> = loadRemote(rxCache, cacheKey, source, false)
        return Observable
            .concatDelayError(listOf(remote, cache))
            .take(1)
    }
}