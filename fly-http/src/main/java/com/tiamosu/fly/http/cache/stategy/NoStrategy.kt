package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import io.reactivex.rxjava3.core.Observable

/**
 * 描述：网络加载，不缓存
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
@Suppress("unused")
class NoStrategy : IStrategy {

    override fun <T : Any> execute(
        rxCache: RxCache,
        cacheKey: String?,
        cacheTime: Long,
        source: Observable<T>
    ): Observable<CacheResult<T>> {
        return source.map { t -> CacheResult(false, t) }
    }
}