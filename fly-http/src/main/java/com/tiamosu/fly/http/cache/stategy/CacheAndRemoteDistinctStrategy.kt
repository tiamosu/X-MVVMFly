package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import io.reactivex.Observable
import okio.ByteString

/**
 * 描述：先显示缓存，再请求网络
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
@Suppress("unused")
class CacheAndRemoteDistinctStrategy : BaseStrategy() {

    override fun <T> execute(
        rxCache: RxCache,
        cacheKey: String?,
        cacheTime: Long,
        source: Observable<T>
    ): Observable<CacheResult<T>> {
        val cache: Observable<CacheResult<T>> = loadCache(rxCache, cacheKey, cacheTime, true)
        val remote: Observable<CacheResult<T>> = loadRemote(rxCache, cacheKey, source, false)
        return Observable.concat(cache, remote)
            .filter { cacheResult -> cacheResult.data != null }
            .distinctUntilChanged { cacheResult ->
                ByteString.of(*cacheResult.data.toString().toByteArray()).md5()
                    .hex()
            }
    }
}