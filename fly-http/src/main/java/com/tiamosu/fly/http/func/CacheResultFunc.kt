package com.tiamosu.fly.http.func

import com.tiamosu.fly.http.cache.model.CacheResult
import io.reactivex.functions.Function

/**
 * 描述：缓存结果转换
 *
 * @author tiamosu
 * @date 2020/3/3.
 */
class CacheResultFunc<T> : Function<CacheResult<T>, T> {

    @Throws(Exception::class)
    override fun apply(tCacheResult: CacheResult<T>): T? {
        return tCacheResult.data
    }
}