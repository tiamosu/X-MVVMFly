package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import io.reactivex.Observable

/**
 * 描述：只读取缓存
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
@Suppress("unused")
class OnlyCacheStrategy : BaseStrategy() {

    override fun <T> execute(
        rxCache: RxCache,
        cacheKey: String?,
        cacheTime: Long,
        source: Observable<T>
    ): Observable<CacheResult<T>> {
        return loadCache(rxCache, cacheKey, cacheTime, false)
    }
}