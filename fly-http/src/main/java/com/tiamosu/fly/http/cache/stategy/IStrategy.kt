package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import io.reactivex.Observable

/**
 * 描述：实现缓存策略的接口，可以自定义缓存实现方式，只要实现该接口就可以了
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
interface IStrategy {

    /**
     * 执行缓存
     *
     * @param rxCache   缓存管理对象
     * @param cacheKey  缓存key
     * @param cacheTime 缓存时间
     * @param source    网络请求对象
     * @return 返回带缓存的Observable流对象
     */
    fun <T> execute(
        rxCache: RxCache,
        cacheKey: String?,
        cacheTime: Long,
        source: Observable<T>
    ): Observable<CacheResult<T>>
}