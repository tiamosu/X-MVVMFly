package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import io.reactivex.Observable
import java.lang.reflect.Type

/**
 * 描述：只请求网络
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
@Suppress("unused")
class OnlyRemoteStrategy : BaseStrategy() {

    override fun <T> execute(
        rxCache: RxCache,
        cacheKey: String,
        cacheTime: Long,
        source: Observable<T>,
        type: Type?
    ): Observable<CacheResult<T>> {
        return loadRemote(rxCache, cacheKey, source, false)
    }
}