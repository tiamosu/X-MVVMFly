package com.tiamosu.fly.http.cache.stategy

import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.model.CacheResult
import com.tiamosu.fly.http.utils.FlyHttpLog
import io.reactivex.rxjava3.core.Observable

/**
 * 描述：实现缓存策略的基类
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
abstract class BaseStrategy : IStrategy {

    fun <T : Any> loadCache(
        rxCache: RxCache,
        key: String?,
        time: Long,
        needEmpty: Boolean
    ): Observable<CacheResult<T>> {
        var observable = rxCache.load<T>(key, time)
            .flatMap { t ->
                Observable.just(CacheResult(true, t))
            }
        if (needEmpty) {
            observable = observable.onErrorResumeNext { Observable.empty() }
        }
        return observable
    }

    //请求成功后：同步保存
    fun <T : Any> loadRemote(
        rxCache: RxCache,
        key: String?,
        source: Observable<T>,
        needEmpty: Boolean
    ): Observable<CacheResult<T>> {
        var observable = source
            .flatMap { t ->
                rxCache.save(key, t)
                    .map { boolean ->
                        FlyHttpLog.iLog("save status => $boolean")
                        CacheResult(false, t)
                    }
                    .onErrorReturn { throwable ->
                        FlyHttpLog.iLog("save status => $throwable")
                        CacheResult(false, t)
                    }
            }
        if (needEmpty) {
            observable = observable.onErrorResumeNext { Observable.empty() }
        }
        return observable
    }
}