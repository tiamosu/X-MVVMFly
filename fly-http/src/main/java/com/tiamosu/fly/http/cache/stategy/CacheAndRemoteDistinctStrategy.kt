package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import io.reactivex.rxjava3.core.Observable
import okio.ByteString

/**
 * 描述：先使用缓存，不管是否存在，仍然请求网络。
 * 有缓存先显示缓存，等网络请求数据回来后发现和缓存是一样的就不会再返回，否则数据不一样会继续返回。
 * （目的是为了防止数据是一致的也会刷新两次界面）
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
@Suppress("unused")
class CacheAndRemoteDistinctStrategy : BaseStrategy() {

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
            .distinctUntilChanged { cacheResult ->
                ByteString.of(*cacheResult.data.toString().toByteArray()).md5()
                    .hex()
            }
    }
}