package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import io.reactivex.Observable
import okio.ByteString
import java.lang.reflect.Type

/**
 * 描述：先显示缓存，再请求网络
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
@Suppress("unused")
class CacheAndRemoteDistinctStrategy : BaseStrategy() {

    override fun <T> execute(
        rxCache: RxCache,
        cacheKey: String,
        cacheTime: Long,
        source: Observable<T>,
        type: Type?
    ): Observable<CacheResult<T>> {
        val cache: Observable<CacheResult<T>> = loadCache(rxCache, type, cacheKey, cacheTime, true)
        val remote = loadRemote(rxCache, cacheKey, source, false)
        return Observable.concat(cache, remote)
            .filter { cacheResult -> cacheResult.data != null }
            .distinctUntilChanged { cacheResult ->
                ByteString.of(*cacheResult.data.toString().toByteArray()).md5().hex()
            }
    }
}