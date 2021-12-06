package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import io.reactivex.rxjava3.core.Observable

/**
 * 描述：先使用缓存，不管是否存在，仍然请求网络，会回调两次
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
@Suppress("unused")
class CacheAndRemoteStrategy : BaseStrategy() {

    override fun <T : Any> execute(
        rxCache: RxCache,
        cacheKey: String?,
        cacheTime: Long,
        source: Observable<T>
    ): Observable<CacheResult<T>> {
        val cache: Observable<CacheResult<T>> = loadCache(rxCache, cacheKey, cacheTime, true)
        val remote: Observable<CacheResult<T>> = loadRemote(rxCache, cacheKey, source, false)
        return Observable.concat(cache, remote)
            .filter { cacheResult -> cacheResult.data != null }
    }
}